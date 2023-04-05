package com.example.loseit.ui.diet;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

import com.example.loseit.R;
import com.example.loseit.databinding.CardDietBinding;
import com.example.loseit.model.DietItem;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

/**
 * custom view for show diet breakfast,lunch and dinner list on HomeFragment
 */
public class DietCard extends ConstraintLayout {
    //diet dialog tag
    public static final String TAG_DIET_DIALOG = "diet dialog";
    private final CardDietBinding binding;
    //FragmentManager for show dialog
    private FragmentManager fragmentManager;
    //listener for watching total kcal change
    private OnTotalKcalChangeListener totalKcalChangeListener;

    public DietCard(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.card_diet, this);
        binding = CardDietBinding.bind(view);
        TypedArray attrsArray = context.obtainStyledAttributes(attrs, R.styleable.DietCard);
        //set diet name
        String dietName = attrsArray.getString(R.styleable.DietCard_diet_name);
        binding.tvDietName.setText(dietName);
        //set diet icon
        Drawable dietIcon = attrsArray.getDrawable(R.styleable.DietCard_diet_icon);
        if (!Objects.equals(dietIcon, null)) {
            binding.dietIcon.setImageDrawable(dietIcon);
        }
        attrsArray.recycle();
        initView();
    }

    /**
     * listener for watching total kcal change
     */
    public interface OnTotalKcalChangeListener {
        void onChange(int totalKcal);
    }

    /**
     * set listener for watching total kcal change
     */
    public void setTotalKcalChangeListener(OnTotalKcalChangeListener totalKcalChangeListener) {
        this.totalKcalChangeListener = totalKcalChangeListener;
    }

    /**
     * set  fragmentManager for show dialog
     *
     * @param fragmentManager FragmentManager
     */
    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    /**
     * clear all diet item
     */
    public void clear() {
        binding.dietItemList.clear();
    }

    /**
     * init view
     */
    private void initView() {
        //listener for create diet item
        binding.buttonAddDiet.setOnClickListener(view -> {
            DietItemDialog dialog = new DietItemDialog();
            dialog.show(fragmentManager, TAG_DIET_DIALOG);
            dialog.setOnCloseListener(this::createDietItem);
        });
        //watch diet item change
        binding.dietItemList.setDietChangeListener(dietItemList -> {
            //refresh total KCAL
            int totalKcal = dietItemList.getTotalKcal();
            binding.tvDietKcal.setText(String.format(Locale.ENGLISH,
                    "%d KCAL", totalKcal));
            if (totalKcalChangeListener != null) {
                totalKcalChangeListener.onChange(totalKcal);
            }
        });

    }

    /**
     * get item list
     *
     * @return ArrayList<DietItem>
     */
    public ArrayList<DietItem> getDietItemList() {
        return binding.dietItemList.getDietItems();
    }

    /**
     * add new diet item
     *
     * @param newDietItem DietItem
     */
    private void createDietItem(DietItem newDietItem) {
        if (newDietItem == null) {
            return;
        }
        binding.dietItemList.addDietItem(newDietItem);
    }

    /**
     * add diet items to list
     *
     * @param dietItems ArrayList<DietItem>
     */
    public void addDietItems(ArrayList<DietItem> dietItems) {
        binding.dietItemList.addDietItems(dietItems);
    }

    /**
     * enable or disable edit
     *
     * @param editable boolean
     */
    public void setEditable(boolean editable) {
        binding.dietItemList.setEditable(editable);
        binding.buttonAddDiet.setEnabled(editable);
    }
}
