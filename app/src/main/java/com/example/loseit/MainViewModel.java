package com.example.loseit;

import static com.example.loseit.StartActivity.DB_USER_INFO_PATH;
import static com.example.loseit.ui.user_info.GenderPanelViewSetting.MALE;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.loseit.model.DailyWeight;
import com.example.loseit.model.Diet;
import com.example.loseit.model.Food;
import com.example.loseit.model.UserInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.jar.Attributes;


public class MainViewModel extends ViewModel {
    public static final String DB_FOOD_CALORIES = "food_calories";
    //data key used in updateEatKcal method
    public static final String DIET_BREAKFAST = "breakfast";
    public static final String DIET_LUNCH = "lunch";
    public static final String DIET_DINNER = "dinner";
    //map for saving total eat kcal of breakfast, lunch and dinner
    private final HashMap<String, Integer> eatKcalMap;

    //live data saving user information
    private final MutableLiveData<UserInfo> userInfoLiveData;

    //live data saving eat kcal of current showing diet
    private final MutableLiveData<Integer> eatKcalLiveData;

    //live data saving left kcal of current showing diet
    private final MutableLiveData<Integer> leftKcalLiveData;

    //live data saving a list of food calories
    private final MutableLiveData<ArrayList<Food>> FoodLiveData;
    private final MutableLiveData<String> KcalLiveData;

    //current showing diet in diet list of user info
    private int currentShowDietIndex = -1;

    public MainViewModel() {
        userInfoLiveData = new MutableLiveData<>();
        eatKcalLiveData = new MutableLiveData<>(0);
        leftKcalLiveData = new MutableLiveData<>(0);
        FoodLiveData = new MutableLiveData<>(new ArrayList<>());
        KcalLiveData = new MutableLiveData<>("");
        eatKcalMap = new HashMap<>();
        eatKcalMap.put(DIET_BREAKFAST, 0);
        eatKcalMap.put(DIET_LUNCH, 0);
        eatKcalMap.put(DIET_DINNER, 0);
    }

    /**
     * listener for watching if fetch user information has done
     */
    public interface OnFetchUserInfoDoneListener {
        void fetchIsDone(UserInfo userInfo, boolean successful);
    }

    /**
     * check if today diet is created
     *
     * @return true means created
     */
    public boolean isTodayDietCreated() {
        ArrayList<Diet> dietList = getUserInfo().getDietList();
        if (dietList.size() < 1) {
            return false;
        } else {
            //Loading the die list for the first time
            Diet diet = dietList.get(dietList.size() - 1);
            Calendar now = Calendar.getInstance();
            //check if the last diet is today diet
            return Diet.isDietAtDate(diet, now.getTime());
        }
    }

    /**
     * observe eat kcal change
     *
     * @return MutableLiveData<Integer>
     */
    public MutableLiveData<Integer> observeEatKcal() {
        return eatKcalLiveData;
    }

    /**
     * observe left kcal change
     *
     * @return MutableLiveData<Integer>
     */
    public MutableLiveData<Integer> observeLeftKcal() {
        return leftKcalLiveData;
    }

    /**
     * update eat kcal of current showing diet
     *
     * @param type String
     */
    public void updateEatKcal(int kcal, String type) {
        eatKcalMap.put(type, kcal);
        int totalEatKcal = 0;
        totalEatKcal += eatKcalMap.get(DIET_BREAKFAST);
        totalEatKcal += eatKcalMap.get(DIET_LUNCH);
        totalEatKcal += eatKcalMap.get(DIET_DINNER);
        eatKcalLiveData.postValue(totalEatKcal);

        //refresh left kcal
        UserInfo userInfo = getUserInfo();
        assert userInfo != null;
        double K;
        if (Objects.equals(userInfo.getGender(), MALE)) {
            K = 5;
        } else {
            K = -161;
        }
        double BMR = 10 * getCurrentWeight() + 6.25 * userInfo.getHeight()
                - 5 * UserInfo.getAge(userInfo.getBirth()) + K;
        double maxLeftKcal = BMR - userInfo.getGoalWeightLoss() / 7. * 2.2046 * 1800;
        leftKcalLiveData.postValue((int) (maxLeftKcal - totalEatKcal));
    }

    /**
     * get the diet current show, note the diet object is shallow copied
     *
     * @return Diet
     */
    public Diet getCurrentShowDiet() {
        ArrayList<Diet> dietList = getUserInfo().getDietList();
        if (currentShowDietIndex >= 0) {
            return dietList.get(currentShowDietIndex);
        }
        if (isTodayDietCreated()) {
            //Loading the die list for the first time, return today diet
            int index = dietList.size() - 1;
            if (currentShowDietIndex < 0) {
                currentShowDietIndex = index;
            }
            return dietList.get(index);
        } else {
            //Loading the die list for the first time and diet list is empty,
            // create diet for today
            Diet diet = new Diet();
            FirebaseUser user = getUserAuth();
            diet.setUserID(user.getUid());
            dietList.add(diet);
            if (currentShowDietIndex < 0) {
                currentShowDietIndex = 0;
            }
            return diet;
        }
    }

    /**
     * show last diet
     */
    public Diet lastDiet() {
        ArrayList<Diet> dietList = getUserInfo().getDietList();
        if (dietList.size() < 1) {
            return null;
        }
        currentShowDietIndex -= 1;
        if (currentShowDietIndex < 0) {
            currentShowDietIndex = 0;
        }
        return getCurrentShowDiet();
    }

    /**
     * show next diet
     */
    public Diet nextDiet() {
        ArrayList<Diet> dietList = getUserInfo().getDietList();
        if (dietList.size() < 1) {
            return null;
        }
        currentShowDietIndex += 1;
        if (currentShowDietIndex >= dietList.size()) {
            currentShowDietIndex = dietList.size() - 1;
        }
        return getCurrentShowDiet();
    }

