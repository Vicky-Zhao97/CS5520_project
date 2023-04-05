package com.example.loseit.ui.user_info;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.lang.reflect.Field;

/**
 * number picker with custom style
 */
public class StyledNumberPicker extends NumberPicker {


    public StyledNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * convert sp to pixel
     *
     * @param context Context
     * @param spValue sp
     * @return px
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        updateView(child);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        super.addView(child, params);
        updateView(child);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        updateView(child);
    }

    private void updateView(View view) {
        if (view instanceof EditText) {
            ((EditText) view).setTextColor(Color.parseColor("#2c2c2c"));
            ((EditText) view).setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
        }
    }
}
