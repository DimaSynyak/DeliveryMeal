package com.dmitriy.sinyak.delivarymeal.app.activity.payment;

import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.dmitriy.sinyak.delivarymeal.app.R;
import com.dmitriy.sinyak.delivarymeal.app.activity.restaurant.service.Meal;
import com.dmitriy.sinyak.delivarymeal.app.activity.restaurant.service.MealList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by 1 on 17.11.2015.
 */
public class Garbage {
    private static Garbage garbage;
    private AppCompatActivity activity;
    private int total;
    private TextView garbageNum;
    private Set<String> listID;

    public Garbage() {
        listID = new HashSet<>();
    }

    public static Garbage getInstance(){
        if (garbage == null){
            garbage = new Garbage();
        }
        return garbage;
    }

    public void update(){
        garbageNum = (TextView) activity.findViewById(R.id.garbageNum);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                garbageNum.setText(String.valueOf(total));
            }
        });
    }

    public AppCompatActivity getActivity() {
        return activity;
    }

    public void setActivity(AppCompatActivity _activity) {
        activity = _activity;
    }

    public int getTotal() {
        return total;
    }

    public void add(Meal meal) {
        listID.add(meal.getId());
        total++;
    }

    public void remove(Meal meal){
        if (total > 0) {
            total--;
        }

        if (meal.getCountMeal() == 0){
            listID.remove(meal.getId());
        }
    }

    public void removeAll(){
        total = 0;
        listID.clear();
    }

    public static void clear(){
        garbage = null;
    }

    public Set<String> getListID() {
        return listID;
    }

    public void setListID(Set<String> listID) {
        this.listID = listID;
    }
}
