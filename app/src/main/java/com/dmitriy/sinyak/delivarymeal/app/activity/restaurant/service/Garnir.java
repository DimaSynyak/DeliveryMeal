package com.dmitriy.sinyak.delivarymeal.app.activity.restaurant.service;

import android.widget.LinearLayout;
import android.widget.RadioButton;

/**
 * Created by 1 on 02.12.2015.
 */
public class Garnir {
    private String garnirId;
    private String garnirName;
    private String garnirValue;
    private LinearLayout horizontalLayout;
    private RadioButton radioButton;

    public String getGarnirId() {
        return garnirId;
    }

    public void setGarnirId(String garnirId) {
        this.garnirId = garnirId;
    }

    public String getGarnirName() {
        return garnirName;
    }

    public void setGarnirName(String garnirName) {
        this.garnirName = garnirName;
    }

    public String getGarnirValue() {
        return garnirValue;
    }

    public void setGarnirValue(String garnirValue) {
        this.garnirValue = garnirValue;
    }

    public LinearLayout getHorizontalLayout() {
        return horizontalLayout;
    }

    public void setHorizontalLayout(LinearLayout horizontalLayout) {
        this.horizontalLayout = horizontalLayout;
    }

    public RadioButton getRadioButton() {
        return radioButton;
    }

    public void setRadioButton(RadioButton radioButton) {
        this.radioButton = radioButton;
    }
}
