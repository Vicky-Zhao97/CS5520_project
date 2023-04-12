package com.example.loseit.ui.recipe;

import static com.example.loseit.CreateForumRecipeActivity.DB_COLLECTION_PATH;
import static com.example.loseit.CreateForumRecipeActivity.DB_FORUM_RECIPE_PATH;
import static com.example.loseit.StartActivity.DB_USER_INFO_PATH;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.loseit.R;
import com.example.loseit.RecipeDetailActivity;
import com.example.loseit.model.CollectItem;
import com.example.loseit.model.RecipeItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
        if (recipe.getAuthorId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            holder.mDeleteButton.setVisibility(View.VISIBLE);
            holder.mCollectButton.setVisibility(View.GONE);
        } else {
            holder.mDeleteButton.setVisibility(View.GONE);
            holder.mCollectButton.setVisibility(View.VISIBLE);
        }
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
        holder.mCollectButton.setOnClickListener(view -> {
            if (holder.mCollectButton.isSelected()){
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
                                holder.mCollectButton.setSelected(false);
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
                                holder.mCollectButton.setSelected(true);
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

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private TextView mAuthorTextView;
        private TextView mKcalTextView;
        private ImageView mImageView;
        private ImageView mDeleteButton;

        private ImageView mCollectButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitleTextView = itemView.findViewById(R.id.title);
            mDateTextView = itemView.findViewById(R.id.date);
            mAuthorTextView = itemView.findViewById(R.id.author);
            mKcalTextView = itemView.findViewById(R.id.calorie);
            mImageView = itemView.findViewById(R.id.image);
            mDeleteButton = itemView.findViewById(R.id.buttonDeleteRecipe);
            mCollectButton = itemView.findViewById(R.id.buttonCollect);
        }

        public void bind(RecipeItem recipe) {
            mTitleTextView.setText(recipe.getTitle());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            mDateTextView.setText(sdf.format(recipe.getCreationDate()));

            FirebaseFirestore.getInstance().collection(DB_USER_INFO_PATH)
                    .document(recipe.getAuthorId()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String authorName = documentSnapshot.getString("userName");
                            mAuthorTextView.setText("by " + authorName);
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
                                mCollectButton.setSelected(count > 0);
                            }
                        }
                    });
            mKcalTextView.setText(String.format(Locale.ENGLISH,"%.2f kCal", recipe.getTotalKcal()));

            if (recipe.getImageUrl() != "") {
                Glide.with(mImageView.getContext()).load(recipe.getImageUrl()).into(mImageView);
            } else {
                mImageView.setImageResource(R.drawable.ic_dinner);
            }
        }
    }
}

