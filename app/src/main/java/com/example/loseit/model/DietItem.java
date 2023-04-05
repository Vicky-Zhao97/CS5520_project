package com.example.loseit.model;

import java.io.Serializable;

/**
 * class represents a diet item in list
 */
public class DietItem implements Serializable {
    private String name;
    private double kcal;

    public DietItem() {

    }

    public DietItem(String name, double kcal) {
        this.name = name;
        this.kcal = kcal;
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
}
