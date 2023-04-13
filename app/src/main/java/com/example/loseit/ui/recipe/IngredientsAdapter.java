package com.example.loseit.ui.recipe;

import static com.example.loseit.CreateForumRecipeActivity.DB_FORUM_RECIPE_PATH;
import static com.example.loseit.StartActivity.DB_USER_INFO_PATH;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.loseit.R;
import com.example.loseit.RecipeDetailActivity;
import com.example.loseit.model.DietItem;
import com.example.loseit.model.RecipeItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {

    private ArrayList<DietItem> mIngredients;
    public IngredientsAdapter(ArrayList<DietItem> ingredients) {
        mIngredients = ingredients;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_ingredient_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mIngredients != null && !mIngredients.isEmpty()) {
            DietItem ingredient = mIngredients.get(position);
            holder.bind(ingredient);
        } else {
            holder.mNameTextView.setText("/");
            holder.mKcalTextView.setText("");
        }
    }

    @Override
    public int getItemCount() {
        // show 1 card_ingredient_item with "/" when mIngredients is empty
        return mIngredients != null && !mIngredients.isEmpty() ? mIngredients.size() : 1;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mNameTextView;
        private TextView mKcalTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mNameTextView = itemView.findViewById(R.id.name);
            mKcalTextView = itemView.findViewById(R.id.kcal);
        }

        public void bind(DietItem ingredient) {
            mNameTextView.setText(ingredient.getName());
            mKcalTextView.setText(String.format(Locale.ENGLISH,"%.2f kCal", ingredient.getKcal()));
        }
    }
}

