package com.example.loseit.ui.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.loseit.MainActivity;
import com.example.loseit.MainViewModel;
import com.example.loseit.R;
import com.example.loseit.StartActivity;
import com.example.loseit.databinding.FragmentMeBinding;
import com.example.loseit.model.UserInfo;

import org.checkerframework.checker.units.qual.C;

import java.util.Calendar;
import java.util.Locale;


public class MeFragment extends Fragment {

    private FragmentMeBinding binding;
    private MainViewModel mainViewModel;

    public MeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMeBinding.inflate(inflater, container, false);
        //get view model hold by activity
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        //observe user information change
        mainViewModel.observeUserInfo().observe(requireActivity(), this::showUserInfo);
        //listener for log out
        binding.buttonLogOut.setOnClickListener(view -> {
            logOut();
        });
        //listener for reset user information
        binding.buttonResetInfo.setOnClickListener(view -> {
            resetUserInfo();
        });
        return binding.getRoot();
    }

    /**
     * log out
     */
    private void logOut() {
        //show confirm deleting dialog
        Context mContext = binding.getRoot().getContext();
        AlertDialog dialog = new AlertDialog.Builder(mContext).create();
        dialog.setTitle(mContext.getString(R.string.hint));
        dialog.setMessage(getString(R.string.msg_confirm_log_out));
        //confirm
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, mContext.getString(R.string.yes),
                (dialogInterface, i) -> {
                    mainViewModel.signOut();
                    //go to StartActivity
                    Intent intent = new Intent(getContext(), StartActivity.class);
                    mContext.startActivity(intent);
                    //finish current activity
                    requireActivity().finish();
                });
        //cancel
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, mContext.getString(R.string.cancel),
                (dialogInterface, i) -> {
                    dialog.dismiss();
                });
        dialog.show();
    }

    /**
     * reset user information
     */
    private void resetUserInfo() {
        MainActivity mainActivity = (MainActivity) requireActivity();
        mainViewModel.fetchUserInfo((userInfo, successful) -> {
            mainActivity.resetUserInfo(userInfo);
        });
    }

    /**
     * fill user information to table
     *
     * @param userInfo UserInfo
     */
    private void showUserInfo(UserInfo userInfo) {
        binding.meUserName.setText(userInfo.getUserName());
        binding.meEmail.setText(userInfo.getEmail());
        binding.meGender.setText(userInfo.getGender());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(userInfo.getBirth());
        int year = calendar.get(Calendar.YEAR);
        int mon = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        binding.meBirth.setText(String.format(Locale.ENGLISH,
                "%d/%d/%d", year, mon, day));
        binding.meHeight.setText(String.format(Locale.ENGLISH,
                "%.1f", userInfo.getHeight()));
        binding.meCurrentWeight.setText(String.format(Locale.ENGLISH,
                "%.1f", userInfo.getCurrentWeight()));
        binding.meGoalWeight.setText(String.format(Locale.ENGLISH,
                "%.1f", userInfo.getGoalWeight()));
    }
}