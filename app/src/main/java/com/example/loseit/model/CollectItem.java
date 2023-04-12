package com.example.loseit.model;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class CollectItem implements Serializable {
    private String id;
    private String recipeId;
    private String userID;

    @PropertyName("creationDate")   // The name of this field in the database is creationDate
    private Date createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
