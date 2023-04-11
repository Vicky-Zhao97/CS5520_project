package com.example.loseit.model;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * data class represent user information for lose weight
 */
public class RecipeItem implements Serializable {
    private String title;
    private double totalKcal;
    //private String image;
    private ArrayList<DietItem> ingredients;
    private String description;
    @PropertyName("creationDate")   // The name of this field in the database is creationDate
    private Date createdAt;

    public RecipeItem() {
        ingredients = new ArrayList<>();
        createdAt = new Date();
    }

    public RecipeItem(String title, String description, ArrayList<DietItem> ingredients,
                      double totalKcal) {
        this.title = title;
        this.description = description;
        this.ingredients = ingredients;
        this.totalKcal = totalKcal;
        this.createdAt = new Date();
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

    public Date getCreationDate() { return createdAt; }

    public void setIngredients(ArrayList<DietItem> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public String toString() {
        String ans = String.format("<%s - %s kcal>\n", title, totalKcal);
        ans += description + "\n";
        for (DietItem ingredient : ingredients) {
            ans += String.format("%s(%s kcal)  ",ingredient.getName(), ingredient.getKcal());
        }
        return ans;
    }
}
