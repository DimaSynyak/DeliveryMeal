package com.dmitriy.sinyak.delivarymeal.app.activity.restaurant.head;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dmitriy.sinyak.delivarymeal.app.R;
import com.dmitriy.sinyak.delivarymeal.app.activity.main.service.Restaurant;

/**
 * Created by 1 on 03.11.2015.
 */
public class RestaurantHeadFragment extends Fragment {
    private Restaurant restaurant;

    public RestaurantHeadFragment(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.restaurant_head_fragment, container, false);

        TextView name = (TextView) view.findViewById(R.id.restaurantName);
        name.setText(restaurant.getName());

        TextView profile = (TextView) view.findViewById(R.id.restaurantProfile);
        profile.setText(restaurant.getProfile());

        TextView level = (TextView) view.findViewById(R.id.restaurantLevel);
        level.setText(restaurant.getStars().toString());

        RatingBar stars = (RatingBar) view.findViewById(R.id.ratingBar);
        stars.setRating(restaurant.getStars());
        stars.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        TextView costMeal = (TextView) view.findViewById(R.id.costMeal);
        costMeal.setText(restaurant.getCostMeal());

        TextView costDeliver = (TextView) view.findViewById(R.id.costDeliver);
        costDeliver.setText(restaurant.getCostDeliver());

        TextView timeDeliver = (TextView) view.findViewById(R.id.timeDeliver);
        timeDeliver.setText(restaurant.getTimeDeliver());

        ImageView img = (ImageView) view.findViewById(R.id.restaurantHeadAvatar);
        img.setImageBitmap(restaurant.getImgBitmap());

        TextView costMealText = (TextView) view.findViewById(R.id.costMealText);
        TextView costDeliverText = (TextView) view.findViewById(R.id.costDeliverText);
        TextView timeDeliverText = (TextView) view.findViewById(R.id.timeDeliverText);
        TextView menu = (TextView) view.findViewById(R.id.textView7);

        Typeface geometric = Typeface.createFromAsset(getActivity().getAssets(), "fonts/geometric/geometric_706_black.ttf");

        Typeface arimo = Typeface.createFromAsset(getActivity().getAssets(), "fonts/arimo/Arimo_Regular.ttf");

        name.setTypeface(geometric);
        level.setTypeface(geometric);
        costMeal.setTypeface(geometric);
        costDeliver.setTypeface(geometric);
        timeDeliver.setTypeface(geometric);
        menu.setTypeface(geometric);

        profile.setTypeface(arimo);
        costDeliverText.setTypeface(arimo);
        costMealText.setTypeface(arimo);
        timeDeliverText.setTypeface(arimo);
        return view;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}
