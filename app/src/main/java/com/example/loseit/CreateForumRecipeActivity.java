package com.example.loseit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.loseit.databinding.ActivityCreateForumRecipeBinding;
import com.example.loseit.model.DietItem;
import com.example.loseit.model.RecipeItem;
import com.example.loseit.ui.diet.DietItemDialog;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateForumRecipeActivity extends AppCompatActivity {
    ActivityCreateForumRecipeBinding binding;
    public static final String DB_FORUM_RECIPE_PATH = "recipes";
    public static final String TAG_INGREDIENT_DIALOG = "add ingredient dialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateForumRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonAddIngredient.setOnClickListener(view -> {
            DietItemDialog dialog = new DietItemDialog();
            dialog.show(getSupportFragmentManager(), TAG_INGREDIENT_DIALOG);
            dialog.setOnCloseListener((DietItem newDietItem) -> {
                if (newDietItem == null) {
                    return;
                }
                binding.ingredientsList.addDietItem(newDietItem);
            });
        });

        binding.ingredientsList.setDietChangeListener(dietItemList -> {
            int totalKcal = dietItemList.getTotalKcal();
            binding.editTotalKcal.setText(String.valueOf(totalKcal));
        });

        binding.buttonCreationSubmit.setOnClickListener(view -> {
            if (saveRecipe()) {     // recipe creation succeeded
                onBackPressed();
            }
        });
        binding.buttonCreationCancel.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    /**
     * create recipe when user click OK.
     * @return whether a new recipe is created successfully and added to the database.
     */
    private boolean saveRecipe() {
        binding.errorMsg.setText("");
        String title = binding.editRecipeTitle.getText().toString();
        if (title.equals("")) {
            binding.errorMsg.setText("Title cannot be empty!");
            return false;
        }
        String description = binding.editDescription.getText().toString();
        if (description.equals("")) {
            binding.errorMsg.setText("Description cannot be empty!");
            return false;
        }
        String totalKcalString = binding.editTotalKcal.getText().toString();
        if (totalKcalString.equals("")) {
            binding.errorMsg.setText("Total kCal cannot be empty!");
            return false;
        }
        double totalKcal = Double.parseDouble(totalKcalString);

        RecipeItem newRecipe = new RecipeItem(title, description,
                binding.ingredientsList.getDietItems(), totalKcal);
        // update database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(DB_FORUM_RECIPE_PATH).add(newRecipe)
                .addOnSuccessListener(documentReference -> {
                    String recipeId = documentReference.getId();
                    Log.d("CreateForumRecipe", "create recipe: " + recipeId);
                })
                .addOnFailureListener(e -> {
                    Log.e("CreateForumRecipe", "create recipe failed");
                });
        return true;
    }
}