    /**
     * get user authentication
     *
     * @return FirebaseUser
     */
    public FirebaseUser getUserAuth() {
        //user authentication
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        return mAuth.getCurrentUser();
    }

    /**
     * set user info but do not notify
     *
     * @param userInfo UserInfo
     */
    public void setUserInfo(UserInfo userInfo) {
        FirebaseUser currentUser = getUserAuth();
        if (currentUser == null) {
            return;
        }
        //update database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(DB_USER_INFO_PATH).document(currentUser.getUid())
                .set(userInfo)
                .addOnSuccessListener(documentReference -> {
                    //notify to refresh view
                    userInfoLiveData.setValue(userInfo);
                    Log.d("MainViewModel", "updateUserInfo: successfully! ");
                })
                .addOnFailureListener(e -> {
                    Log.e("MainViewModel", "updateUserInfo: failed! ");
                });
    }

    /**
     * get user info
     *
     * @return UserInfo
     */
    public UserInfo getUserInfo() {
        return userInfoLiveData.getValue();
    }

    /**
     * observe user information change
     *
     * @return MutableLiveData<UserInfo>
     */
    public MutableLiveData<UserInfo> observeUserInfo() {
        return userInfoLiveData;
    }

    /**
     * fetch user information
     *
     * @param fetchUserInfoDoneListener OnFetchUserInfoDoneListener
     */
    public void fetchUserInfo(OnFetchUserInfoDoneListener fetchUserInfoDoneListener) {
        if (fetchUserInfoDoneListener == null) {
            return;
        }
        //user information already fetched
        UserInfo currentUserInfo = userInfoLiveData.getValue();
        if (currentUserInfo != null) {
            fetchUserInfoDoneListener.fetchIsDone(currentUserInfo, true);
            return;
        }
        //user auth
        FirebaseUser currentUser = getUserAuth();
        //firestore api
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        assert currentUser != null;
        DocumentReference docRef = db.collection(DB_USER_INFO_PATH).document(currentUser.getUid());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    //fetch is done, set data to view model
                    UserInfo userInfo = task.getResult().toObject(UserInfo.class);
                    //set current user information
                    userInfoLiveData.postValue(userInfo);
                    assert userInfo != null;
                    fetchUserInfoDoneListener.fetchIsDone(userInfo, true);
                }
            } else {
                //fetch user info failed
                fetchUserInfoDoneListener.fetchIsDone(null, false);
            }
        }).addOnFailureListener(e -> {
            //fetch user info failed
            fetchUserInfoDoneListener.fetchIsDone(null, false);
        });
    }

    /**
     * get current weight of user
     *
     * @return DailyWeight
     */
    public double getCurrentWeight() {
        ArrayList<DailyWeight> dailyWeightList = getUserInfo().getDailyWeights();
        return dailyWeightList.get(dailyWeightList.size() - 1).getWeight();
    }

    /**
     * record daily weight
     *
     * @param dailyWeight DailyWeight
     */
    public void recordDailyWeight(DailyWeight dailyWeight) {
        boolean isCreated = false;
        for (DailyWeight weight : getUserInfo().getDailyWeights()) {
            if (DailyWeight.isWeightRecordAt(dailyWeight, weight.getCreateAt())) {
                //user has recorded today weight, update it
                weight.setWeight(dailyWeight.getWeight());
                isCreated = true;
            }
        }
        if (!isCreated) {
            //this weight has not been recorded, add it
            getUserInfo().getDailyWeights().add(dailyWeight);
        }
        //sync user info
        setUserInfo(getUserInfo());
    }

    /**
     * observe food calories change
     *
     * @return MutableLiveData<ArrayList < Food>>
     */
    public MutableLiveData<ArrayList<Food>> observeFoodLiveData() {
        return FoodLiveData;
    }
    public MutableLiveData<String> observeKcalLiveData() {
        return KcalLiveData;
    }

    /**
     * filter food from database by name
     *
     * @param foodName String
     */
    public void fetchFood(String foodName) {
        //firestore api
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection(DB_FOOD_CALORIES);
        //filter by name
        Query query = collectionReference.whereGreaterThanOrEqualTo(Food.FILTER_NAME, foodName)
                .whereLessThanOrEqualTo(Food.FILTER_NAME,
                        String.format("%s\uf8ff", foodName)).limit(5);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            ArrayList<Food> Food = new ArrayList<>();
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                if (documentSnapshot.exists()) {
                    Food fc = documentSnapshot.toObject(Food.class);
                    Food.add(fc);
                }
            }
            //notify data change
            FoodLiveData.postValue(Food);
        }).addOnFailureListener(e -> {
            //handle errors
        });

    }


    public void fetchKcal(String foodName) {
        RequestParams requestParams = new RequestParams("https://api.api-ninjas.com/v1/nutrition?query=" + foodName);
        requestParams.addHeader("X-Api-Key","C+0ABlX/CZ/s6r3EGfAxyw==2RR2KE5bgoihWl3k");
        requestParams.addHeader("Content-Type","application/json");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (TextUtils.isEmpty(result))
                    return;
                try {
                    JSONArray jsa = new JSONArray(result);
                    if (jsa.length()==0)
                        return;
                    JSONObject jsonObject = jsa.optJSONObject(0);
                    if (jsonObject==null)
                        return;
                    String calories = jsonObject.optString("calories");
                    KcalLiveData.postValue(calories);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * sign out
     */
    public void signOut() {
        FirebaseAuth.getInstance().signOut();
    }
}