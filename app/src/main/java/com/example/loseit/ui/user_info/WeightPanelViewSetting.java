package com.example.loseit.ui.user_info;

import android.content.Context;
import android.view.View;

import androidx.appcompat.content.res.AppCompatResources;

import com.example.loseit.R;
import com.example.loseit.databinding.PanelWeightBinding;
import com.example.loseit.databinding.ViewScrollPickerBinding;

import java.util.Objects;

public class WeightPanelViewSetting extends UserInfoViewSetting {
    public static final String UNIT_KG = "kg";
    public static final String UNIT_LBS = "lbs";
    public static final String UNIT_ST_LBS = "st/lbs";
    //max weight in list,kg
    public static final int maxWeight = 727;
    //min weight in list, kg
    public static final int minWeight = 30;
    //unit of weight
    public static String[] units = new String[]{UNIT_KG, UNIT_LBS, UNIT_ST_LBS};
    //selected
    private int selectedUnitIndex = 0;
    private int selectedLeftIndex;
    private int selectedCentralIndex = 0;

    private String errorMsg = "";

    private ViewScrollPickerBinding scrollPickerBinding;
    private PanelWeightBinding binding;

    private ValueChangeListener valueChangeListener;

    public WeightPanelViewSetting(String contentViewName) {
        super(contentViewName);
    }

    /**
     * listener for watching value change
     */
    public interface ValueChangeListener {
        void onValueChanged(double value);
    }

    public void setValueChangeListener(ValueChangeListener valueChangeListener) {
        this.valueChangeListener = valueChangeListener;
    }

    public void setSelectedLeftIndex(int selectedLeftIndex) {
        this.selectedLeftIndex = selectedLeftIndex;
    }

    @Override
    public void bind(View view) {
        binding = PanelWeightBinding.bind(view);
        //set unitList
        binding.weightScrollPicker.setRightList(units);
        //set left and central list
        setLeftCentralList(binding);
        //listener
        scrollPickerBinding = ViewScrollPickerBinding.
                bind(binding.weightScrollPicker);
        scrollPickerBinding.leftPicker.setValue(selectedLeftIndex);
        scrollPickerBinding.centerPicker.setValue(selectedCentralIndex);
        scrollPickerBinding.rightPicker.setValue(selectedUnitIndex);
        scrollPickerBinding.rightPicker.setOnValueChangedListener((numberPicker, i, i1) -> {
            selectedUnitIndex = i1;
            //show value in kg
            if (Objects.equals(units[selectedUnitIndex], UNIT_KG)) {
                showKg(binding, scrollPickerBinding);
            }
            //show value in lbs
            if (Objects.equals(units[selectedUnitIndex], UNIT_LBS)) {
                showLbs(binding, scrollPickerBinding, i);
            }
            //show value in st/lbs
            if (Objects.equals(units[selectedUnitIndex], UNIT_ST_LBS)) {
                showStLbs(binding, scrollPickerBinding);
            }
            //notify value change
            if (valueChangeListener != null) {
                valueChangeListener.onValueChanged(getWeightInKg());
            }
        });
        scrollPickerBinding.leftPicker.setOnValueChangedListener((numberPicker, i, i1) -> {
            selectedLeftIndex = i1;
            //notify value change
            if (valueChangeListener != null) {
                valueChangeListener.onValueChanged(getWeightInKg());
            }
        });
        scrollPickerBinding.centerPicker.setOnValueChangedListener((numberPicker, i, i1) -> {
            selectedCentralIndex = i1;
            //notify value change
            if (valueChangeListener != null) {
                valueChangeListener.onValueChanged(getWeightInKg());
            }
        });
        //show error message if errorMsg !=''
        showError();
    }

    /**
     * if errorMsg !='', this widget will show an error message below
     * and turn scroll picker background to red
     *
     * @param errorMsg String
     */
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    /**
     * show error message and set the background of scroll list to red
     */
    public void showError() {
        if (scrollPickerBinding == null) {
            return;
        }
        Context context = scrollPickerBinding.getRoot().getContext();
        if (!Objects.equals(errorMsg, "")) {
            scrollPickerBinding.centralBar.setBackground(
                    AppCompatResources.getDrawable(context, R.drawable.red_round_rect)
            );
            binding.tvGoalWeightWarning.setVisibility(View.VISIBLE);
            binding.tvGoalWeightWarning.setText(errorMsg);
            binding.tvGoalWeightWarning.setTextColor(context.getColor(R.color.error_msg_bg));
        } else {
            scrollPickerBinding.centralBar.setBackground(
                    AppCompatResources.getDrawable(context, R.drawable.green_round_rect)
            );
            binding.tvGoalWeightWarning.setVisibility(View.INVISIBLE);
            binding.tvGoalWeightWarning.setText("");
        }
    }

