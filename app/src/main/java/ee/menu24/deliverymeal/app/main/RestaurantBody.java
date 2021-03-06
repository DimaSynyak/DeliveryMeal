package ee.menu24.deliverymeal.app.main;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import ee.menu24.deliverymeal.app.R;
import ee.menu24.deliverymeal.app.main.fragments.RestaurantFragment;
import ee.menu24.deliverymeal.app.main.service.Restaurant;
import ee.menu24.deliverymeal.app.main.service.RestaurantList;

/**
 * Created by 1 on 02.11.2015.
 */
public class RestaurantBody {

    private AppCompatActivity activity;
    private FragmentTransaction ft;
    public static RestaurantBody restaurantBody;
    private RestaurantList restaurantList;

    public RestaurantBody(AppCompatActivity activity) {
        this.activity = activity;
        restaurantBody = this;
        restaurantList = RestaurantList.getInstance();
    }

    public void init(){
        ft = activity.getSupportFragmentManager().beginTransaction();
        for (Restaurant restaurant: restaurantList.getRestaurants()) {
            RestaurantFragment restaurantFragment = new RestaurantFragment();
            restaurantFragment.init(activity, restaurant);
            ft.add(R.id.restaurants_list, restaurantFragment);
        }
        ft.commit();
    }

    public static RestaurantBody getInstance(AppCompatActivity activity){
        if (restaurantBody == null){
            restaurantBody = new RestaurantBody(activity);
        }

        return restaurantBody;
    }
}
