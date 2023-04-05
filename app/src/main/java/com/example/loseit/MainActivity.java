package com.example.loseit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.loseit.model.UserInfo;
import com.example.loseit.ui.user_info.UserInfoFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.loseit.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    public static final String KEY_USER_INFO = "user_info";
    private MainViewModel mainViewModel;
    private ActivityMainBinding binding;
    private ActivityResultLauncher<Intent> resetUserInfoLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_recipe,
                R.id.navigation_forum, R.id.navigation_me)
                .build();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.navView, navController);
        //view model
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        //activity launcher for reset user information
        resetUserInfoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getData() != null
                            && result.getResultCode() == Activity.RESULT_OK) {
                        //get feedback result
                        UserInfo newUserInfo = (UserInfo) result.getData()
                                .getSerializableExtra(KEY_USER_INFO);
                        mainViewModel.setUserInfo(newUserInfo);
                    }
                });
    }

    /**
     * reset user information
     *
     *
     * @param userInfo UserInfo
     */
    public void resetUserInfo(UserInfo userInfo) {
        Intent intent = new Intent(this, ResetUserInfoActivity.class);
        intent.putExtra(KEY_USER_INFO, userInfo);
        resetUserInfoLauncher.launch(intent);
    }
}