package com.dmitriy.sinyak.delivarymeal.app.activity.restaurant.thread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.dmitriy.sinyak.delivarymeal.app.R;
import com.dmitriy.sinyak.delivarymeal.app.activity.main.MainActivity;
import com.dmitriy.sinyak.delivarymeal.app.activity.main.service.Restaurant;
import com.dmitriy.sinyak.delivarymeal.app.activity.main.service.RestaurantList;
import com.dmitriy.sinyak.delivarymeal.app.activity.main.thread.Count;
import com.dmitriy.sinyak.delivarymeal.app.activity.main.title.fragments.LoadPageFragment;
import com.dmitriy.sinyak.delivarymeal.app.activity.restaurant.service.Garbage;
import com.dmitriy.sinyak.delivarymeal.app.activity.restaurant.MealBody;
import com.dmitriy.sinyak.delivarymeal.app.activity.restaurant.RestaurantActivity;
import com.dmitriy.sinyak.delivarymeal.app.activity.restaurant.service.Meal;
import com.dmitriy.sinyak.delivarymeal.app.activity.restaurant.service.MealList;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by 1 on 11.11.2015.
 */
public class ChangeLanguageAsyncTask extends AsyncTask<String, Void, String> {

    private FragmentTransaction ft;
    private Count count;
    private AppCompatActivity activity;
    private LoadPageFragment loadPageFragment;
    private MealBody mealBody;
    private Restaurant restaurant;

    private boolean isCancled;

    private Connection connection;
    private Connection.Response response;
    private List<Meal> meals_copy;
    private Garbage garbage;


    public ChangeLanguageAsyncTask(AppCompatActivity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        connection = Restaurant.getConnection();

        List<Meal> meals = MealList.getMeals();
        meals_copy = new ArrayList<>(meals);

        if (meals != null && meals.size() > 0) {
            ft =  activity.getSupportFragmentManager().beginTransaction();
            for (Meal meal : MealList.getMeals()) {
                ft.remove(meal.getFragment());
            }
            ft.commit();


            meals.clear();
        }

        ((RestaurantActivity) activity).removeFragment();


        count = new Count(5);
        loadPageFragment = new LoadPageFragment(count);
        ft = activity.getSupportFragmentManager().beginTransaction();
        ft.add(R.id.restaurantPercentContainer, loadPageFragment);
        ft.commit();
    }

