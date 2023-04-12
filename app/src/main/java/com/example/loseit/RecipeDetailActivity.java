package com.example.loseit;

import static com.example.loseit.ui.recipe.RecipeAdapter.KEY_CLICKED_RECIPE;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.loseit.model.RecipeItem;

public class RecipeDetailActivity extends AppCompatActivity {
    private RecipeItem mRecipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        mRecipe = (RecipeItem) getIntent().getSerializableExtra(KEY_CLICKED_RECIPE);
        // show mRecipe details
        TextView titleTextView = findViewById(R.id.title);
        titleTextView.setText(mRecipe.getTitle());
    }
}