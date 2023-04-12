package com.example.loseit.model;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * data class represent user information for lose weight
 */
public class RecipeItem implements Serializable {
    private String id;
    private String title;
    private String description;
    private ArrayList<DietItem> ingredients;
    private double totalKcal;
    private String imageUrl;
    @PropertyName("creationDate")   // The name of this field in the database is creationDate
    private Date createdAt;

    public RecipeItem() {
        ingredients = new ArrayList<>();
        createdAt = new Date();
    }

    public RecipeItem(String title, String description, ArrayList<DietItem> ingredients,
                      double totalKcal, String url) {
        this.title = title;
        this.description = description;
        this.ingredients = ingredients;
        this.totalKcal = totalKcal;
        this.imageUrl = url;
        this.createdAt = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getTotalKcal() {
        return totalKcal;
    }

    public void setTotalKcal(double kcal) {
        this.totalKcal = kcal;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String text) {
        this.description = text;
    }

    public ArrayList<DietItem> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<DietItem> ingredients) {
        this.ingredients = ingredients;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String url) {
        this.imageUrl = url;
    }

    public Date getCreationDate() { return createdAt; }
}
