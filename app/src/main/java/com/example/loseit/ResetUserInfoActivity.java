package com.example.loseit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.loseit.databinding.ActivityResetUserInfoBinding;
import com.example.loseit.model.UserInfo;
import com.example.loseit.ui.user_info.UserInfoFragment;

/**
 * activity for reset user information
 */
public class ResetUserInfoActivity extends AppCompatActivity {
    private ActivityResetUserInfoBinding binding;
    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetUserInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //get the user information passed from MainActivity
        userInfo = (UserInfo) getIntent().
                getSerializableExtra(MainActivity.KEY_USER_INFO);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        UserInfoFragment userInfoFragment = UserInfoFragment.newInstance(true);
        if (userInfo != null) {
            //set current user information to userInfoFragment after it has been created,
            // so that user can modified their information
            userInfoFragment.setViewCreatedListener(userInfoFragment1 -> {
                //set user information to view
                userInfoFragment1.setUserInfo(userInfo);
            });
        }
        //go to MainActivity after user information has been submitted to server
        userInfoFragment.setSubmittedUserInfoListener(userInfo -> {
            Intent intent = new Intent(ResetUserInfoActivity.this,
                    MainActivity.class);
            intent.putExtra(MainActivity.KEY_USER_INFO, userInfo);
            setResult(MainActivity.RESULT_OK, intent);
            //finish current activity
            finish();
        });
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_reset_user_info,
                userInfoFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }
}