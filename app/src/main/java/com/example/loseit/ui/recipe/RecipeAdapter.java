package com.example.loseit.ui.recipe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loseit.R;
import com.example.loseit.model.RecipeItem;

import java.util.ArrayList;
import java.util.Locale;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private ArrayList<RecipeItem> mRecipes;

    public RecipeAdapter(ArrayList<RecipeItem> recipes) {
        mRecipes = recipes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_recipe_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecipeItem recipe = mRecipes.get(position);
        holder.bind(recipe);
    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleTextView;
        private TextView mKcalTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitleTextView = itemView.findViewById(R.id.title);
            mKcalTextView = itemView.findViewById(R.id.calorie);
        }

        public void bind(RecipeItem recipe) {
            mTitleTextView.setText(recipe.getTitle());
            mKcalTextView.setText(String.format(Locale.ENGLISH,"%.2f KCAL", recipe.getTotalKcal()));
        }
    }
}

