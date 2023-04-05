package com.example.loseit.ui.user_info;

import android.view.View;
import android.widget.NumberPicker;

import com.example.loseit.databinding.PanelHeightBinding;
import com.example.loseit.databinding.ViewScrollPickerBinding;

import java.util.Arrays;
import java.util.Objects;

public class HeightPanelViewSetting extends UserInfoViewSetting {
    public static final String UNIT_CM = "cm";
    public static final String UNIT_FT_IN = "ft/in";
    //unit of height
    public static String[] units = new String[]{UNIT_CM, UNIT_FT_IN,};
    //min and max height in cm
    public static double minHeight = 10;
    public static double maxHeight = 350;

    //current selected unit index
    private int selectedUnitIndex = 0;
    private int selectedLeftIndex = 0;
    private int selectedCentralIndex = 0;
    private PanelHeightBinding binding;

    private ViewScrollPickerBinding scrollPickerBinding;

    public HeightPanelViewSetting(String contentViewName) {
        super(contentViewName);
        //default set to 160cm
        selectedLeftIndex = (int) (160 - minHeight);
    }

    @Override
    public void bind(View view) {
        binding = PanelHeightBinding.bind(view);
        //set unit list
        binding.heightScrollPicker.setRightList(units);
        //set left and central list depends on unit
        setLeftCentralList(binding);
        //listener for unit
        scrollPickerBinding = ViewScrollPickerBinding.
                bind(binding.heightScrollPicker);
        scrollPickerBinding.leftPicker.setOnValueChangedListener((numberPicker, i, i1) ->
                selectedLeftIndex = i1);
        scrollPickerBinding.centerPicker.setOnValueChangedListener((numberPicker, i, i1) -> {
            selectedCentralIndex = i1;
        });
        scrollPickerBinding.rightPicker.setOnValueChangedListener((numberPicker, i, i1) -> {
            selectedUnitIndex = i1;
            if (i1 == 0) {
                //cm
                showCm(binding, scrollPickerBinding);
            } else {
                //ft/in
                showFtInch(binding, scrollPickerBinding);
            }
        });
        //default 160cm
        scrollPickerBinding.leftPicker.setValue(selectedLeftIndex);
        scrollPickerBinding.centerPicker.setValue(selectedCentralIndex);
    }

    /**
     * set left and central list depends on unit
     *
     * @param binding PanelHeightBinding
     */
    private void setLeftCentralList(PanelHeightBinding binding) {
        //set central list depends on unit
        String[] centralList;
        if (Objects.equals(units[selectedUnitIndex], UNIT_FT_IN)) {
            //ft/in
            centralList = new String[12];
        } else {
            //cm
            centralList = new String[10];
        }
        for (int i = 0; i < centralList.length; i++) {
            centralList[i] = String.valueOf(i);
        }
        binding.heightScrollPicker.setCentralList(centralList);
        // set left list depends on unit
        String[] leftList;
        if (Objects.equals(units[selectedUnitIndex], UNIT_FT_IN)) {
            //ft/in
            int[] maxFtInch = cm2FtInch(maxHeight);
            int[] minFtInch = cm2FtInch(minHeight);
            leftList = new String[maxFtInch[0] - minFtInch[0]];
            for (int i = 0; i < leftList.length; i++) {
                leftList[i] = String.valueOf(minFtInch[0] + i);
            }
        } else {
            //cm
            leftList = new String[(int) (maxHeight - minHeight)];
            for (int i = 0; i < leftList.length; i++) {
                leftList[i] = String.valueOf((int) minHeight + i);
            }
        }
        binding.heightScrollPicker.setLeftList(leftList);
    }

    /**
     * show height in ft/in
     *
     * @param binding ViewScrollPickerBinding
     */
    private void showFtInch(PanelHeightBinding binding, ViewScrollPickerBinding scrollPickerBinding) {
        double cm = selectedLeftIndex + minHeight +
                selectedCentralIndex / 10.;
        int[] ftInch = cm2FtInch(cm);
        setLeftCentralList(binding);
        int[] minFtInch = cm2FtInch(minHeight);
        scrollPickerBinding.leftPicker.setValue(ftInch[0] - minFtInch[0]);
        scrollPickerBinding.centerPicker.setValue(ftInch[1]);
        selectedLeftIndex = ftInch[0] - minFtInch[0];
        selectedCentralIndex = ftInch[1];
        binding.heightScrollPicker.setLeftSplit("'");
        binding.heightScrollPicker.setRightSplit("\"");

    }

    /**
     * show height in cm
     *
     * @param binding ViewScrollPickerBinding
     */
    private void showCm(PanelHeightBinding binding, ViewScrollPickerBinding scrollPickerBinding) {
        int[] ftInch = new int[2];
        int[] minFtInch = cm2FtInch(minHeight);
        ftInch[0] = selectedLeftIndex + minFtInch[0];
        ftInch[1] = selectedCentralIndex;
        int[] cm = ftInch2cm(ftInch);
        setLeftCentralList(binding);
        //get int part
        scrollPickerBinding.leftPicker.setValue((int) (cm[0] - minHeight));
        //get one decimal
        scrollPickerBinding.centerPicker.setValue(cm[1]);
        selectedLeftIndex = (int) (cm[0] - minHeight);
        selectedCentralIndex = cm[1];
        binding.heightScrollPicker.setLeftSplit(".");
        binding.heightScrollPicker.setRightSplit("");
    }

    /**
     * convert cm to  ft/inch
     *
     * @param cm double
     * @return int[ft, inch]
     */
    private int[] cm2FtInch(double cm) {
        double ft = Math.floor(cm * 0.3937008 / 12);
        double inch = Math.floor((cm * 0.3937008 - ft * 12));
        return new int[]{(int) ft, (int) inch};
    }

    /**
     * convert  ft/inch to cm
     *
     * @param ftInch int[ft, inch]
     * @return cm
     */
    private int[] ftInch2cm(int[] ftInch) {
        double totalCm = (ftInch[0] * 12 + ftInch[1]) / 0.3937008;
        return split2IntegerDecimal(totalCm);
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
     * get height set by user in the unit cm
     *
     * @return double
     */
    public double getHeightInCm() {
        double height = 0;
        if (Objects.equals(units[selectedUnitIndex], UNIT_CM)) {
            height = selectedLeftIndex + minHeight;
            height += selectedCentralIndex / 10.;
            //height in cm
            int[] cm = split2IntegerDecimal(height);
            height = cm[0] + cm[1] / 10.;
        } else {
            //height in ft/in
            int[] ftInch = new int[2];
            int[] minFtInch = cm2FtInch(minHeight);
            ftInch[0] = selectedLeftIndex + minFtInch[0];
            ftInch[1] = selectedCentralIndex;
            int[] cm = ftInch2cm(ftInch);
            height = cm[0] + cm[1] / 10.;
        }
        return height;
    }

    /**
     * set height in unit cm
     *
     * @param height double
     */
    public void setHeightInCm(double height) {
        if (Objects.equals(units[selectedUnitIndex], UNIT_CM)) {
            //height in cm
            int[] cm = split2IntegerDecimal(height);
            int[] minCm = split2IntegerDecimal(minHeight);
            selectedLeftIndex = cm[0] - minCm[0];
            selectedCentralIndex = cm[1];
        } else {
            //height in ft/in
            int[] ftInch = cm2FtInch(height);
            int[] minFtInch = cm2FtInch(minHeight);
            selectedLeftIndex = ftInch[0] - minFtInch[0];
            selectedCentralIndex = ftInch[1];
        }
    }
}
