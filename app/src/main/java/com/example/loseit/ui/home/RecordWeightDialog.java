package com.example.loseit.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.loseit.databinding.DialogRecordWeightBinding;
import com.example.loseit.model.DailyWeight;
import com.example.loseit.ui.user_info.BirthPanelViewSetting;
import com.example.loseit.ui.user_info.WeightPanelViewSetting;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RecordWeightDialog extends DialogFragment {
    private DialogRecordWeightBinding binding;
    private WeightPanelViewSetting weightPanelViewSetting;
    private DailyWeight dailyWeight;
    //listener for watching window close
    private OnDialogCloseListener onCloseListener;
    private double currentWeight;

    public RecordWeightDialog() {

    }

    public RecordWeightDialog(double currentWeight) {
        this.currentWeight = currentWeight;
    }

    /**
     * listener for watching dialog closed
     */
    public interface OnDialogCloseListener {
        void onClose(DailyWeight dailyWeight);
    }

    /**
     * set listener for watching dialog closed
     *
     * @param onCloseListener OnCloseListener
     */
    public void setOnCloseListener(OnDialogCloseListener onCloseListener) {
        this.onCloseListener = onCloseListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DialogRecordWeightBinding.inflate(inflater, container, false);
        //dismiss dialog when click cancel button
        binding.buttonWeightDialogCancel.setOnClickListener(view -> {
            dismiss();
        });
        //save data and dismiss dialog when click confirm button
        binding.buttonWeightDialogConfirm.setOnClickListener(view -> saveData());
        //bind weight scroll picker with view setting
        weightPanelViewSetting = new WeightPanelViewSetting("");
        //default set scroll picker to current weight
        weightPanelViewSetting.setWeightInKg(currentWeight);
        //bind view with view setting
        weightPanelViewSetting.bind(binding.scrollPickerWeightDialog.getRoot());
        dailyWeight = new DailyWeight();
        //show record date
        showRecordDate(dailyWeight.getCreateAt());
        return binding.getRoot();
    }

    /**
     * show record date
     */
    private void showRecordDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int mon = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String month = BirthPanelViewSetting.moths[mon];
        binding.tvWeightDialogDate.setText(String.format(Locale.ENGLISH,
                "Today %s %d, %d", month, day, year));
    }

    /**
     * save data when user click ok
     */
    private void saveData() {
        double weight = weightPanelViewSetting.getWeightInKg();
        dailyWeight.setWeight(weight);
        if (onCloseListener != null) {
            onCloseListener.onClose(dailyWeight);
        }
        dismiss();
    }
}
