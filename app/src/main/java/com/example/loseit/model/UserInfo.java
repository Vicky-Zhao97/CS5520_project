package com.example.loseit.model;

import static com.example.loseit.ui.user_info.GenderPanelViewSetting.MALE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * data class represent user information for lose weight
 */
public class UserInfo implements Serializable {
    private String userName;
    private String email;
    //male or female
    private String gender;
    //birth
    private Date birth;
    //height in unit cm
    private double height;
    //goal weight in unit kg
    private double goalWeight;
    //start date of losing weight
    private Date startDate;
    //end date of losing weight
    private Date endDate;
    //weekly goal weight loss
    private double goalWeightLoss;

    //daily weight in unit kg
    private ArrayList<DailyWeight> dailyWeights;

    //list of diet id
    private ArrayList<Diet> dietList;

    public UserInfo() {
        birth = new Date();
        startDate = new Date();
        endDate = new Date();
        dailyWeights = new ArrayList<>();
        dietList = new ArrayList<>();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getCurrentWeight() {
        return dailyWeights.get(dailyWeights.size() - 1).getWeight();
    }

    public double getGoalWeight() {
        return goalWeight;
    }

    public void setGoalWeight(double goalWeight) {
        this.goalWeight = goalWeight;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public double getGoalWeightLoss() {
        return goalWeightLoss;
    }

    public void setGoalWeightLoss(double goalWeightLoss) {
        this.goalWeightLoss = goalWeightLoss;
    }


    public ArrayList<DailyWeight> getDailyWeights() {
        return dailyWeights;
    }

    public void setDailyWeights(ArrayList<DailyWeight> dailyWeights) {
        this.dailyWeights = dailyWeights;
    }

    public double getStartWeight() {
        return dailyWeights.get(0).getWeight();
    }

    public ArrayList<Diet> getDietList() {
        return dietList;
    }

    public void setDietList(ArrayList<Diet> dietList) {
        this.dietList = dietList;
    }

    /**
     * calculate age by birth date
     *
     * @param birthDate Date
     * @return age int
     */
    public static int getAge(Date birthDate) {
        Calendar birthCal = Calendar.getInstance();
        birthCal.setTime(birthDate);

        Calendar nowCal = Calendar.getInstance();
        return nowCal.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);
    }

    /**
     * get total days to lose current weight to goal weight
     *
     * @return int
     */
    public int getTotalDays() {
        double loseWeight = getStartWeight() - goalWeight;
        //Calculate the total number of days required to lose weight
        return (int) Math.ceil((loseWeight / (goalWeightLoss / 7.0)));
    }

    /**
     * calculate the max kcal the user should eat today
     *
     * @return int
     */
    public int getLeftKcal() {
        double K = 0;
        if (Objects.equals(gender, MALE)) {
            K = 5;
        } else {
            K = -161;
        }
        double BMR = 10 * getCurrentWeight() + 6.25 * getHeight() - 5 * getAge(birth) + K;
        return (int) Math.ceil(BMR - goalWeightLoss / 7. * 2.2046 * 1800);
    }

    /**
     * Calculate weight loss duration days
     *
     * @return int
     */
    public int getPassedDay() {
        //show date
        Calendar now = Calendar.getInstance();
        Date nowDate = now.getTime();
        Date start = (Date) startDate.clone();
        int i = 0;
        Date nextDay = start;
        while (nextDay.before(nowDate)) {
            Calendar cld = Calendar.getInstance();
            cld.setTime(start);
            cld.add(Calendar.DATE, 1);
            start = cld.getTime();
            nextDay = start;
            i++;
        }
        return i;
    }
}
