package com.example.loseit.ui.user_info;

import static com.example.loseit.StartActivity.DB_USER_INFO_PATH;
import static com.example.loseit.ui.user_info.UserInfoViewPagerAdapter.VIEW_BIRTH;
import static com.example.loseit.ui.user_info.UserInfoViewPagerAdapter.VIEW_GENDER;
import static com.example.loseit.ui.user_info.UserInfoViewPagerAdapter.VIEW_GOAL_WEIGHT;
import static com.example.loseit.ui.user_info.UserInfoViewPagerAdapter.VIEW_HEIGHT;
import static com.example.loseit.ui.user_info.UserInfoViewPagerAdapter.VIEW_PROJECTED_PROGRESS;
import static com.example.loseit.ui.user_info.UserInfoViewPagerAdapter.VIEW_WEIGHT;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.loseit.R;
import com.example.loseit.databinding.FragmentUserInfoBinding;
import com.example.loseit.model.DailyWeight;
import com.example.loseit.model.UserInfo;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

/**
 * fragment for asking user information
 */
public class UserInfoFragment extends Fragment {
    private FragmentUserInfoBinding binding;
    //questions asking for user information
    public final ArrayList<String> questions;
    //listener for watching if this fragment has created its view
    OnViewCreatedListener viewCreatedListener;
    //listener for watching if the user information has been submitted to server
    OnSubmittedUserInfoListener submittedUserInfoListener;
    //indicate if keep submit button showing
    private boolean alwaysShowSubmitButton = false;

    private UserInfo userInfo;
    private UserInfoViewPagerAdapter pagerAdapter;

    public UserInfoFragment() {
        // Required empty public constructor
        //questions shown on head to ask user information
        questions = new ArrayList<>();
    }

    public void setViewCreatedListener(OnViewCreatedListener viewCreatedListener) {
        this.viewCreatedListener = viewCreatedListener;
    }

    public void setSubmittedUserInfoListener(OnSubmittedUserInfoListener submittedUserInfoListener) {
        this.submittedUserInfoListener = submittedUserInfoListener;
    }

    /**
     * listener for watching if this fragment has created its view
     */
    public interface OnViewCreatedListener {
        void viewCreated(UserInfoFragment userInfoFragment);
    }

