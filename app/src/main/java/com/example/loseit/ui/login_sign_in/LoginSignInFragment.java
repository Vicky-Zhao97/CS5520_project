package com.example.loseit.ui.login_sign_in;

import static com.example.loseit.StartActivity.DB_USER_INFO_PATH;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.loseit.MainActivity;
import com.example.loseit.R;
import com.example.loseit.StartActivity;
import com.example.loseit.databinding.FragmentLoginSignInBinding;
import com.example.loseit.databinding.PanelSignInBinding;
import com.example.loseit.databinding.PanelLoginBinding;
import com.example.loseit.utils.SPUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * fragment for sign up  and login
 */
public class LoginSignInFragment extends Fragment {
    //Minimum password length
    public static final int MIN_PASS_LEN = 6;
    private FragmentLoginSignInBinding binding;
    private FirebaseAuth mAuth;

    public LoginSignInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoginSignInBinding.inflate(inflater, container, false);
        binding.loginPanel.tvSignInHint.setOnClickListener(view -> {
            //hide login and show sign in when tvSignInHint is clicked
            binding.loginPanel.getRoot().setVisibility(View.GONE);
            binding.signInPanel.getRoot().setVisibility(View.VISIBLE);
        });
        binding.signInPanel.tvLoginInHint.setOnClickListener(view -> {
            //hide sign in and show login when tvLoginInHint is clicked
            binding.signInPanel.getRoot().setVisibility(View.GONE);
            binding.loginPanel.getRoot().setVisibility(View.VISIBLE);
        });
        binding.loginPanel.buttonLogin.setOnClickListener(view -> {
            login();
        });
        binding.signInPanel.buttonSignIn.setOnClickListener(view -> {
            signIn();
        });
        //TODO delete this after debug
//        PanelLoginBinding loginPanel = binding.loginPanel;
//        loginPanel.editEmailLoginIn.setText("1@q.com");
//        loginPanel.editPasswordLoginIn.setText("111111");
//        login();
//        PanelSignInBinding signInPanel = binding.signInPanel;
//        signInPanel.editEmail.setText("2@q.com");
//        signInPanel.editUsernameSignIn.setText("wwww");
//        signInPanel.editPasswordSignIn.setText("111111");

