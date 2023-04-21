package com.example.loseit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.loseit.databinding.ActivityStartBinding;
import com.example.loseit.ui.user_info.UserInfoFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.atomic.AtomicInteger;

public class StartActivity extends AppCompatActivity {
    public static final String DB_USER_INFO_PATH = "lose_weight";
    private ActivityStartBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Glide.with(this)
                .load(R.drawable.login_bg)
                .centerCrop()
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        binding.getRoot().setBackground(resource);
                    }
                });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10010);
        }
    }

    /**
     * show user information fragment
     */
    public void gotoUserInfo() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        UserInfoFragment userInfoFragment = UserInfoFragment.newInstance(false);
        //go to MainActivity after user information has been submitted to server
        userInfoFragment.setSubmittedUserInfoListener(userInfo -> {
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent);
            //finish current activity
            finish();
        });
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_start,
                userInfoFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }


    /**
     * show message
     *
     * @param msg message content
     */
    private void showMsg(String msg, int color) {
        Snackbar snackbar = Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_SHORT);
        View mView = snackbar.getView();
        mView.setBackgroundColor(color);
        snackbar.show();
    }
}