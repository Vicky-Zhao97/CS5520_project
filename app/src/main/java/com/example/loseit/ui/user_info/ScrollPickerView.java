package com.example.loseit.ui.user_info;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.example.loseit.R;
import com.example.loseit.databinding.ViewScrollPickerBinding;

import java.util.Objects;

/**
 * custom scroll picker view
 */
public class ScrollPickerView extends FrameLayout {
    private final ViewScrollPickerBinding binding;

    public ScrollPickerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.view_scroll_picker, this);
        binding = ViewScrollPickerBinding.bind(view);
        TypedArray attrsArray = context.obtainStyledAttributes(attrs, R.styleable.ScrollPickerView);
        //set background
        Drawable centralBarBg = attrsArray.getDrawable(R.styleable.ScrollPickerView_central_bar_bg);
        if (Objects.equals(centralBarBg, null)) {
            //default bg
            centralBarBg = AppCompatResources.getDrawable(context, R.drawable.green_round_rect);
        }
        binding.centralBar.setBackground(centralBarBg);
        //set split character
        String splitLeft = attrsArray.getString(R.styleable.ScrollPickerView_split_left);
        if (Objects.equals(splitLeft, null)) {
            splitLeft = ".";
        }
        binding.splitLeft.setText(splitLeft);
        String splitRight = attrsArray.getString(R.styleable.ScrollPickerView_split_right);
        if (Objects.equals(splitRight, null)) {
            splitRight = ".";
        }
        binding.splitRight.setText(splitRight);
        attrsArray.recycle();


    }

    /**
     * set left scroll list
     *
     * @param leftList String[]
     */
    public void setLeftList(String[] leftList) {
        binding.leftPicker.setDisplayedValues(null);
        binding.leftPicker.setMaxValue(leftList.length - 1);
        binding.leftPicker.setDisplayedValues(leftList);
    }

    /**
     * set central scroll list
     *
     * @param centralList String[]
     */
    public void setCentralList(String[] centralList) {
        binding.centerPicker.setDisplayedValues(null);
        binding.centerPicker.setMaxValue(centralList.length - 1);
        binding.centerPicker.setDisplayedValues(centralList);
    }

    /**
     * set right scroll list
     *
     * @param rightList String[]
     */
    public void setRightList(String[] rightList) {
        binding.rightPicker.setDisplayedValues(null);
        binding.rightPicker.setMaxValue(rightList.length - 1);
        binding.rightPicker.setDisplayedValues(rightList);
    }

    /**
     * set right split character
     *
     * @param split split character
     */
    public void setRightSplit(String split) {
        binding.splitRight.setText(split);
    }

    /**
     * set left split character
     *
     * @param split split character
     */
    public void setLeftSplit(String split) {
        binding.splitLeft.setText(split);
    }
}
