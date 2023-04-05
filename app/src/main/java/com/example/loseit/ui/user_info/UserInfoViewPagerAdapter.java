package com.example.loseit.ui.user_info;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loseit.R;
import com.example.loseit.databinding.ViewpagerUserInfoBinding;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class UserInfoViewPagerAdapter extends RecyclerView.Adapter<UserInfoViewPagerAdapter.UserInfoViewHolder> {
    //view name
    public static final String VIEW_GENDER = "gender";
    public static final String VIEW_BIRTH = "birth";
    public static final String VIEW_HEIGHT = "height";
    public static final String VIEW_WEIGHT = "weight";
    public static final String VIEW_GOAL_WEIGHT = "goal weight";
    public static final String VIEW_PROJECTED_PROGRESS = "projected progress";
    //specify these view shown dynamically
    public static final String[] viewNames = {VIEW_GENDER, VIEW_BIRTH, VIEW_HEIGHT,
            VIEW_WEIGHT, VIEW_GOAL_WEIGHT, VIEW_PROJECTED_PROGRESS};
    //settings for view in each page
    private final HashMap<String, UserInfoViewSetting> viewSettings;

    public UserInfoViewPagerAdapter(HashMap<String, UserInfoViewSetting> viewSettings) {
        this.viewSettings = viewSettings;
    }

    /**
     * get view setting by name
     *
     * @param viewName String
     * @return UserInfoViewSetting
     */
    public UserInfoViewSetting getViewSettingByName(String viewName) {
        return viewSettings.get(viewName);
    }

    /**
     * view holder
     */
    public static class UserInfoViewHolder extends RecyclerView.ViewHolder {
        private final HashMap<String, View> viewHashMap;

        public UserInfoViewHolder(@NonNull ViewpagerUserInfoBinding binding) {
            super(binding.getRoot());
            viewHashMap = new HashMap<>();
            //specify view list shown in sliding
            viewHashMap.put(VIEW_GENDER, binding.panelGender.getRoot());
            viewHashMap.put(VIEW_BIRTH, binding.panelBirth.getRoot());
            viewHashMap.put(VIEW_HEIGHT, binding.panelHeight.getRoot());
            viewHashMap.put(VIEW_WEIGHT, binding.panelWeight.getRoot());
            viewHashMap.put(VIEW_GOAL_WEIGHT, binding.panelGoalWeight.getRoot());
            viewHashMap.put(VIEW_PROJECTED_PROGRESS, binding.panelProjectedProgress.getRoot());
        }

        /**
         * decide which view to show
         *
         * @param setting UserInfoViewSetting
         */
        public void bind(UserInfoViewSetting setting) {
            //show or hide view dynamically
            for (String key : viewHashMap.keySet()) {
                View view = viewHashMap.get(key);
                assert view != null;
                if (Objects.equals(key, setting.contentViewName)) {
                    view.setVisibility(View.VISIBLE);
                    //bind data with view
                    setting.bind(view);
                } else {
                    //hide view when it is not shown
                    view.setVisibility(View.GONE);
                }

            }
        }
    }

    @NonNull
    @Override
    public UserInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewpagerUserInfoBinding binding = ViewpagerUserInfoBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        UserInfoViewHolder holder = new UserInfoViewHolder(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserInfoViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        String viewName = viewNames[position];
        UserInfoViewSetting setting = viewSettings.get(viewName);
        //bind data with view
        holder.bind(setting);
        WeightPanelViewSetting currentWeightViewSetting = (WeightPanelViewSetting)
                viewSettings.get(VIEW_WEIGHT);
        WeightPanelViewSetting goalWeightViewSetting = (WeightPanelViewSetting)
                viewSettings.get(VIEW_GOAL_WEIGHT);

        assert currentWeightViewSetting != null;
        assert goalWeightViewSetting != null;
        boolean goalWeightInValid = checkGoalWeight(currentWeightViewSetting,
                goalWeightViewSetting);
        if (goalWeightInValid) {
            currentWeightViewSetting.setErrorMsg(context.getString(
                    R.string.error_msg_invalid_goal_weight));
            goalWeightViewSetting.setErrorMsg(context.getString(
                    R.string.error_msg_invalid_goal_weight));
        } else {
            currentWeightViewSetting.setErrorMsg("");
            goalWeightViewSetting.setErrorMsg("");
        }
        currentWeightViewSetting.showError();
        goalWeightViewSetting.showError();
        currentWeightViewSetting.setValueChangeListener(value -> {
            boolean inValid = checkGoalWeight(currentWeightViewSetting,
                    goalWeightViewSetting);
            if (inValid) {
                currentWeightViewSetting.setErrorMsg(context.getString(
                        R.string.error_msg_invalid_goal_weight));
                currentWeightViewSetting.showError();
            } else {
                currentWeightViewSetting.setErrorMsg("");
                currentWeightViewSetting.showError();
            }
            refreshProjectProgress();
        });
        goalWeightViewSetting.setValueChangeListener(value -> {
            boolean inValid = checkGoalWeight(currentWeightViewSetting,
                    goalWeightViewSetting);
            if (inValid) {
                goalWeightViewSetting.setErrorMsg(context.getString(
                        R.string.error_msg_invalid_goal_weight));
                goalWeightViewSetting.showError();
            } else {
                goalWeightViewSetting.setErrorMsg("");
                goalWeightViewSetting.showError();
            }
            refreshProjectProgress();
        });
        assert setting != null;
        if (Objects.equals(setting.contentViewName, VIEW_PROJECTED_PROGRESS)) {
            //refresh projected progress when it is first bound
            refreshProjectProgress();
        }
    }

    /**
     * refresh project progress
     */
    private void refreshProjectProgress() {
        WeightPanelViewSetting weightPanelViewSetting = (WeightPanelViewSetting)
                getViewSettingByName(VIEW_WEIGHT);
        WeightPanelViewSetting goalPanelViewSetting = (WeightPanelViewSetting)
                getViewSettingByName(VIEW_GOAL_WEIGHT);
        ProjectedProgressViewSetting progressViewSetting = (ProjectedProgressViewSetting)
                getViewSettingByName(VIEW_PROJECTED_PROGRESS);
        double currentWeight = weightPanelViewSetting.getWeightInKg();
        double goalWeight = goalPanelViewSetting.getWeightInKg();
        //calculate projected progress by user information
        progressViewSetting.setWeight(currentWeight, goalWeight);
    }

    /**
     * check if goal weight is more than current weight
     *
     * @param currentSetting WeightPanelViewSetting
     * @param goalSetting    WeightPanelViewSetting
     */
    private boolean checkGoalWeight(WeightPanelViewSetting currentSetting,
                                    WeightPanelViewSetting goalSetting) {

        double currentWeight = currentSetting.getWeightInKg();
        double goalWeight = goalSetting.getWeightInKg();
        return goalWeight > currentWeight;
    }

    @Override
    public int getItemCount() {
        return viewNames.length;
    }
}