        PanelLoginBinding loginPanel = binding.loginPanel;
        String email = SPUtils.getString("email", "");
        String password = SPUtils.getString("password","");
        if (!TextUtils.isEmpty(email)){
            loginPanel.editEmailLoginIn.setText(email);
        }
        if (!TextUtils.isEmpty(password)){
            loginPanel.editPasswordLoginIn.setText(password);
        }
        return binding.getRoot();
    }

    /**
     * switch to user information fragment
     */
    private void gotoUserInfo() {
        StartActivity activity = (StartActivity) getActivity();
        assert activity != null;
        activity.gotoUserInfo();
    }

    /**
     * login
     */
    private void login() {
        PanelLoginBinding loginPanel = binding.loginPanel;
        String email = loginPanel.editEmailLoginIn.getText().toString();
        String password = loginPanel.editPasswordLoginIn.getText().toString();
        Context context = binding.getRoot().getContext();
        if (Objects.equals(email, "")) {
            //check email
            showMsg(context.getString(R.string.msg_empty_email_error),
                    binding.getRoot().getContext().getColor(R.color.error_msg_bg));
            return;
        }
        if (!isValidEmail(email)) {
            //check email
            showMsg(context.getString(R.string.msg_invalid_email_error),
                    binding.getRoot().getContext().getColor(R.color.error_msg_bg));
        }
        if (Objects.equals(password, "")) {
            //check password
            showMsg(context.getString(R.string.msg_empty_password_error),
                    binding.getRoot().getContext().getColor(R.color.error_msg_bg));
            return;
        }
        showLoginLoading(true);
        Activity activity = getActivity();
        assert activity != null;
        //firebase auth
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        SPUtils.putString("email",email);
                        SPUtils.putString("password",password);
                        checkUserInfo(Objects.requireNonNull(mAuth.getCurrentUser()));
                    } else {
                        // If sign in fails, display a message to the user.
                        //Log.w("TAG", "signInWithEmail:failure", task.getException());
                        showMsg(getString(R.string.error_msg_longin_failed),
                                binding.getRoot().getContext().getColor(R.color.error_msg_bg));
                        showLoginLoading(false);
                    }
                });
    }

    /**
     * show or hide loading for login
     *
     * @param show true means showing
     */
    private void showLoginLoading(boolean show) {
        if (show) {
            //show loading and hide login button
            binding.loginPanel.progressLogin.setVisibility(View.VISIBLE);
            binding.loginPanel.buttonLogin.setVisibility(View.INVISIBLE);
        } else {
            //hide loading and show login button
            binding.loginPanel.progressLogin.setVisibility(View.INVISIBLE);
            binding.loginPanel.buttonLogin.setVisibility(View.VISIBLE);
        }
    }

    /**
     * check if the user has filled all information for lose weight
     *
     * @param currentUser FirebaseUser
     */
    private void checkUserInfo(FirebaseUser currentUser) {
        Context context = binding.getRoot().getContext();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(DB_USER_INFO_PATH).document(currentUser.getUid());
        docRef.get().addOnCompleteListener(task -> {
            //hide loading
            showLoginLoading(false);
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    //User has login and filled information, go to Main activity
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    context.startActivity(intent);
                    //finish current activity
                    requireActivity().finish();
                } else {
                    //User has login but not filled information, go to information page
                    gotoUserInfo();
                }
            } else {
                showMsg(getString(R.string.error_msg_fetch_user_info),
                        context.getColor(R.color.error_msg_bg));
            }
        });
    }

    /**
     * sign in
     */
    private void signIn() {
        PanelSignInBinding signInPanel = binding.signInPanel;
        String username = signInPanel.editUsernameSignIn.getText().toString();
        String password = signInPanel.editPasswordSignIn.getText().toString();
        String email = signInPanel.editEmail.getText().toString();
        Context context = binding.getRoot().getContext();
        if (Objects.equals(username, "")) {
            //check username
            showMsg(context.getString(R.string.msg_empty_username_error),
                    binding.getRoot().getContext().getColor(R.color.error_msg_bg));
            return;
        }
        if (Objects.equals(password, "")) {
            //check password
            showMsg(context.getString(R.string.msg_empty_password_error),
                    binding.getRoot().getContext().getColor(R.color.error_msg_bg));
            return;
        }
        if (password.length() < MIN_PASS_LEN) {
            showMsg(context.getString(R.string.msg_invalid_password_error),
                    binding.getRoot().getContext().getColor(R.color.error_msg_bg));
            return;
        }
        if (Objects.equals(email, "")) {
            //check email
            showMsg(context.getString(R.string.msg_empty_email_error),
                    binding.getRoot().getContext().getColor(R.color.error_msg_bg));
            return;
        }
        if (!isValidEmail(email)) {
            //check email
            showMsg(context.getString(R.string.msg_invalid_email_error),
                    binding.getRoot().getContext().getColor(R.color.error_msg_bg));
        }
        showSignUpLoading(true);
        Activity activity = getActivity();
        assert activity != null;
        //firebase auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        UserProfileChangeRequest userProfileChangeRequest =
                                new UserProfileChangeRequest.Builder()
                                        .setDisplayName(username).build();
                        assert user != null;
                        //save user name
                        user.updateProfile(userProfileChangeRequest).addOnCompleteListener(task1 -> {
                            //hide loading and show sign up button
                            showSignUpLoading(false);
                            //navigate to user information pages
                            gotoUserInfo();
                        });
                        showMsg(getString(R.string.msg_sign_up_successfully),
                                binding.getRoot().getContext().getColor(R.color.primary_green));
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "signInWithEmail:failure", task.getException());
                        showMsg(task.getException().getMessage(),
                                binding.getRoot().getContext().getColor(R.color.error_msg_bg));
                        showSignUpLoading(false);
                    }

                });
    }

    /**
     * show loading for signing up
     *
     * @param show true means showing
     */
    private void showSignUpLoading(boolean show) {
        if (show) {
            //show loading and hide sign up button
            binding.signInPanel.progressSignIn.setVisibility(View.VISIBLE);
            binding.signInPanel.buttonSignIn.setVisibility(View.INVISIBLE);
        } else {
            //hide loading and show sign up button
            binding.signInPanel.progressSignIn.setVisibility(View.INVISIBLE);
            binding.signInPanel.buttonSignIn.setVisibility(View.VISIBLE);
        }
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

    /**
     * check if the email is valid
     *
     * @param email email address
     * @return true is valid
     */
    private boolean isValidEmail(String email) {
        boolean isValid = false;
        try {
            String check = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            isValid = matcher.matches();
        } catch (Exception ignored) {
        }
        return isValid;
    }
}