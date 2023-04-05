package com.example.loseit.ui.user_info;

import android.view.View;

import androidx.appcompat.content.res.AppCompatResources;

import com.example.loseit.R;
import com.example.loseit.databinding.PanelGenderBinding;

import java.util.Objects;

/**
 * class for set gender
 */
public class GenderPanelViewSetting extends UserInfoViewSetting {
    public static final String MALE = "male";
    public static final String FEMALE = "female";
    //current select gender
    private String selectedGender = FEMALE;
    private PanelGenderBinding binding;

    public GenderPanelViewSetting(String contentViewName) {
        super(contentViewName);
    }

    /**
     * bind data with view
     *
     * @param container View
     */
    public void bind(View container) {
        binding = PanelGenderBinding.bind(container);
        binding.buttonFemale.setOnClickListener(view -> {
            selectedGender = FEMALE;
            //set checked button background
            setButtonBg(binding);
        });
        binding.buttonMale.setOnClickListener(view -> {
            selectedGender = MALE;
            //set checked button background
            setButtonBg(binding);
        });
        setButtonBg(binding);
    }

    /**
     * set background for button selected
     *
     * @param binding PanelGenderBinding
     */
    private void setButtonBg(PanelGenderBinding binding) {
        if (Objects.equals(selectedGender, FEMALE)) {
            binding.buttonFemale.setBackground(
                    AppCompatResources.getDrawable(binding.getRoot().getContext(),
                            R.drawable.green_round_rect)
            );
            binding.buttonMale.setBackground(
                    AppCompatResources.getDrawable(binding.getRoot().getContext(),
                            R.drawable.white_stroke_rect)
            );
        } else {
            binding.buttonMale.setBackground(
                    AppCompatResources.getDrawable(binding.getRoot().getContext(),
                            R.drawable.green_round_rect)
            );
            binding.buttonFemale.setBackground(
                    AppCompatResources.getDrawable(binding.getRoot().getContext(),
                            R.drawable.white_stroke_rect)
            );
        }
    }

    /**
     * get selected gender
     *
     * @return String
     */
    public String getGender() {
        return selectedGender;
    }

    /**
     * set selected gender
     *
     * @param gender gender
     */
    public void setGender(String gender) {
        selectedGender = gender;
    }
}
