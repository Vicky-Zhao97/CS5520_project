package com.example.loseit.ui.forum;

import static com.example.loseit.CreateForumRecipeActivity.DB_FORUM_RECIPE_PATH;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loseit.CreateForumRecipeActivity;
import com.example.loseit.databinding.FragmentForumBinding;
import com.example.loseit.model.RecipeItem;
import com.example.loseit.ui.recipe.RecipeAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ForumFragment extends Fragment {

    private FragmentForumBinding binding;
    private RecipeAdapter mAdapter;
    private ArrayList<RecipeItem> mRecipes = new ArrayList<>();
    private CollectionReference mRecipesRef = FirebaseFirestore.getInstance().collection(DB_FORUM_RECIPE_PATH);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentForumBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.createButton.setOnClickListener(view -> {
            createRecipeActivity();
        });

        RecyclerView recipeListView = binding.recipesList;
        recipeListView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mAdapter = new RecipeAdapter(mRecipes);
        recipeListView.setAdapter(mAdapter);
        // Listen for realtime updates from Firestore
        mRecipesRef.orderBy("creationDate", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
            if (e != null) { return; }   // Error occurred
            ArrayList<RecipeItem> recipes = new ArrayList<>();
            for (QueryDocumentSnapshot doc : snapshots) {
                RecipeItem recipe = doc.toObject(RecipeItem.class);
                recipes.add(recipe);
            }
            mRecipes.clear();
            mRecipes.addAll(recipes);
            mAdapter.notifyDataSetChanged();
        });
        return root;
    }

    private void createRecipeActivity() {
        Intent intent = new Intent(getActivity(), CreateForumRecipeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}