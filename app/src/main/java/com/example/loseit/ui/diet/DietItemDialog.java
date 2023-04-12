package com.example.loseit.ui.diet;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.loseit.MainViewModel;
import com.example.loseit.R;
import com.example.loseit.databinding.DialogDietItemBinding;
import com.example.loseit.model.DietItem;
import com.example.loseit.model.Food;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

/**
 * dialog for add and edit diet item
 */
public class DietItemDialog extends DialogFragment {
    //key of argument passed from out scope
    public static final String KEY_DIET_ITEM = "diet item";
    //view binding
    private DialogDietItemBinding binding;
    //diet item current edit
    private DietItem dietItem;
    //listener for watching window close
    private OnCloseListener onCloseListener;
    //adapter for food matching list
//    private ArrayAdapter<String> adapter;
    //view model
    private MainViewModel mainViewModel;
    private final ArrayList<Food> foodList = new ArrayList<>();
    Observer<ArrayList<Food>> foodListObserver;
    Observer<String> KcalObserver;


    /**
     * listener for watching dialog closed
     */
    public interface OnCloseListener {
        void onClose(DietItem dietItem);
    }

    /**
     * set listener for watching dialog closed
     *
     * @param onCloseListener OnCloseListener
     */
    public void setOnCloseListener(OnCloseListener onCloseListener) {
        this.onCloseListener = onCloseListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //view binding
        binding = DialogDietItemBinding.inflate(inflater, container, false);
        //disable auto cancel
        setCancelable(false);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        //cancel button listener
        binding.buttonDietDialogCancel.setOnClickListener(view -> {
            dismiss();
        });
        //confirm button listener
        binding.buttonDietDialogConfirm.setOnClickListener(view -> saveData());
        //clear error message when user change input
        binding.editDietDialogName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }


            @Override
            public void afterTextChanged(Editable editable) {
                binding.tvDietDialogMsg.setText("");
                //fetch food list matched from database
//                mainViewModel.fetchFood(editable.toString());
            }
        });
        binding.editDietDialogName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    String foodName = binding.editDietDialogName.getText().toString();
                    if (TextUtils.isEmpty(foodName.trim()))
                        return;
                    mainViewModel.fetchKcal(foodName);
                }
            }
        });

        //clear error message when user change input
        binding.editDietDialogKcal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.tvDietDialogMsg.setText("");
            }
        });
        // set adapter for auto match list of food name input widget
//        adapter = new ArrayAdapter<>(binding.getRoot().getContext(),
//                android.R.layout.simple_list_item_1, new ArrayList<>());
//        binding.editDietDialogName.setAdapter(adapter);
        //observe food list change and update auto matched food list
//        foodListObserver = foods -> {
//            if(foods!=null){
//                adapter.clear();
//                foodList.clear();
//                for (Food food : foods) {
//                    foodList.add(food);
//                    //update adapter
//                    adapter.add(food.getName());
//                }
//            }
//        };
//        mainViewModel.observeFoodLiveData().observe(requireActivity(), foodListObserver);
//        binding.editDietDialogName.setOnItemClickListener((adapterView, view, position, id) -> {
//            Food food = foodList.get(position);
//            //show food calories
//            binding.editDietDialogKcal.setText(String.format(Locale.ENGLISH,
//                    "%.2f", food.getCaloriePerOunce()));
//        });
        KcalObserver = kcal->{
            binding.editDietDialogKcal.setText(kcal);
            if (!TextUtils.isEmpty(kcal)){
                binding.editDietDialogWeight.setText("1");
            }
        };
        mainViewModel.observeKcalLiveData().observe(requireActivity(),KcalObserver);
        return binding.getRoot();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onCloseListener != null) {
            //notify dialog closed
            onCloseListener.onClose(dietItem);
        }
//        mainViewModel.observeFoodLiveData().removeObserver(foodListObserver);
        mainViewModel.observeKcalLiveData().postValue("");
        mainViewModel.observeKcalLiveData().removeObserver(KcalObserver);
        foodListObserver = null;
        KcalObserver = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            //get diet item passed from out scope to edit
            dietItem = (DietItem) savedInstanceState.getSerializable(KEY_DIET_ITEM);
        }
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        //set dialog size
        Configuration config = getResources().getConfiguration();
        int screenWidth = config.screenWidthDp;
        int screenHeight = config.screenHeightDp;
        Context context = getContext();
        assert context != null;
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        window.setLayout(
                dp2px(context, screenWidth),
                dp2px(context, screenHeight * 0.85f));
    }

    /**
     * save data when user click ok
     */
    private void saveData() {
        String name = binding.editDietDialogName.getText().toString();
        //check name
        if (name.equals("")) {
            binding.tvDietDialogMsg.setText(R.string.err_msg_empty_diet_name);
            return;
        }

        String kcalString = binding.editDietDialogKcal.getText().toString();
        //check kcal
        if (kcalString.equals("")) {
            binding.tvDietDialogMsg.setText(R.string.err_msg_empty_kcal);
            return;
        }
        double kcalPerOunce = Double.parseDouble(kcalString);

        String weightString = binding.editDietDialogWeight.getText().toString();
        //check weight
        if (weightString.equals("")) {
            binding.tvDietDialogMsg.setText(R.string.err_msg_empty_food_weight);
            return;
        }
        double foodWeight = Double.parseDouble(weightString);
        double kcal = foodWeight * kcalPerOunce;
        if (dietItem == null) {
            //creating diet item
            dietItem = new DietItem(name, foodWeight, kcalPerOunce);
        } else {
            //editing diet item
            dietItem.setKcal(kcal);
            dietItem.setAmountInOunce(foodWeight);
            dietItem.setCaloriesPerOunce(kcalPerOunce);
            dietItem.setName(name);
        }
        dismiss();
    }

    /**
     * convert dp to px
     *
     * @param context Context
     * @param dpValue dp
     * @return px
     */
    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}