    /**
     * show weight in unit kg
     *
     * @param binding             PanelWeightBinding
     * @param scrollPickerBinding ViewScrollPickerBinding
     */
    private void showKg(PanelWeightBinding binding, ViewScrollPickerBinding scrollPickerBinding) {
        int[] minLbs = kg2lbs(minWeight);
        double lbs = scrollPickerBinding.leftPicker.getValue() + minLbs[0];
        lbs += scrollPickerBinding.centerPicker.getValue() / 10.;
        int[] kg = lbs2kg(lbs);
        setLeftCentralList(binding);
        scrollPickerBinding.leftPicker.setValue(kg[0] - minWeight);
        scrollPickerBinding.centerPicker.setValue(kg[1]);
        selectedLeftIndex = kg[0] - minWeight;
        selectedCentralIndex = kg[1];
        binding.weightScrollPicker.setLeftSplit(".");
        binding.weightScrollPicker.setRightSplit("");
    }

    /**
     * show weight in unit lbs
     *
     * @param binding             PanelWeightBinding
     * @param scrollPickerBinding ViewScrollPickerBinding
     */

    private void showLbs(PanelWeightBinding binding, ViewScrollPickerBinding scrollPickerBinding,
                         int lastUnitIndex) {
        int[] lbs;
        if (Objects.equals(units[lastUnitIndex], UNIT_KG)) {
            // kg->lbs
            double kg = scrollPickerBinding.leftPicker.getValue() + minWeight;
            kg += scrollPickerBinding.centerPicker.getValue() / 10.;
            lbs = kg2lbs(kg);
        } else {
            // st/lib->lbs
            int[] minStLbs = kg2StLbs(minWeight);
            double st = scrollPickerBinding.leftPicker.getValue() + minStLbs[0];
            double lb = scrollPickerBinding.centerPicker.getValue();
            lbs = stAndLbs2Lbs(new int[]{(int) st, (int) lb});
        }
        setLeftCentralList(binding);
        int[] minLbs = kg2lbs(minWeight);
        scrollPickerBinding.leftPicker.setValue(lbs[0] - minLbs[0]);
        scrollPickerBinding.centerPicker.setValue(lbs[1]);
        selectedLeftIndex = lbs[0] - minLbs[0];
        selectedCentralIndex = lbs[1];
        binding.weightScrollPicker.setLeftSplit(".");
        binding.weightScrollPicker.setRightSplit("");
    }

    /**
     * show weight in unit st/lbs
     *
     * @param binding             PanelWeightBinding
     * @param scrollPickerBinding ViewScrollPickerBinding
     */
    private void showStLbs(PanelWeightBinding binding, ViewScrollPickerBinding scrollPickerBinding) {
        int[] minLbs = kg2lbs(minWeight);
        double lbs = scrollPickerBinding.leftPicker.getValue() + minLbs[0];
        lbs += scrollPickerBinding.centerPicker.getValue() / 10.;
        int[] stLbs = lbs2StAndLbs(lbs);
        setLeftCentralList(binding);
        int[] minStLbs = kg2StLbs(minWeight);
        scrollPickerBinding.leftPicker.setValue(stLbs[0] - minStLbs[0]);
        scrollPickerBinding.centerPicker.setValue(stLbs[1]);
        selectedLeftIndex = stLbs[0] - minStLbs[0];
        selectedCentralIndex = stLbs[1];
        binding.weightScrollPicker.setLeftSplit("'");
        binding.weightScrollPicker.setRightSplit("");
    }

    /**
     * set left and central scroll list depends on unit
     *
     * @param binding PanelWeightBinding
     */
    private void setLeftCentralList(PanelWeightBinding binding) {
        String[] centralList;
        if (Objects.equals(units[selectedUnitIndex], UNIT_ST_LBS)) {
            //st/lbs
            centralList = new String[14];
        } else {
            //kg, lbs
            centralList = new String[10];
        }
        for (int i = 0; i < centralList.length; i++) {
            centralList[i] = String.valueOf(i);
        }
        binding.weightScrollPicker.setCentralList(centralList);

        String[] leftList;
        if (Objects.equals(units[selectedUnitIndex], UNIT_KG)) {
            leftList = new String[maxWeight - minWeight];
            for (int i = minWeight; i < maxWeight; i++) {
                leftList[i - minWeight] = String.valueOf(i);
            }
        } else if (Objects.equals(units[selectedUnitIndex], UNIT_LBS)) {
            //lbs
            int[] minLbs = kg2lbs(minWeight);
            int[] maxLbs = kg2lbs(maxWeight);
            leftList = new String[maxLbs[0] - minLbs[0]];
            for (int i = 0; i < leftList.length; i++) {
                leftList[i] = String.valueOf(i + minLbs[0]);
            }
        } else {
            //st/lbs
            int[] minStLbs = kg2StLbs(minWeight);
            int[] maxStLbs = kg2StLbs(maxWeight);
            leftList = new String[maxStLbs[0] - minStLbs[0]];
            for (int i = 0; i < leftList.length; i++) {
                leftList[i] = String.valueOf(i + minStLbs[0]);
            }
        }
        binding.weightScrollPicker.setLeftList(leftList);
    }