    /**
     * listener for watching if the user information has been submitted to server
     */
    public interface OnSubmittedUserInfoListener {
        void userInfoSubmitted(UserInfo userInfo);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserInfoFragment.
     */
    public static UserInfoFragment newInstance(boolean alwaysShowSubmitButton) {
        UserInfoFragment fragment = new UserInfoFragment();
        fragment.setAlwaysShowSubmitButton(alwaysShowSubmitButton);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserInfoBinding.inflate(inflater, container, false);
        //set question list show on head for asking user information
        questions.addAll(Arrays.asList(
                getString(R.string.ask_gender), getString(R.string.ask_birth),
                getString(R.string.ask_height), getString(R.string.ask_weight),
                getString(R.string.ask_goal_weight), getString(R.string.projected_progress)
        ));
        //Set the maximum value of seekbar to the number of questions
        binding.seekBarProcess.setMax(questions.size() - 1);
        pagerAdapter = initPagerAdapter();
        binding.viewPagerContent.setAdapter(pagerAdapter);
        //watch page slide
        binding.viewPagerContent.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                //change question when page is scrolled
                binding.tvQuestion.setText(questions.get(position));
                binding.seekBarProcess.setProgress(position);
                //show submit button on last page
                if (position == questions.size() - 1) {
                    binding.buttonSubmit.setVisibility(View.VISIBLE);
                } else if (!alwaysShowSubmitButton) {
                    binding.buttonSubmit.setVisibility(View.GONE);
                }
            }
        });
        //slide when seek bar change
        binding.seekBarProcess.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    binding.viewPagerContent.setCurrentItem(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        // back button click listener
        binding.btnBack.setOnClickListener(view -> {
            int currentIndex = binding.viewPagerContent.getCurrentItem();
            currentIndex -= 1;
            if (currentIndex < 0) {
                currentIndex = 0;
            }
            binding.viewPagerContent.setCurrentItem(currentIndex);
        });
        //forward button click listener
        binding.btnForward.setOnClickListener(view -> {
            int currentIndex = binding.viewPagerContent.getCurrentItem();
            currentIndex += 1;
            if (currentIndex > questions.size() - 1) {
                currentIndex = questions.size() - 1;
            }
            binding.viewPagerContent.setCurrentItem(currentIndex);
        });
        //submit user information listener
        binding.buttonSubmit.setOnClickListener(view -> submitUserInfo());
        //cancel filled user information and exit listener
        binding.buttonCancel.setOnClickListener(view -> {
            cancelAndExit();
        });
        if (alwaysShowSubmitButton) {
            //keep submit button shown in sliding
            binding.buttonSubmit.setVisibility(View.VISIBLE);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //notify view created
        if (viewCreatedListener != null) {
            viewCreatedListener.viewCreated(this);
        }
    }

    /**
     * indicate if keep submit button showing
     *
     * @param alwaysShowSubmitButton bool
     */
    public void setAlwaysShowSubmitButton(boolean alwaysShowSubmitButton) {
        this.alwaysShowSubmitButton = alwaysShowSubmitButton;
    }

    /**
     * user cancels filling information and exit
     */
    private void cancelAndExit() {
        //show confirm dialog
        Context mContext = binding.getRoot().getContext();
        AlertDialog dialog = new AlertDialog.Builder(mContext).create();
        dialog.setTitle(mContext.getString(R.string.hint));
        dialog.setMessage(getString(R.string.hint_msg_discard_info_and_exit));
        //confirm delete
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, mContext.getString(R.string.yes),
                (dialogInterface, i) -> {
                    //exit this activity
                    Activity activity = getActivity();
                    assert activity != null;
                    activity.finish();
                });
        //cancel delete
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, mContext.getString(R.string.cancel),
                (dialogInterface, i) -> {
                    dialog.dismiss();
                });
        dialog.show();
    }

    /**
     * submit user information and go to MainActivity
     */
    private void submitUserInfo() {
        Context context = binding.getRoot().getContext();
        UserInfo userInfo = collectUserInfo();
        //link user id
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            showMsg(getString(R.string.error_msg_no_login),
                    context.getColor(R.color.error_msg_bg));
            return;
        }
        //set username
        userInfo.setUserName(currentUser.getDisplayName());
        //set email
        userInfo.setEmail(currentUser.getEmail());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //show loading
        showLoading(true);
        //upload user information to firestore
        db.collection(DB_USER_INFO_PATH).document(currentUser.getUid())
                .set(userInfo)
                .addOnSuccessListener(documentReference -> {
                    showLoading(false);
                    //notify user information submitted
                    if (submittedUserInfoListener != null) {
                        submittedUserInfoListener.userInfoSubmitted(userInfo);
                    }
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    showMsg(getString(R.string.error_msg_submit_info_error),
                            context.getColor(R.color.error_msg_bg));
                });

    }

    /**
     * show loading when user submits the data
     *
     * @param show true mean showing, false means hiding
     */
    private void showLoading(boolean show) {
        if (show) {
            binding.buttonSubmit.setVisibility(View.GONE);
            binding.progressBarSubmit.setVisibility(View.VISIBLE);
        } else {
            binding.buttonSubmit.setVisibility(View.VISIBLE);
            binding.progressBarSubmit.setVisibility(View.GONE);
        }
        //disable cancel button when data is submitting
        binding.buttonCancel.setEnabled(!show);
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

    /**
     * init viewpager adapter
     *
     * @return UserInfoViewPagerAdapter
     */
    private UserInfoViewPagerAdapter initPagerAdapter() {
        HashMap<String, UserInfoViewSetting> viewSettings = new HashMap<>();
        //gender page
        viewSettings.put(VIEW_GENDER, new GenderPanelViewSetting(VIEW_GENDER));
        //birth page
        viewSettings.put(VIEW_BIRTH, new BirthPanelViewSetting(VIEW_BIRTH));
        //height page
        viewSettings.put(VIEW_HEIGHT, new HeightPanelViewSetting(VIEW_HEIGHT));
        //current weight page
        WeightPanelViewSetting currentWeightSetting = new WeightPanelViewSetting(VIEW_WEIGHT);
        //current weight is 80kg default
        currentWeightSetting.setSelectedLeftIndex(80 - WeightPanelViewSetting.minWeight);
        viewSettings.put(VIEW_WEIGHT, currentWeightSetting);
        //goal weight page
        //default set goal weight to 76kg
        WeightPanelViewSetting goalWeightViewSetting = new WeightPanelViewSetting(VIEW_GOAL_WEIGHT);
        goalWeightViewSetting.setSelectedLeftIndex(50 - WeightPanelViewSetting.minWeight);
        viewSettings.put(VIEW_GOAL_WEIGHT, goalWeightViewSetting);
        //projected progress page
        viewSettings.put(VIEW_PROJECTED_PROGRESS, new ProjectedProgressViewSetting(VIEW_PROJECTED_PROGRESS));
        return new UserInfoViewPagerAdapter(viewSettings);
    }

    /**
     * collect user information from views
     *
     * @return HashMap<String, Object>
     */
    public UserInfo collectUserInfo() {
        //view adapter of these sliding pages
        GenderPanelViewSetting genderPanelViewSetting = (GenderPanelViewSetting)
                pagerAdapter.getViewSettingByName(VIEW_GENDER);
        BirthPanelViewSetting birthPanelViewSetting = (BirthPanelViewSetting)
                pagerAdapter.getViewSettingByName(VIEW_BIRTH);
        HeightPanelViewSetting heightPanelViewSetting = (HeightPanelViewSetting)
                pagerAdapter.getViewSettingByName(VIEW_HEIGHT);
        WeightPanelViewSetting weightPanelViewSetting = (WeightPanelViewSetting)
                pagerAdapter.getViewSettingByName(VIEW_WEIGHT);
        WeightPanelViewSetting goalPanelViewSetting = (WeightPanelViewSetting)
                pagerAdapter.getViewSettingByName(VIEW_GOAL_WEIGHT);
        ProjectedProgressViewSetting progressViewSetting = (ProjectedProgressViewSetting)
                pagerAdapter.getViewSettingByName(VIEW_PROJECTED_PROGRESS);
        if (userInfo == null) {
            //this is a new user, who is creating the user information
            userInfo = new UserInfo();
        }
        userInfo.setGender(genderPanelViewSetting.getGender());
        userInfo.setBirth(birthPanelViewSetting.getBirth());
        userInfo.setHeight(heightPanelViewSetting.getHeightInCm());
        userInfo.setGoalWeight(goalPanelViewSetting.getWeightInKg());

        //daily weight list, the first time a user adds information,
        // an empty list will be returned
        ArrayList<DailyWeight> dailyWeights = userInfo.getDailyWeights();
        //get the value of current weight
        double currentWeightVal = weightPanelViewSetting.getWeightInKg();
        DailyWeight currentWeight;
        //init daily weight list
        if (dailyWeights.size() > 0) {
            //The user edits the current weight information again,
            // only change the value of weight
            currentWeight = dailyWeights.get(dailyWeights.size() - 1);
        } else {
            //The user sets the current weight information for the first time
            //create the first daily weight
            currentWeight = new DailyWeight();
            //create date
            currentWeight.setCreateAt(Calendar.getInstance().getTime());
            dailyWeights.add(currentWeight);
        }
        currentWeight.setWeight(currentWeightVal);
        userInfo.setDailyWeights(dailyWeights);

        userInfo.setEndDate(progressViewSetting.getEndDate());
        userInfo.setGoalWeightLoss(progressViewSetting.getGoalLoss());
        return userInfo;
    }

    /**
     * set user information to view
     *
     * @param userInfo UserInfo
     */
    public void setUserInfo(UserInfo userInfo) {
        GenderPanelViewSetting genderPanelViewSetting = (GenderPanelViewSetting)
                pagerAdapter.getViewSettingByName(VIEW_GENDER);
        BirthPanelViewSetting birthPanelViewSetting = (BirthPanelViewSetting)
                pagerAdapter.getViewSettingByName(VIEW_BIRTH);
        HeightPanelViewSetting heightPanelViewSetting = (HeightPanelViewSetting)
                pagerAdapter.getViewSettingByName(VIEW_HEIGHT);
        WeightPanelViewSetting weightPanelViewSetting = (WeightPanelViewSetting)
                pagerAdapter.getViewSettingByName(VIEW_WEIGHT);
        WeightPanelViewSetting goalPanelViewSetting = (WeightPanelViewSetting)
                pagerAdapter.getViewSettingByName(VIEW_GOAL_WEIGHT);
        ProjectedProgressViewSetting progressViewSetting = (ProjectedProgressViewSetting)
                pagerAdapter.getViewSettingByName(VIEW_PROJECTED_PROGRESS);

        genderPanelViewSetting.setGender(userInfo.getGender());
        birthPanelViewSetting.setBirth(userInfo.getBirth());
        heightPanelViewSetting.setHeightInCm(userInfo.getHeight());
        weightPanelViewSetting.setWeightInKg(userInfo.getCurrentWeight());
        goalPanelViewSetting.setWeightInKg(userInfo.getGoalWeight());
        progressViewSetting.setGoalLoss(userInfo.getGoalWeightLoss());
        this.userInfo = userInfo;
    }
}