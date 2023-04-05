package com.example.loseit.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * data class for  diet
 */
public class Diet implements Serializable {
    // diet of breakfast
    private ArrayList<DietItem> breakFast;
    // diet of lunch
    private ArrayList<DietItem> lunch;
    //diet for dinner
    private ArrayList<DietItem> dinner;
    // create date
    private Date createAt;
    // user id
    private String userID;

    public Diet() {
        breakFast = new ArrayList<>();
        lunch = new ArrayList<>();
        dinner = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        createAt = calendar.getTime();
    }

    public ArrayList<DietItem> getBreakFast() {
        return (ArrayList<DietItem>) breakFast.clone();
    }

    public void setBreakFast(ArrayList<DietItem> breakFast) {
        this.breakFast = breakFast;
    }

    public ArrayList<DietItem> getLunch() {
        return (ArrayList<DietItem>) lunch.clone();
    }

    public void setLunch(ArrayList<DietItem> lunch) {
        this.lunch = lunch;
    }

    public ArrayList<DietItem> getDinner() {
        return (ArrayList<DietItem>) dinner.clone();
    }

    public void setDinner(ArrayList<DietItem> dinner) {
        this.dinner = dinner;
    }

    public Date getCreateAt() {
        return (Date) createAt.clone();
    }

    public void setCreateAt(Date mod_date) {
        this.createAt = mod_date;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Diet diet = (Diet) o;
        boolean cond1 = Objects.equals(diet.getUserID(), userID);
        boolean cond2 = isDietAtDate(diet, getCreateAt());
        return cond1 && cond2;
    }

    /**
     * check if the diet is created at date
     *
     * @param diet Diet
     * @param date Date
     * @return bool
     */
    public static boolean isDietAtDate(Diet diet, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar dietCal = Calendar.getInstance();
        dietCal.setTime(diet.getCreateAt());
        boolean cond1 = calendar.get(Calendar.YEAR) == dietCal.get(Calendar.YEAR);
        boolean cond2 = calendar.get(Calendar.MONTH) == dietCal.get(Calendar.MONTH);
        boolean cond3 = calendar.get(Calendar.DAY_OF_MONTH) == dietCal.get(Calendar.DAY_OF_MONTH);
        return cond1 && cond2 && cond3;
    }

    @Override
    public int hashCode() {
        return Objects.hash(createAt);
    }

}
