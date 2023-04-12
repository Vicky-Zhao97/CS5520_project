package com.example.loseit.ui.home;

import static com.example.loseit.StartActivity.DB_USER_INFO_PATH;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.loseit.MainViewModel;
import com.example.loseit.R;
import com.example.loseit.databinding.FragmentHomeBinding;
import com.example.loseit.databinding.LayoutEatKcalBinding;
import com.example.loseit.model.DailyWeight;
import com.example.loseit.model.Diet;
import com.example.loseit.model.DietItem;
import com.example.loseit.model.UserInfo;
import com.example.loseit.ui.diet.DietCard;
import com.example.loseit.ui.diet.DietItemDialog;
import com.example.loseit.ui.diet.DietItemList;
import com.example.loseit.ui.user_info.BirthPanelViewSetting;
import com.github.mikephil.charting.data.Entry;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public class HomeFragment extends Fragment {
    //tag for showing record weight dialog
    public static final String TAG_RECORD_WEIGHT_DIALOG = "record weight";
    private FragmentHomeBinding binding;
    //manage the chart to keep HomeFragment simple
    private ProcessChartManager chartManager;
    //loading dialog
    private LoadingDialog loadingDialog;
    //view model
    private MainViewModel mainViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        chartManager = new ProcessChartManager(binding.chartProgress,
                binding.getRoot().getContext());
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        initView();
        return binding.getRoot();
    }

    /**
     * init view
     */
    private void initView() {
        //init break, lunch and dinner cards
        binding.dietBreakfast.setFragmentManager(getParentFragmentManager());
        binding.dietLunch.setFragmentManager(getParentFragmentManager());
        binding.dietDinner.setFragmentManager(getParentFragmentManager());

        //init loading dialog
        loadingDialog = new LoadingDialog(binding.getRoot().getContext(),
                getString(R.string.load_user_data));

        //watch eat kcal change
        binding.dietBreakfast.setTotalKcalChangeListener(totalKcal -> {
            mainViewModel.updateEatKcal(totalKcal, MainViewModel.DIET_BREAKFAST);
        });
        binding.dietLunch.setTotalKcalChangeListener(totalKcal -> {
            mainViewModel.updateEatKcal(totalKcal, MainViewModel.DIET_LUNCH);
        });
        binding.dietDinner.setTotalKcalChangeListener(totalKcal -> {
            mainViewModel.updateEatKcal(totalKcal, MainViewModel.DIET_DINNER);
        });

        //observe user information change and refresh views when it has changed
        LifecycleOwner activity = requireActivity();
        mainViewModel.observeUserInfo().observe(activity, this::showUserInfo);

        //observe eat kcal change and refresh view
        mainViewModel.observeEatKcal().observe(activity, integer -> {
            //refresh eat kcal
            binding.kcalEat.tvEatKcalValue.setText(String.format(Locale.ENGLISH,
                    "%d Kcal", integer));
        });

        //observe left kcal change and refresh view
        mainViewModel.observeLeftKcal().observe(activity, integer -> {
            //show left kcal
            binding.kcalLeft.tvLeftKcalValue.setText(String.format(Locale.ENGLISH,
                    "%d Kcal", integer));
            Context context=binding.getRoot().getContext();
            if (integer < 0) {
                //warn when left kcal is less than 0
                binding.kcalLeft.leftKcalContainer.setBackground(AppCompatResources.getDrawable(
                        context , R.drawable.red_round_stroke
                ));
                binding.kcalLeft.tvLeftKcalName.setTextColor(context.getColor(R.color.error_msg_bg));
            } else {
                binding.kcalLeft.leftKcalContainer.setBackground(AppCompatResources.getDrawable(
                        binding.getRoot().getContext(), R.drawable.green_round_stroke
                ));
                binding.kcalLeft.tvLeftKcalName.setTextColor(context.getColor(R.color.primary_green));
            }
        });

        //next diet
        binding.btnDietNext.setOnClickListener(view -> {
            syncCurrentShowDiet();
            Diet diet = mainViewModel.nextDiet();
            if (diet == null) {
                return;
            }
            showDiet(diet);
        });
        //last diet
        binding.btnLastDiet.setOnClickListener(view -> {
            syncCurrentShowDiet();
            Diet diet = mainViewModel.lastDiet();
            if (diet == null) {
                return;
            }
            showDiet(diet);
        });
        //record weight
        binding.buttonHomeRecordWeight.setOnClickListener(view -> recordWeight());
    }

    /**
     * record weight
     */
    private void recordWeight() {
        RecordWeightDialog dialog = new RecordWeightDialog(mainViewModel.getCurrentWeight());
        dialog.show(getParentFragmentManager(), TAG_RECORD_WEIGHT_DIALOG);
        //save weight when user click confirm button and close dialog
        dialog.setOnCloseListener(dailyWeight -> {
            mainViewModel.recordDailyWeight(dailyWeight);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        //fetch user information
        loadingDialog.show();
        mainViewModel.fetchUserInfo((userInfo, successful) -> {
            loadingDialog.hide();
            if (!successful) {
                //show error message
                showMsg(getString(R.string.error_msg_fecth_user_info),
                        binding.getRoot().getContext().getColor(R.color.error_msg_bg));
            }
        });
    }

    /**
     * sync diet item list in the diet card view with current showing diet
     */
    private void syncCurrentShowDiet() {
        //sync  diet item list in the view to diet in main view model
        Diet diet = mainViewModel.getCurrentShowDiet();
        diet.setBreakFast(binding.dietBreakfast.getDietItemList());
        diet.setLunch(binding.dietLunch.getDietItemList());
        diet.setDinner(binding.dietDinner.getDietItemList());
    }

    @Override
    public void onPause() {
        super.onPause();
        syncCurrentShowDiet();
        //sync local user info with firebase
        mainViewModel.setUserInfo(mainViewModel.getUserInfo());
    }


    /**
     * show diet
     *
     * @param diet Diet
     */
    private void showDiet(Diet diet) {
        //clear diet item list
        binding.dietBreakfast.clear();
        binding.dietLunch.clear();
        binding.dietDinner.clear();
        //add diet item to list
        binding.dietBreakfast.addDietItems(diet.getBreakFast());
        binding.dietLunch.addDietItems(diet.getLunch());
        binding.dietDinner.addDietItems(diet.getDinner());

        //show date of current showing diet
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(diet.getCreateAt());
        int mon = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);
        String month = BirthPanelViewSetting.moths[mon];
        String today = "";
        if (Diet.isDietAtDate(diet, Calendar.getInstance().getTime())) {
            today = "Today";
        }
        //show date of diet
        binding.tvDietDate.setText(String.format(Locale.ENGLISH,
                "%s %s %d, %d", today, month, day, year));
    }

    /**
     * bind data with view
     *
     * @param userInfo data fetched from database
     */
    private void showUserInfo(UserInfo userInfo) {
        if (userInfo == null) {
            return;
        }
        //show weight chart
        chartManager.showChartOfWeight(userInfo);
        //get current showing diet
        Diet diet = mainViewModel.getCurrentShowDiet();
        //show current weight
        binding.tvHomeCurrentWeight.setText(String.format(Locale.ENGLISH,
                "Current weight: %.2f kg",
                mainViewModel.getCurrentWeight()));
        //show diet
        showDiet(diet);
    }

    /**
     * show message
     *
     * @param msg message content
     */
    private void showMsg(String msg, int color) {
        Snackbar snackbar = Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_SHORT);
        View mView = snackbar.getView();
        mView.setBackgroundColor(color);
        snackbar.show();
    }
}