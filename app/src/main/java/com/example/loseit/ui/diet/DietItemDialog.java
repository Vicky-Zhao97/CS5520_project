package com.example.loseit.ui.diet;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.loseit.R;
import com.example.loseit.databinding.DialogDietItemBinding;
import com.example.loseit.model.DietItem;

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
        binding = DialogDietItemBinding.inflate(inflater, container, false);
        setCancelable(false);
        binding.buttonDietDialogCancel.setOnClickListener(view -> {
            dismiss();
        });
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
        return binding.getRoot();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onCloseListener != null) {
            //notify dialog closed
            onCloseListener.onClose(dietItem);
        }
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
                dp2px(context, screenWidth * 0.8f),
                dp2px(context, screenHeight * 0.5f));
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
        double kcal = Double.parseDouble(kcalString);
        if (dietItem == null) {
            dietItem = new DietItem(name, kcal);
        } else {
            dietItem.setKcal(kcal);
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
