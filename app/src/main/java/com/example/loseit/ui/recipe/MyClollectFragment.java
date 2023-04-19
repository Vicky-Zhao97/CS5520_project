package com.example.loseit.ui.recipe;

import static com.example.loseit.CreateForumRecipeActivity.DB_COLLECTION_PATH;
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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.loseit.R;
import com.example.loseit.RecipeDetailActivity;
import com.example.loseit.databinding.FragmentMyCollectBinding;
import com.example.loseit.databinding.FragmentRecipeBinding;
import com.example.loseit.model.CollectItem;
import com.example.loseit.model.RecipeItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGAStickinessRefreshViewHolder;

public class MyClollectFragment extends Fragment implements BGARefreshLayout.BGARefreshLayoutDelegate {

    private FragmentMyCollectBinding binding;
    private ArrayList<RecipeItem> mRecipes = new ArrayList<>();
    private CollectionReference mRecipesRef = FirebaseFirestore.getInstance().collection(DB_FORUM_RECIPE_PATH);

    private BGARecyclerViewAdapter bgaRefreshLayoutAdapter;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMyCollectBinding.inflate(inflater, container, false);
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


                FirebaseFirestore.getInstance().collection(DB_COLLECTION_PATH)
                        .whereEqualTo("recipeId", recipe.getId())
                        .whereEqualTo("userID", FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .count().get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    AggregateQuerySnapshot snapshot = task.getResult();
                                    long count = snapshot.getCount();
                                    helper.getImageView(R.id.buttonCollect).setSelected(count > 0);
                                }
                            }
                        });

                helper.getTextView(R.id.calorie).setText(String.format(Locale.ENGLISH,"%.2f kCal", recipe.getTotalKcal()));
                if (!TextUtils.isEmpty(recipe.getImageUrl())){
                    Glide.with(helper.getConvertView().getContext()).load(recipe.getImageUrl()).into(helper.getImageView(R.id.image));
                }else{
                    helper.getImageView(R.id.image).setImageResource(R.drawable.ic_dinner);
                }


                helper.getImageView(R.id.buttonCollect).setOnClickListener(view -> {
                    if (helper.getImageView(R.id.buttonCollect).isSelected()){
                        FirebaseFirestore.getInstance().collection(DB_COLLECTION_PATH)
                                .whereEqualTo("recipeId", recipe.getId())
                                .whereEqualTo("userID", FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                                        for (DocumentSnapshot document:documents){
                                            document.getReference().delete();
                                        }
                                        helper.getImageView(R.id.buttonCollect).setSelected(false);
                                        Toast.makeText(view.getContext(), "Recipe cancel collect", Toast.LENGTH_SHORT).show();
                                        initData();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(view.getContext(), "Failed to collect recipe", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

                helper.getConvertView().setOnClickListener(view -> {
                    Intent intent = new Intent(view.getContext(), RecipeDetailActivity.class);
                    intent.putExtra(KEY_CLICKED_RECIPE, recipe);
                    view.getContext().startActivity(intent);
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

        FirebaseFirestore.getInstance().collection(DB_COLLECTION_PATH)
                .whereEqualTo("userID", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                        if (error!=null){
                            if (binding!=null)
                                binding.refreshlayout.endRefreshing();
                            return;
                        }
                        ArrayList<String> recipeIds = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            CollectItem collect = doc.toObject(CollectItem.class);
                            recipeIds.add(collect.getRecipeId());
                        }
                        if (recipeIds.size()==0){
                            if (binding!=null){
                                binding.refreshlayout.endRefreshing();
                            }
                            mRecipes.clear();
                            bgaRefreshLayoutAdapter.clear();
                            return;
                        }
                        // Listen for realtime updates from Firestore
                        mRecipesRef.whereIn("id",recipeIds)
                                .addSnapshotListener((sss, e) -> {
                                    if (e != null) {
                                        if (binding!=null)
                                            binding.refreshlayout.endRefreshing();
                                        return;
                                    }   // Error occurred
                                    ArrayList<RecipeItem> recipes = new ArrayList<>();
                                    for (QueryDocumentSnapshot doc : sss) {
                                        RecipeItem recipe = doc.toObject(RecipeItem.class);
                                        recipes.add(recipe);
                                    }
                                    Collections.reverse(recipes);
                                    mRecipes.clear();
                                    mRecipes.addAll(recipes);
                                    bgaRefreshLayoutAdapter.setData(mRecipes);
                                    if (binding==null)
                                        return;
                                    binding.refreshlayout.endRefreshing();
                                });
                    }
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