    /**
     * split a number into integer and decimal
     *
     * @param num double
     * @return [integer, decimal]
     */
    private int[] split2IntegerDecimal(double num) {
        double integer = Math.floor(num);
        double decimal = Math.rint((num - integer) * 10);
        return new int[]{(int) integer, (int) decimal};
    }

    /**
     * convert kg to lbs
     *
     * @param kg double
     * @return int[]
     */
    private int[] kg2lbs(double kg) {
        double lbs = kg * 2.2046226;
        return split2IntegerDecimal(lbs);
    }

    /**
     * convert lbs to kg
     *
     * @param lbs double
     * @return int[]
     */
    private int[] lbs2kg(double lbs) {
        double kg = lbs / 2.2046226;
        return split2IntegerDecimal(kg);
    }

    /**
     * convert lbs to st/lbs
     *
     * @param lbs double
     * @return int[]
     */
    private int[] lbs2StAndLbs(double lbs) {
        double st = Math.floor(lbs / 14);
        double lb = Math.rint(lbs - 14 * st);
        return new int[]{(int) st, (int) lb};
    }

    /**
     * convert st/lbs to lbs
     *
     * @param stLbs double
     * @return int[]
     */
    private int[] stAndLbs2Lbs(int[] stLbs) {
        double lbs = stLbs[0] * 14 + stLbs[1];
        return split2IntegerDecimal(lbs);
    }

    /**
     * convert kg to st/lbs
     *
     * @param kg double
     * @return int[]
     */
    private int[] kg2StLbs(double kg) {
        int[] lbs = kg2lbs(kg);
        return lbs2StAndLbs(lbs[0] + lbs[1] / 10.0);
    }

    /**
     * get weight set by user in the unit kg
     *
     * @return double
     */
    public double getWeightInKg() {
        double weight = 0;
        if (Objects.equals(units[selectedUnitIndex], UNIT_KG)) {
            weight = selectedLeftIndex + minWeight;
            weight += selectedCentralIndex / 10.;
        }
        //lbs -> kg
        if (Objects.equals(units[selectedUnitIndex], UNIT_LBS)) {
            int[] minLbs = kg2lbs(minWeight);
            double lbs = selectedLeftIndex + minLbs[0];
            lbs += selectedCentralIndex / 10.;
            int[] kg = lbs2kg(lbs);
            weight = kg[0] + kg[1] / 10.;
        }
        //st/lbs -> kg
        if (Objects.equals(units[selectedUnitIndex], UNIT_ST_LBS)) {
            int[] minStLbs = kg2StLbs(minWeight);
            double st = selectedLeftIndex + minStLbs[0];
            double lb = selectedCentralIndex;
            int[] lbs;
            lbs = stAndLbs2Lbs(new int[]{(int) st, (int) lb});
            int[] kg = lbs2kg(lbs[0] + lbs[1] / 10.);
            weight = kg[0] + kg[1] / 10.;
        }
        return weight;
    }

    /**
     * set weight in unit kg
     *
     * @param weight weight
     */
    public void setWeightInKg(double weight) {
        if (Objects.equals(units[selectedUnitIndex], UNIT_KG)) {
            int[] kg = split2IntegerDecimal(weight);
            selectedLeftIndex = kg[0] - minWeight;
            selectedCentralIndex = kg[1];
        }
        if (Objects.equals(units[selectedUnitIndex], UNIT_LBS)) {
            //show in unit lbs
            int[] lbs = kg2lbs(weight);
            int[] minLbs = kg2lbs(minWeight);
            selectedLeftIndex = lbs[0] - minLbs[0];
            selectedCentralIndex = lbs[1];
        }
        if (Objects.equals(units[selectedUnitIndex], UNIT_ST_LBS)) {
            //show in st/lbs
            int[] lbs_int = kg2lbs(weight);
            double lbs = lbs_int[0] + lbs_int[1] / 10.;
            int[] stLbs = lbs2StAndLbs(lbs);
            int[] minStLbs = kg2StLbs(minWeight);
            selectedLeftIndex = stLbs[0] - minStLbs[0];
            selectedCentralIndex = stLbs[1];
        }
    }
}
