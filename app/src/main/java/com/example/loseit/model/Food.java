package com.example.loseit.model;

import java.io.Serializable;

/**
 * Class representing calories per ounce of food
 */
public class Food implements Serializable {
    //key for filter this object in database
    public static final String FILTER_NAME = "name";
    private String name;
    private double caloriePerOunce;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCaloriePerOunce() {
        return caloriePerOunce;
    }

    public void setCaloriePerOunce(double caloriePerOunce) {
        this.caloriePerOunce = caloriePerOunce;
    }
}
