package com.example.loseit.ui.forum;

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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.loseit.CreateForumRecipeActivity;
import com.example.loseit.R;
import com.example.loseit.RecipeDetailActivity;
import com.example.loseit.databinding.FragmentForumBinding;
import com.example.loseit.model.CollectItem;
import com.example.loseit.model.RecipeItem;
import com.example.loseit.ui.recipe.RecipeAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGAStickinessRefreshViewHolder;

public class ForumFragment extends Fragment implements BGARefreshLayout.BGARefreshLayoutDelegate {

    private FragmentForumBinding binding;
    private RecipeAdapter mAdapter;
    private ArrayList<RecipeItem> mRecipes = new ArrayList<>();
    private CollectionReference mRecipesRef = FirebaseFirestore.getInstance().collection(DB_FORUM_RECIPE_PATH);
    private BGARecyclerViewAdapter bgaRefreshLayoutAdapter;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentForumBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.createButton.setOnClickListener(view -> {
            createRecipeActivity();
        });

        initRefreshLayout();
        binding.recycler.setLayoutManager(new StaggeredGridLayoutManager(2,RecyclerView.VERTICAL));
        bgaRefreshLayoutAdapter = new BGARecyclerViewAdapter<RecipeItem>(binding.recycler, R.layout.card_recipe_item_2) {
            @Override
            protected void fillData(BGAViewHolderHelper helper, int position, RecipeItem recipe) {
                helper.getTextView(R.id.title).setText(recipe.getTitle());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                helper.getTextView(R.id.date).setText(sdf.format(recipe.getCreationDate()));

                if (recipe.getAuthorId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    helper.getImageView(R.id.buttonDeleteRecipe).setVisibility(View.GONE);
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

                helper.getTextView(R.id.calorie).setText(String.format(Locale.ENGLISH,"%.0f kCal", recipe.getTotalKcal()));
                if (!TextUtils.isEmpty(recipe.getImageUrl())){
                    Glide.with(helper.getConvertView().getContext()).load(recipe.getImageUrl()).into(helper.getImageView(R.id.image));
                }else{
                    helper.getImageView(R.id.image).setImageResource(R.mipmap.ic_launcher);
                }

                helper.getConvertView().setOnClickListener(view -> {
                    Intent intent = new Intent(view.getContext(), RecipeDetailActivity.class);
                    intent.putExtra(KEY_CLICKED_RECIPE, recipe);
                    view.getContext().startActivity(intent);
                });

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
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(view.getContext(), "Failed to collect recipe", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }else{
                        CollectItem collectItem = new CollectItem();
                        collectItem.setRecipeId(recipe.getId());
                        collectItem.setUserID(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        collectItem.setCreatedAt(new Date());
                        FirebaseFirestore.getInstance().collection(DB_COLLECTION_PATH)
                                .add(collectItem)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        helper.getImageView(R.id.buttonCollect).setSelected(true);
                                        Toast.makeText(view.getContext(), "Recipe collected", Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });

                    }
                });
            }
        };
        binding.recycler.setAdapter(bgaRefreshLayoutAdapter);
        binding.refreshlayout.beginRefreshing();
        return root;
    }

    private void createRecipeActivity() {
        Intent intent = new Intent(getActivity(), CreateForumRecipeActivity.class);
        startActivity(intent);
    }

    private void initData() {
        mRecipesRef.orderBy("creationDate", Query.Direction.DESCENDING)
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
                    mRecipes.clear();
                    mRecipes.addAll(recipes);
                    bgaRefreshLayoutAdapter.setData(recipes);
                    if (binding==null)
                        return;
                    binding.refreshlayout.endRefreshing();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void initRefreshLayout() {
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