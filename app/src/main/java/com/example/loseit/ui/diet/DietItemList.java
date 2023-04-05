package com.example.loseit.ui.diet;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.loseit.R;
import com.example.loseit.databinding.CardDietItemBinding;
import com.example.loseit.model.DietItem;

import java.util.ArrayList;
import java.util.Locale;

/**
 * custom view for showing a list of diet
 */
public class DietItemList extends LinearLayout {
    //diet list
    private ArrayList<DietItem> dietItems;
    private final Context mContext;
    private OnDietChangeListener dietChangeListener;


    public DietItemList(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        dietItems = new ArrayList<>();
    }

    public void setDietChangeListener(OnDietChangeListener dietChangeListener) {
        this.dietChangeListener = dietChangeListener;
    }

    /**
     * get diet item list
     * @return ArrayList<DietItem>
     */
    public ArrayList<DietItem> getDietItems() {
        return (ArrayList<DietItem>) dietItems.clone();
    }

    /**
     * listener for watching diet item change
     */
    public interface OnDietChangeListener {
        void notify(DietItemList dietItemList);
    }

    /**
     * get total kcal of diets
     *
     * @return int
     */
    public int getTotalKcal() {
        double kcal = 0.;
        for (DietItem dietItem : dietItems) {
            kcal += dietItem.getKcal();
        }
        return (int) Math.ceil(kcal);
    }

    /**
     * add diet item
     *
     * @param dietItem DietItem
     */
    public void addDietItem(DietItem dietItem) {
        dietItems.add(0, dietItem);
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_diet_item, null);
        CardDietItemBinding itemBinding = CardDietItemBinding.bind(view);
        itemBinding.tvDietItemName.setText(dietItem.getName());
        itemBinding.tvDietItemKcal.setText(String.format(Locale.ENGLISH,
                "%.2f KCAL", dietItem.getKcal()));
        addView(view, dietItems.size() - 1);
        //add listener for delete diet
        itemBinding.btnDietItemDelete.setOnClickListener(view1 -> {
            //show confirm deleting dialog
            AlertDialog dialog = new AlertDialog.Builder(mContext).create();
            dialog.setTitle(mContext.getString(R.string.hint));
            dialog.setMessage(mContext.getString(R.string.delete_diet_hint));
            //confirm delete
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, mContext.getString(R.string.yes),
                    (dialogInterface, i) -> {
                        deleteDietItem(dietItem, view);
                    });
            //cancel delete
            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, mContext.getString(R.string.cancel),
                    (dialogInterface, i) -> {
                        dialog.dismiss();
                    });
            dialog.show();
        });
        if (dietChangeListener != null) {
            dietChangeListener.notify(this);
        }
    }

    /**
     * add items
     *
     * @param dietItems ArrayList<DietItem>
     */
    public void addDietItems(ArrayList<DietItem> dietItems) {
        for (DietItem dietItem : dietItems) {
            addDietItem(dietItem);
        }
    }

    /**
     * clear all diet item
     */
    public void clear() {
        dietItems = new ArrayList<>();
        removeAllViews();
        if (dietChangeListener != null) {
            dietChangeListener.notify(this);
        }
    }

    /**
     * delete diet item
     *
     * @param dietItem DietItem
     * @param view     View
     */
    public void deleteDietItem(DietItem dietItem, View view) {
        dietItems.remove(dietItem);
        removeView(view);
        if (dietChangeListener != null) {
            dietChangeListener.notify(this);
        }
    }

    /**
     * enable or disable edit
     *
     * @param editable boolean
     */
    public void setEditable(boolean editable) {
        for (int i = 0; i < dietItems.size(); i++) {
            //get item view
            View view = getChildAt(i);
            CardDietItemBinding itemBinding = CardDietItemBinding.bind(view);
            //disable or enable delete button
            itemBinding.btnDietItemDelete.setEnabled(editable);
        }
    }
}
