package com.example.loseit.ui.recipe;

import static com.example.loseit.CreateForumRecipeActivity.DB_FORUM_RECIPE_PATH;

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
import com.example.loseit.model.RecipeItem;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private ArrayList<RecipeItem> mRecipes;
    public RecipeAdapter(ArrayList<RecipeItem> recipes) {
        mRecipes = recipes;
    }
    public static final String KEY_CLICKED_RECIPE = "clicked_recipe";

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_recipe_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecipeItem recipe = mRecipes.get(position);
        holder.bind(recipe);
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), RecipeDetailActivity.class);
            intent.putExtra(KEY_CLICKED_RECIPE, recipe);
            view.getContext().startActivity(intent);
        });
        holder.mDeleteButton.setOnClickListener(view -> {
            FirebaseFirestore.getInstance().collection(DB_FORUM_RECIPE_PATH).document(recipe.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(view.getContext(), "Recipe deleted", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(view.getContext(), "Failed to delete recipe", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private TextView mKcalTextView;
        private ImageView mImageView;
        private ImageView mDeleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitleTextView = itemView.findViewById(R.id.title);
            mDateTextView = itemView.findViewById(R.id.date);
            mKcalTextView = itemView.findViewById(R.id.calorie);
            mImageView = itemView.findViewById(R.id.image);
            mDeleteButton = itemView.findViewById(R.id.buttonDeleteRecipe);
        }

        public void bind(RecipeItem recipe) {
            mTitleTextView.setText(recipe.getTitle());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            mDateTextView.setText(sdf.format(recipe.getCreationDate()));
            mKcalTextView.setText(String.format(Locale.ENGLISH,"%.2f kCal", recipe.getTotalKcal()));
            if (recipe.getImageUrl() != "") {
                Glide.with(mImageView.getContext()).load(recipe.getImageUrl()).into(mImageView);
            } else {
                mImageView.setImageResource(R.drawable.ic_dinner);
            }
        }
    }
}

