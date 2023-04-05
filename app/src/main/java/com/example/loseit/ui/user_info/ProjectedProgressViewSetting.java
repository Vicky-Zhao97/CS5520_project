package com.example.loseit.ui.user_info;

import static com.example.loseit.ui.user_info.GenderPanelViewSetting.MALE;

import android.view.View;
import android.widget.SeekBar;

import com.example.loseit.databinding.PanelProjectedProgressBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ProjectedProgressViewSetting extends UserInfoViewSetting {
    //min lose weight, in kg
    public static final double minLoseWeight = 0.5;
    //max lose weight, in kg
    public static final double maxLoseWeight = 2.5;
    //Weekly Weight Loss Goal
    private double goalLoss = -1;
    //view binding
    private PanelProjectedProgressBinding binding;
    //current weight
    private double startWeight;
    //goal weight
    private double goalWeight;
    //start and end date of lose weight
    private Date startDate;
    private Date endDate;

    public ProjectedProgressViewSetting(String contentViewName) {
        super(contentViewName);
    }

    @Override
    public void bind(View view) {
        binding = PanelProjectedProgressBinding.bind(view);
        //set min and max lose weight
        binding.seekBarWeekProgress.setMin((int) (minLoseWeight * 10));
        binding.seekBarWeekProgress.setMax((int) (maxLoseWeight * 10));
        binding.seekBarWeekProgress.setProgress((int) (goalLoss * 10));
        binding.seekBarWeekProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean isUser) {
                if (isUser) {
                    setGoalLoss(i / 10.);
                }
                //refresh projected progress when weekly goal loss change
                calProjectedProgress(startWeight, goalWeight);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        setGoalLoss(goalLoss);
        calProjectedProgress(startWeight, goalWeight);
    }

    /**
     * @param startWeight start weight in kg
     * @param goalWeight  goal weight in kg
     */
    public void setWeight(double startWeight, double goalWeight) {
        this.startWeight = startWeight;
        this.goalWeight = goalWeight;
        if (binding != null) {
            calProjectedProgress(startWeight, goalWeight);
        }
    }

    /**
     * set goal weight loss weekly
     *
     * @param goalLoss value in kg
     */
    public void setGoalLoss(double goalLoss) {
        this.goalLoss = goalLoss;
        if (binding != null) {
            //this view is shown
            binding.seekBarWeekProgress.setProgress((int) (goalLoss * 10));
            binding.tvProgress.setText(String.format(Locale.ENGLISH,
                    "%.1f kg/week", goalLoss));
        }
    }

    /**
     * set BMR by user information
     *
     * @param currentWeight double, in kg
     * @param goalWeight    double, in kg
     */
    private void calProjectedProgress(double currentWeight, double goalWeight) {
        //user has not set a goal.
        if (goalLoss < 0) {
            setGoalLoss((minLoseWeight + maxLoseWeight) / 2);
        }
        double loseWeight = currentWeight - goalWeight;
        //Calculate the total number of days required to lose weight
        int days = (int) (loseWeight / (goalLoss / 7.));
        Calendar now = Calendar.getInstance();
        startDate = now.getTime();
        //calculate end date
        now.add(Calendar.DAY_OF_MONTH, days);
        endDate = now.getTime();
        //show start and end date
        int day = now.get(Calendar.DAY_OF_MONTH);
        int year = now.get(Calendar.YEAR);
        String month = BirthPanelViewSetting.moths[now.get(Calendar.MONTH)];
        binding.tvProgressStart.setText(String.format(Locale.ENGLISH,
                "Start: Today, %.2f kg", currentWeight));
        binding.tvProgressEnd.setText(String.format(Locale.ENGLISH,
                "End: %s %d,%d, %.2f kg", month, day, year, goalWeight));
    }

    /**
     * weekly goal weight loss
     *
     * @return double
     */
    public double getGoalLoss() {
        return goalLoss;
    }

    /**
     * end date  of lose weight
     *
     * @return Date
     */
    public Date getEndDate() {
        return endDate;
    }

}
