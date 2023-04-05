package com.example.loseit.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * data class for save daily weight
 */
public class DailyWeight implements Serializable {
    //date when the weight record
    private Date createAt;
    //weight in unit kg
    private double weight;

    public DailyWeight() {
        createAt = Calendar.getInstance().getTime();
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date date) {
        this.createAt = date;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * static method for checking if a daily weight is recorded at date
     *
     * @param weight DailyWeight
     * @param date   Date
     * @return true means this daily weight is recorded at date
     */
    public static boolean isWeightRecordAt(DailyWeight weight, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar dietCal = Calendar.getInstance();
        dietCal.setTime(weight.getCreateAt());
        boolean cond1 = calendar.get(Calendar.YEAR) == dietCal.get(Calendar.YEAR);
        boolean cond2 = calendar.get(Calendar.MONTH) == dietCal.get(Calendar.MONTH);
        boolean cond3 = calendar.get(Calendar.DAY_OF_MONTH) == dietCal.get(Calendar.DAY_OF_MONTH);
        return cond1 && cond2 && cond3;
    }
}
