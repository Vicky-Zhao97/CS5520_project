package com.example.loseit.ui.diet;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loseit.databinding.CardDietItemBinding;
import com.example.loseit.model.DietItem;

import java.util.ArrayList;

/**
 * adapter for diet list
 */
public class DietCardAdapter extends RecyclerView.Adapter<DietCardAdapter.DietCardViewHolder> {
    //diet list
    public final ArrayList<DietItem> dietItems;

    public DietCardAdapter() {
        dietItems = new ArrayList<>();
    }

    /**
     * add diet item
     *
     * @param dietItem DietItem
     */
    public void addDietItem(DietItem dietItem) {
        dietItems.add(0, dietItem);
        notifyItemChanged(0);
    }

    @NonNull
    @Override
    public DietCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardDietItemBinding binding = CardDietItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new DietCardViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DietCardViewHolder holder, int position) {
        holder.bind(dietItems.get(position));
    }

    @Override
    public int getItemCount() {
        return dietItems.size();
    }

    /**
     * view holder
     */
    public static class DietCardViewHolder extends RecyclerView.ViewHolder {
        private final CardDietItemBinding binding;

        public DietCardViewHolder(@NonNull CardDietItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        /**
         * bind date with view
         *
         * @param dietItem DietItem
         */
        public void bind(DietItem dietItem) {
            binding.tvDietItemName.setText(dietItem.getName());
            binding.tvDietItemKcal.setText(String.valueOf(dietItem.getKcal())+"KCAL");
        }
    }
}
