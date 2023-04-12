package com.example.loseit.model;

import java.io.Serializable;

/**
 * class represents a diet item in list
 */
public class DietItem implements Serializable {
    private String name;
    private double kcal;
    //amount in ounce
    private double amountInOunce;
    private double caloriesPerOunce;

    public DietItem() {

    }

    public DietItem(String name, double amountInOunce, double caloriesPerOunce) {
        this.name = name;
        this.kcal = amountInOunce * caloriesPerOunce;
    }

    public void setKcal(double kcal) {
        this.kcal = kcal;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getKcal() {
        return kcal;
    }

    public String getName() {
        return name;
    }

    public double getAmountInOunce() {
        return amountInOunce;
    }

    public void setAmountInOunce(double amountInOunce) {
        this.amountInOunce = amountInOunce;
    }

    public double getCaloriesPerOunce() {
        return caloriesPerOunce;
    }

    public void setCaloriesPerOunce(double caloriesPerOunce) {
        this.caloriesPerOunce = caloriesPerOunce;
    }
}