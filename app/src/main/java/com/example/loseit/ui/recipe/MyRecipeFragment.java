package com.example.loseit.ui.recipe;

import static com.example.loseit.CreateForumRecipeActivity.DB_FORUM_RECIPE_PATH;
import static com.example.loseit.StartActivity.DB_USER_INFO_PATH;
import static com.example.loseit.ui.recipe.RecipeAdapter.KEY_CLICKED_RECIPE;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.loseit.R;
import com.example.loseit.RecipeDetailActivity;
import com.example.loseit.databinding.FragmentMyRecipeBinding;
import com.example.loseit.databinding.FragmentRecipeBinding;
import com.example.loseit.model.RecipeItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGAStickinessRefreshViewHolder;

public class MyRecipeFragment extends Fragment implements BGARefreshLayout.BGARefreshLayoutDelegate {

    private FragmentMyRecipeBinding binding;
    private ArrayList<RecipeItem> mRecipes = new ArrayList<>();
    private CollectionReference mRecipesRef = FirebaseFirestore.getInstance().collection(DB_FORUM_RECIPE_PATH);

    private BGARecyclerViewAdapter bgaRefreshLayoutAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMyRecipeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initRefreshLayout();
        binding.recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        bgaRefreshLayoutAdapter = new BGARecyclerViewAdapter<RecipeItem>(binding.recycler, R.layout.card_recipe_item) {
            @Override
            protected void fillData(BGAViewHolderHelper helper, int position, RecipeItem recipe) {
                helper.getTextView(R.id.title).setText(recipe.getTitle());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                helper.getTextView(R.id.date).setText(sdf.format(recipe.getCreationDate()));

                if (recipe.getAuthorId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    helper.getImageView(R.id.buttonDeleteRecipe).setVisibility(View.VISIBLE);
                    helper.getImageView(R.id.buttonCollect).setVisibility(View.GONE);
                } else {
                    helper.getImageView(R.id.buttonDeleteRecipe).setVisibility(View.GONE);
                    helper.getImageView(R.id.buttonCollect).setVisibility(View.VISIBLE);
                }

                FirebaseFirestore.getInstance().collection(DB_USER_INFO_PATH)
                        .document(recipe.getAuthorId()).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String authorName = documentSnapshot.getString("userName");
                                helper.getTextView(R.id.author).setText("by " + authorName);
                            }
                        });

                helper.getTextView(R.id.calorie).setText(String.format(Locale.ENGLISH,"%.2f kCal", recipe.getTotalKcal()));
                if (!TextUtils.isEmpty(recipe.getImageUrl())){
                    Glide.with(helper.getConvertView().getContext()).load(recipe.getImageUrl()).into(helper.getImageView(R.id.image));
                }else{
                    helper.getImageView(R.id.image).setImageResource(R.drawable.ic_dinner);
                }

                helper.getConvertView().setOnClickListener(view -> {
                    Intent intent = new Intent(view.getContext(), RecipeDetailActivity.class);
                    intent.putExtra(KEY_CLICKED_RECIPE, recipe);
                    view.getContext().startActivity(intent);
                });
                helper.getImageView(R.id.buttonDeleteRecipe).setOnClickListener(view -> {
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
        };
        binding.recycler.setAdapter(bgaRefreshLayoutAdapter);
        binding.refreshlayout.beginRefreshing();
        return root;
    }


    public void initData(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();
        if (TextUtils.isEmpty(uid))
            return;
        // Listen for realtime updates from Firestore
        mRecipesRef.whereEqualTo("authorId",currentUser.getUid())
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        if (binding!=null)
                            binding.refreshlayout.endRefreshing();
                        return;
                    }
                    ArrayList<RecipeItem> recipes = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        RecipeItem recipe = doc.toObject(RecipeItem.class);
                        recipes.add(recipe);
                    }
                    Collections.reverse(recipes);
                    mRecipes.clear();
                    mRecipes.addAll(recipes);
                    if (binding!=null)
                        binding.refreshlayout.endRefreshing();
                    bgaRefreshLayoutAdapter.setData(mRecipes);
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initRefreshLayout() {
        binding.refreshlayout.setDelegate(this);
        BGAStickinessRefreshViewHolder refreshViewHolder = new BGAStickinessRefreshViewHolder(getContext(),true);
        refreshViewHolder.setRotateImage(R.mipmap.ic_launcher);
        refreshViewHolder.setStickinessColor(R.color.primary_green);
        binding.refreshlayout.setRefreshViewHolder(refreshViewHolder);
        binding.refreshlayout.setIsShowLoadingMoreView(false);
        refreshViewHolder.setLoadingMoreText("loading");
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        initData();

    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }
}