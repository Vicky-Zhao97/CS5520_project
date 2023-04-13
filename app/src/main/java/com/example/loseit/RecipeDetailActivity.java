package com.example.loseit;

import static com.example.loseit.StartActivity.DB_USER_INFO_PATH;
import static com.example.loseit.ui.recipe.RecipeAdapter.KEY_CLICKED_RECIPE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.loseit.model.RecipeItem;
import com.example.loseit.ui.recipe.IngredientsAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class RecipeDetailActivity extends AppCompatActivity {
    private RecipeItem mRecipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        mRecipe = (RecipeItem) getIntent().getSerializableExtra(KEY_CLICKED_RECIPE);
        // show mRecipe details
        ImageView imageView = findViewById(R.id.image);
        if (mRecipe.getImageUrl() != "") {
            Glide.with(imageView.getContext()).load(mRecipe.getImageUrl()).into(imageView);
        } else {
            imageView.setImageResource(0);
            imageView.getLayoutParams().height = 0;
        }

        TextView titleTextView = findViewById(R.id.title);
        titleTextView.setText(mRecipe.getTitle());

        TextView dateTextView = findViewById(R.id.date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dateTextView.setText(sdf.format(mRecipe.getCreationDate()));

        TextView authorTextView = findViewById(R.id.author);
        FirebaseFirestore.getInstance().collection(DB_USER_INFO_PATH)
                .document(mRecipe.getAuthorId()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String authorName = documentSnapshot.getString("userName");
                        authorTextView.setText("by " + authorName);
                    }
                });

        TextView kcalTextView = findViewById(R.id.calorie);
        kcalTextView.setText(String.format(Locale.ENGLISH,"%.2f kCal", mRecipe.getTotalKcal()));

        RecyclerView recyclerView = findViewById(R.id.ingredients_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        IngredientsAdapter adapter = new IngredientsAdapter(mRecipe.getIngredients());
        recyclerView.setAdapter(adapter);

        TextView descriptionTextView = findViewById(R.id.description);
        descriptionTextView.setText(mRecipe.getDescription());
    }
}