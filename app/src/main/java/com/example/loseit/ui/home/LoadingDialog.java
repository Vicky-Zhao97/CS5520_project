package com.example.loseit.ui.home;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.loseit.R;
import com.example.loseit.databinding.DialogLoadingBinding;

/**
 * custom loading dialog
 */
public class LoadingDialog extends Dialog {

    public LoadingDialog(Context context) {
        this(context, R.style.loading_dialog, context.getString(R.string.trying_to_load));

    }

    public LoadingDialog(Context context, String string) {
        this(context, R.style.loading_dialog, string);
    }

    protected LoadingDialog(Context context, int theme, String message) {
        super(context, theme);
        //Do not close the pop-up window when clicking on other areas
        setCanceledOnTouchOutside(true);
        com.example.loseit.databinding.DialogLoadingBinding binding =
                DialogLoadingBinding.inflate(getLayoutInflater(),
                        null, false);
        setContentView(binding.getRoot());
        binding.tvLoadingTx.setText(message);
        // load animation
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.loading_animation);
        // Display animation using ImageView
        binding.ivLoading.startAnimation(hyperspaceJumpAnimation);
        //Center Display
        getWindow().getAttributes().gravity = Gravity.CENTER;
        ////Background transparency value range: 0~1
        getWindow().getAttributes().dimAmount = 0.5f;
    }
}