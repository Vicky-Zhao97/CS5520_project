package com.example.loseit.ui.user_info;

import android.view.View;

import java.util.HashMap;

/**
 * class for specify the view shown in the user information fragment dynamically
 */
public class UserInfoViewSetting {
    // specify which view to show in the center
    public final String contentViewName;

    public UserInfoViewSetting(String contentViewName) {
        this.contentViewName = contentViewName;
    }


    /**
     * restore state of this view when it is
     *
     * @param view layout of the view
     */
    public void bind(View view) {

    }
}