    @Override
    protected String doInBackground(String... params) {


        garbage = Garbage.getInstance();
        Document doc = null;
        Elements elements = null;

        synchronized (count){
            while (!count.isStateLoadFragment()){
                try {
                    count.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        count.complete();
        while (true) {

            try {
                MealList.setMealListCompleteFlag(false);
                connection.url(params[0]);
                response = connection.execute();
                connection.cookies(response.cookies());

                doc = response.parse();
                count.complete();
                elements = doc.getElementsByClass("food-item");

                if (elements.size() == 0) {
                    count.complete();
                    count.complete();
                    count.complete();


                    synchronized (count) {
                        while (!count.isStateData()) {
                            count.wait(100);
                        }
                    }
                    return null;
                }

                List<Restaurant> restaurants = RestaurantList.getRestaurants();
                int sizeRestaurants = restaurants.size();
                int iter = 0;

                for (Element element : elements) {
                    if (sizeRestaurants <= 0)
                        break;

                    Restaurant restaurant = restaurants.get(iter);

                    restaurant.setId(Integer.parseInt(element.attr("data-id")));
                    restaurant.setCostMeal(element.getElementsByClass("and-cost-mil").get(0).html());
                    restaurant.setCostDeliver(element.getElementsByClass("and-cost-deliver").get(0).html());
                    restaurant.setTimeDeliver(element.getElementsByClass("and-time-deliver").get(0).html());

                    restaurant.setCostMealStatic(activity.getResources().getString(R.string.min_cost_order));
                    restaurant.setCostDeliverStatic(activity.getResources().getString(R.string.cost_deliver));
                    restaurant.setTimeDeliverStatic(activity.getResources().getString(R.string.time_deliver));

                    restaurant.setImgSRC(element.getElementsByTag("img").get(0).attr("src"));
                    restaurant.setName(element.getElementsByClass("and-name").html());
                    restaurant.setProfile(element.getElementsByClass("and-profile").html());
                    restaurant.setStars(element.getElementsByClass("star").get(0).getElementsByTag("span").attr("style"));
                    restaurant.setMenuLink(element.getElementsByClass("food-img").get(0).getElementsByTag("a").attr("href"));

                    try{
                        URL imgURL = new URL(restaurant.getImgSRC());
                        Bitmap image = BitmapFactory.decodeStream(imgURL.openConnection().getInputStream());
                        float k = image.getWidth()/image.getHeight();
                        int width = 500;
                        int height = (int) (width / k);
                        restaurant.setImgBitmap(Bitmap.createScaledBitmap(image, width, height, true));
                    }
                    catch (IOException e){
//                        restaurant.setImgBitmap(((BitmapDrawable) ((MainActivity) activity).getResources().getDrawable(R.drawable.no_image)).getBitmap());
                    }


                    iter++;
                    sizeRestaurants--;
                }

                restaurant = RestaurantList.getRestaurant();

                if (restaurant == null){
                    count.complete();
                    count.complete();
                    count.complete();

                    synchronized (count) {
                        while (!count.isStateData()) {
                            count.wait(100);
                        }
                    }
                    return null;
                }


                count.complete();

                connection.url(restaurant.getMenuLink());

                response = connection.execute();
                connection.cookies(response.cookies());

                doc = response.parse();
                count.complete();
                elements = doc.getElementsByClass("item-food");

                if ((elements.size() == 0)) {
                    count.complete();
                    synchronized (count) {
                        while (!count.isStateData()) {
                            count.wait(100);
                        }
                    }
                    return null;
                }

                for (Element element1 : elements) {

                    Meal meal = new Meal();

                    meal.setId(element1.getElementsByClass("add_to_cart_button").attr("data-product_id"));
                    meal.setName(element1.getElementsByClass("and-name").get(0).html());
                    meal.setComposition(element1.getElementsByClass("and-composition").get(0).html());
                    meal.setWeight(element1.getElementsByClass("pull-right").get(0).html());
                    meal.setCost(element1.getElementsByClass("as").get(0).html());
                    meal.setImgURL(element1.getElementsByClass("item-img").get(0).getElementsByTag("img").attr("src"));

                    try{
                        URL imgURL = new URL(meal.getImgURL());
                        Bitmap image = BitmapFactory.decodeStream(imgURL.openConnection().getInputStream());
                        float k = image.getWidth()/image.getHeight();
                        int width = 350;
                        int height = (int) (width / k);
                        meal.setImg(Bitmap.createScaledBitmap(image, width, height, true));
                    }
                    catch (IOException e){
                        meal.setImg(((BitmapDrawable) activity.getResources().getDrawable(R.drawable.no_image)).getBitmap());
                    }

                    MealList.addMeal(meal);
                }

                for (String id : garbage.getListID()) {
                    for (Meal meal : meals_copy) {
                        if (meal.getId().equals(id)) {
                            MealList.getMeal(id).setCountMeal(meal.getCountMeal());
                        }
                    }
                }

                count.complete();
                synchronized (count) {
                    while (!count.isStateData()) {
                        count.wait(100);
                    }
                }
                return null;
            } catch (MalformedURLException e) {
                return null;
            } catch (IOException e) {
                try {
                    TimeUnit.MILLISECONDS.sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                    return null;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                MealList.setMealListCompleteFlag(true);
            }
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);


        ft = activity.getSupportFragmentManager().beginTransaction();
        ft.remove(loadPageFragment);
        ft.commit();

        isCancled = true;
        cancel(true);

        if (restaurant == null)
            return;

        ((RestaurantActivity) activity).initFragment(restaurant);

        mealBody = new MealBody(activity);
        mealBody.init();
    }

    public boolean isCancled() {
        return isCancled;
    }

    public void setIsCancled(boolean isCancled) {
        this.isCancled = isCancled;
    }

    public LoadPageFragment getLoadPageFragment() {
        return loadPageFragment;
    }

    public void setLoadPageFragment(LoadPageFragment loadPageFragment) {
        this.loadPageFragment = loadPageFragment;
    }
}
