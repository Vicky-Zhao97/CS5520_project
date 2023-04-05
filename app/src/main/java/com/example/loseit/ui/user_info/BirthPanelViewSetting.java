package com.example.loseit.ui.user_info;

import android.view.View;
import android.widget.NumberPicker;

import com.example.loseit.databinding.PanelBirthBinding;
import com.example.loseit.databinding.ViewScrollPickerBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * class for set birth
 */
public class BirthPanelViewSetting extends UserInfoViewSetting {
    public static final int START_YEAR = 1900;
    //abbr of months
    public static final String[] moths = new String[]{
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"};
    //year list, from 1900-now
    public final ArrayList<String> years;
    //current selected
    private int selectedDayIndex;
    private int selectedMonIndex;
    private int selectedYearIndex;

    private ViewScrollPickerBinding scrollPickerBinding;

    public BirthPanelViewSetting(String contentViewName) {
        super(contentViewName);
        years = new ArrayList<>();
        //generate years, from 1900
        int start = START_YEAR;
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        while (start <= currentYear) {
            years.add(String.valueOf(start));
            start += 1;
        }
        //set birth to today default
        selectedMonIndex = calendar.get(Calendar.MONTH);
        selectedDayIndex = calendar.get(Calendar.DAY_OF_MONTH) - 1;
        selectedYearIndex = years.indexOf(String.valueOf(calendar.get(Calendar.YEAR)));
    }

    /**
     * bind data with view
     *
     * @param container View
     */
    public void bind(View container) {
        PanelBirthBinding binding = PanelBirthBinding.bind(container);
        //set month list
        binding.birthScrollPicker.setLeftList(moths);
        //set day list
        int year = Integer.parseInt(years.get(selectedYearIndex));
        binding.birthScrollPicker.setCentralList(getDays(selectedMonIndex + 1, year));
        //set year list
        String[] yearArray = new String[years.size()];
        binding.birthScrollPicker.setRightList(years.toArray(yearArray));


        scrollPickerBinding = ViewScrollPickerBinding.
                bind(binding.birthScrollPicker);
        //show selected day, month and year
        scrollPickerBinding.leftPicker.setValue(selectedMonIndex);
        scrollPickerBinding.centerPicker.setValue(selectedDayIndex);
        scrollPickerBinding.rightPicker.setValue(selectedYearIndex);

        //set day list when month is changed
        scrollPickerBinding.leftPicker.setOnValueChangedListener((numberPicker, last, now) -> {
            //index of current selected month
            selectedMonIndex = now;
            //refresh day list
            setDataList(scrollPickerBinding, binding);
        });
        //set day when picker is scrolled
        scrollPickerBinding.centerPicker.setOnValueChangedListener((numberPicker, last, now) -> {
            //index of current selected day
            selectedDayIndex = now;
        });
        //set year when picker is scrolled
        scrollPickerBinding.rightPicker.setOnValueChangedListener((numberPicker, last, now) -> {
            //index of current selected year
            selectedYearIndex = now;
            //refresh day list when the year is leap
            setDataList(scrollPickerBinding, binding);
        });
    }

    /**
     * set day list when month and year changed
     *
     * @param scrollPickerBinding ViewScrollPickerBinding
     * @param binding             PanelBirthBinding
     */
    private void setDataList(ViewScrollPickerBinding scrollPickerBinding, PanelBirthBinding binding) {
        int yearIndex = scrollPickerBinding.rightPicker.getValue();
        int year1 = Integer.parseInt(years.get(yearIndex));
        int month = selectedMonIndex + 1;
        String[] dayList = getDays(month, year1);
        //keep day picker unchanged when month has changed
        binding.birthScrollPicker.setCentralList(dayList);
        if (selectedDayIndex >= dayList.length) {
            selectedDayIndex = dayList.length - 1;
        }
        scrollPickerBinding.centerPicker.setValue(selectedDayIndex);
    }

    /**
     * get day list of selected month
     *
     * @param month selected month
     * @param year  selected year
     * @return String[]
     */
    private String[] getDays(int month, int year) {
        //check if it is a leap year
        boolean isLeap = year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
        ArrayList<Integer> month31 = new ArrayList<>(Arrays.asList(1, 3, 5, 7, 8, 10, 12));
        ArrayList<Integer> month30 = new ArrayList<>(Arrays.asList(4, 6, 9, 11));
        String[] days;
        if (month30.contains(month)) {
            days = new String[30];
        } else if (month31.contains(month)) {
            days = new String[31];
        } else {
            //Feb
            days = new String[28 + (isLeap ? 1 : 0)];
        }
        for (int i = 0; i < days.length; i++) {
            days[i] = String.valueOf(i + 1);
        }
        return days;
    }

    /**
     * get birth set by user
     *
     * @return String
     */
    public Date getBirth() {
        int mon = selectedMonIndex;
        int day = selectedDayIndex + 1;
        int year = selectedYearIndex + START_YEAR;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, mon);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }

    /**
     * set birth
     *
     * @param birth Date
     */
    public void setBirth(Date birth) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(birth);
        selectedMonIndex = calendar.get(Calendar.MONTH);
        selectedDayIndex = calendar.get(Calendar.DAY_OF_MONTH) - 1;
        selectedYearIndex = years.indexOf(String.valueOf(calendar.get(Calendar.YEAR)));
    }
}
