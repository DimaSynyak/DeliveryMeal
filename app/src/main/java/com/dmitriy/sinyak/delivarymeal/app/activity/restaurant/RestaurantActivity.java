package com.dmitriy.sinyak.delivarymeal.app.activity.restaurant;

import android.app.ActionBar;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dmitriy.sinyak.delivarymeal.app.R;
import com.dmitriy.sinyak.delivarymeal.app.activity.IActivity;
import com.dmitriy.sinyak.delivarymeal.app.activity.main.service.Restaurant;
import com.dmitriy.sinyak.delivarymeal.app.activity.main.service.RestaurantList;
import com.dmitriy.sinyak.delivarymeal.app.activity.main.title.Language;
import com.dmitriy.sinyak.delivarymeal.app.activity.main.title.Languages;
import com.dmitriy.sinyak.delivarymeal.app.activity.restaurant.service.DelivaryData;
import com.dmitriy.sinyak.delivarymeal.app.activity.restaurant.head.RestaurantHeadFragment;
import com.dmitriy.sinyak.delivarymeal.app.activity.restaurant.head.RestaurantMiniHeadFragment;
import com.dmitriy.sinyak.delivarymeal.app.activity.restaurant.head.RestaurantMiniMenuFragment;
import com.dmitriy.sinyak.delivarymeal.app.activity.restaurant.menu.fragments.MenuFragment;
import com.dmitriy.sinyak.delivarymeal.app.activity.restaurant.menu.SMCRestaurantActivity;
import com.dmitriy.sinyak.delivarymeal.app.activity.restaurant.service.Garbage;
import com.dmitriy.sinyak.delivarymeal.app.activity.restaurant.service.MealList;
import com.dmitriy.sinyak.delivarymeal.app.activity.restaurant.service.RegistrationData;
import com.dmitriy.sinyak.delivarymeal.app.activity.restaurant.thread.ChangeLanguageAsyncTask;
import com.dmitriy.sinyak.delivarymeal.app.activity.restaurant.thread.RestaurantAsyncTask;
import com.dmitriy.sinyak.delivarymeal.app.activity.tools.Tools;
import com.jeremyfeinstein.slidingmenu.lib.CustomViewAbove;

import java.util.Locale;

/**
 * Created by 1 on 02.11.2015.
 */
public class RestaurantActivity extends AppCompatActivity implements View.OnClickListener, IActivity {
    private SMCRestaurantActivity slidingMenuConfig;
    private Language language;
    private CustomViewAbove customViewAbove;
    private RestaurantHeadFragment restaurantHeadFragment;
    private RestaurantMiniHeadFragment restaurantMiniHeadFragment;
    private RestaurantMiniMenuFragment restaurantMiniMenuFragment;
    private ScrollView scrollView;
    private FrameLayout restaurantHeadContainer;
    private int languageContainerId;
    private static int MIN_SCROLLY = -100;
    private static int MAX_SCROLLY = 300;
    private RestaurantActivity restaurantActivity;
    private Restaurant restaurant;
    private RestaurantAsyncTask restaurantAsyncTask;
    private Languages oldLanguage;
    private RegistrationData registrationData;

    private int positionRestaurant;
    private ChangeLanguageAsyncTask changeLocale;
    private DisplayMetrics metrics;
    private ImageView garbageButton;
    private TextView garbageNum;
    private Garbage garbage;
    private boolean garbageFlag;

    private int paymentLanguageContainer;
    public static final int TWENTY_PERCENT = 20;
    private DelivaryData delivaryData;


    private boolean firstFlag = true;
    private boolean onResumeFlag;

    private MenuFragment menuFragment;


    private FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        setContentView(R.layout.activity_restaurant);
        getSupportActionBar().setCustomView(R.layout.title);

        Typeface geometric = Typeface.createFromAsset(getAssets(), "fonts/geometric/geometric_706_black.ttf");
        Typeface arimo = Typeface.createFromAsset(getAssets(), "fonts/arimo/Arimo_Regular.ttf");

         /*INIT LANGUAGE*/
        languageContainerId = R.id.restaurantLanguageContainer;
        language = new Language(this);
        language.init();
        /*END INIT LANGUAGE*/

        restaurantActivity = this;
        garbage = Garbage.getInstance();
        garbage.setActivity(this);

        restaurantHeadContainer = (FrameLayout) findViewById(R.id.restaurantHeadContainer);

        garbageButton = (ImageView) findViewById(R.id.garbageButton);
        garbageNum = (TextView) findViewById(R.id.garbageNum);

        scrollView = (ScrollView) findViewById(R.id.scrollView3);
        restaurant = RestaurantList.getRestaurants().get((Integer) getIntent().getSerializableExtra("restaurant"));
        positionRestaurant = RestaurantList.getRestaurants().indexOf(restaurant);

        restaurantAsyncTask = null;
        restaurantAsyncTask = new RestaurantAsyncTask(this);
        restaurantAsyncTask.execute(restaurant.getMenuLink());


        initFragment(restaurant);
        scrollInit();

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        garbageNum = (TextView) findViewById(R.id.garbageNum);
        garbageNum.setTypeface(geometric);


        delivaryData = DelivaryData.getInstance();
        restaurant = RestaurantList.getRestaurant();


    }

    @Override
    public void onClick(View v) {
        if (slidingMenuConfig != null)
            slidingMenuConfig.onClickDp(v.getId());
        switch (v.getId()){
            case R.id.menuClick:{
                if (customViewAbove.getCurrentItem() == 1){
                    customViewAbove.setCurrentItem(0);
                }
                else {
                    customViewAbove.setCurrentItem(1);
                }
                break;
            }
            case R.id.imageView3:{
                if (changeLocale != null){
                   if (!changeLocale.isCancelled())
                       return;
                }

                if (restaurantAsyncTask != null){
                    restaurantAsyncTask.getLoadPageFragment().getThread().interrupt();
                    restaurantAsyncTask.cancel(true);
                    restaurantAsyncTask = null;
                }

                finish();
                break;
            }
            case R.id.fullRestaurantImg:{
                ft = getSupportFragmentManager().beginTransaction();
                ft.remove(restaurantMiniHeadFragment);
                ft.commit();
                ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.abc_slide_in_top, R.anim.abc_slide_out_bottom);
                ft.add(R.id.restaurantHeadContainer, restaurantHeadFragment);
                ft.commit();
                firstFlag = true;
                break;
            }
            case R.id.garbageButton:{

                if (garbage.getTotal() == 0)
                    return;


                if (menuFragment == null){
                    menuFragment = (MenuFragment) getSupportFragmentManager().findFragmentById(R.id.menu_fragment_id);
                }

                garbageFlag = true;
                customViewAbove.setCurrentItem(0);

                menuFragment.setGarbageFragment();

                break;
            }
        }
    }

    public void initFragment(Restaurant restaurant){
        restaurantHeadFragment = new RestaurantHeadFragment(restaurant);
        restaurantMiniHeadFragment = new RestaurantMiniHeadFragment(restaurant);
        restaurantMiniMenuFragment = new RestaurantMiniMenuFragment(restaurant);
        restaurantMiniHeadFragment.setRestaurantHeadFragment(restaurantHeadFragment);

        ft = getSupportFragmentManager().beginTransaction();
        if (firstFlag) {
            ft.add(R.id.restaurantHeadContainer, restaurantHeadFragment);
        }
        else {
            ft.add(R.id.restaurantHeadContainer, restaurantMiniHeadFragment);
        }

        ft.commit();
    }

    public void removeFragment(){
        restaurantHeadFragment.setRestaurant(restaurant);
        restaurantMiniHeadFragment.setRestaurant(restaurant);
        restaurantMiniMenuFragment.setRestaurant(restaurant);

        ft = getSupportFragmentManager().beginTransaction();

        if (restaurantHeadFragment.isAdded()){
            ft.remove(restaurantHeadFragment);
        }
        else if (restaurantMiniHeadFragment.isAdded()){
            ft.remove(restaurantMiniHeadFragment);
        }

        ft.commit();
    }

    public void scrollInit() {
        scrollView = (ScrollView) findViewById(R.id.scrollView3);
        scrollView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_SCROLL:
                    case MotionEvent.ACTION_MOVE:
                        if (scrollView.getScrollY() < MIN_SCROLLY && !firstFlag) {
                            firstFlag = true;
                            ft = getSupportFragmentManager().beginTransaction();
                            ft.remove(restaurantMiniHeadFragment);
                            ft.commit();
                            ft = getSupportFragmentManager().beginTransaction();
                            ft.setCustomAnimations(R.anim.abc_slide_in_top, R.anim.abc_slide_out_bottom);
                            ft.add(R.id.restaurantHeadContainer, restaurantHeadFragment);
                            ft.commit();

                            ((MarginLayoutParams) scrollView.getLayoutParams()).setMargins(0, (int) (180 * metrics.density), 0, 0);

                        } else if (scrollView.getScrollY() > MAX_SCROLLY && firstFlag) {
                            firstFlag = false;
                            ft = getSupportFragmentManager().beginTransaction();
                            ft.remove(restaurantHeadFragment);
                            ft.commit();

                            ft = getSupportFragmentManager().beginTransaction();
                            ft.setCustomAnimations(R.anim.abc_slide_in_top, R.anim.abc_slide_out_bottom);
                            ft.add(R.id.restaurantHeadContainer, restaurantMiniHeadFragment);
                            ft.commit();
                            ((MarginLayoutParams) scrollView.getLayoutParams()).setMargins(0, (int) (50 * metrics.density), 0, 0);
                        }
                    case MotionEvent.ACTION_DOWN:

                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:

                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void setCustomViewAbove(CustomViewAbove customViewAbove) {
        this.customViewAbove = customViewAbove;
    }

    @Override
    public CustomViewAbove getCustomViewAbove() {
        return this.customViewAbove;
    }

    @Override
    public int getLanguageContainerId() {
        return languageContainerId;
    }

    @Override
    public void setLanguageContainerId(int languageContainerId) {
        this.languageContainerId = languageContainerId;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MealList.getMeals().clear();
        garbage.clear();
    }

    @Override
    public void changeLanguage(Languages languages) {
        if (language.getLanguages() == languages) {
            return;
        }

        if (changeLocale != null){
            if (!changeLocale.isCancelled())
                return;
        }

        if (restaurantAsyncTask != null){
            if (!restaurantAsyncTask.isCancelled()){
                return;
            }
        }

        if (menuFragment != null && menuFragment.isOrderDataClickFlag()){
            menuFragment.getOrderClick().callOnClick();
        }
        if (menuFragment != null && menuFragment.isFormDataClickFlag()){
            menuFragment.getFormDataClick().callOnClick();
        }
        if (menuFragment != null && menuFragment.isPersonalCabinetClickFlag()){
            menuFragment.getPersonalCabinet().callOnClick();
        }



        Fragment iconFragment = (Fragment) getSupportFragmentManager().findFragmentById(R.id.languagesFrame);
        changeLocale = new ChangeLanguageAsyncTask(this);
        Locale myLocale = null;
        Tools tools = Tools.getInstance();

        oldLanguage = language.getLanguages();
        language.setLanguages(languages);

        switch (languages){
            case RU:{
                ((ImageView) iconFragment.getView().findViewById(R.id.languagesClick)).setImageResource(R.drawable.language_ru);
                myLocale = new Locale("ru");
                changeLocale.execute(language.getURL());
                break;
            }
            case EE:{
                ((ImageView) iconFragment.getView().findViewById(R.id.languagesClick)).setImageResource(R.drawable.language_ee);
                myLocale = new Locale("et");
                changeLocale.execute(language.getURL());
                break;
            }
            case EN:{
                ((ImageView) iconFragment.getView().findViewById(R.id.languagesClick)).setImageResource(R.drawable.language_en);
                myLocale = new Locale("en");
                changeLocale.execute(language.getURL());
                break;
            }
            default:
                return;
        }

        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());


        updateUI();
    }

    private void updateUI(){

        TextView textView26 = (TextView) findViewById(R.id.textView26);
        textView26.setText(R.string.back);

        TextView pay = (TextView) findViewById(R.id.textView25);
        pay.setText(R.string.pay);

        TextView personal_cabinet = (TextView) findViewById(R.id.personal_cabinet_text);
        personal_cabinet.setText(R.string.personal_cabinet);

        TextView yourData = (TextView) findViewById(R.id.deliverText);
        yourData.setText(R.string.your_data);

        TextView yourOrder = (TextView) findViewById(R.id.mealText);
        yourOrder.setText(R.string.your_order);

        TextView total = (TextView) findViewById(R.id.total_text1);
        total.setText(R.string.total);

        TextView nameLabel = (TextView) findViewById(R.id.name_reg_form_label);
        nameLabel.setText(R.string.name);

        TextView emailLabel = (TextView) findViewById(R.id.email_reg_form_label);
        emailLabel.setText(R.string.your_email);

        TextView passwordLabel = (TextView) findViewById(R.id.password_reg_form_label);
        passwordLabel.setText(R.string.password);

        TextView confirmPasswordLabel = (TextView) findViewById(R.id.confirm_pass_reg_form_label);
        confirmPasswordLabel.setText(R.string.confirm_password);

        TextView phoneLabel = (TextView) findViewById(R.id.phone_reg_form_label);
        phoneLabel.setText(R.string.your_phone);

        TextView countryLabel = (TextView) findViewById(R.id.country_reg_form_label);
        countryLabel.setText(R.string.country);

        TextView cityLabel = (TextView) findViewById(R.id.city_reg_form_label);
        cityLabel.setText(R.string.city);

        TextView indexLabel = (TextView) findViewById(R.id.index_reg_form_label);
        indexLabel.setText(R.string.index);

        TextView streetLabel = (TextView) findViewById(R.id.street_reg_form_label);
        streetLabel.setText(R.string.address);

        TextView houseNumLabel = (TextView) findViewById(R.id.num_house_reg_form_label);
        houseNumLabel.setText(R.string.num_house);

        TextView officeNumLabel = (TextView) findViewById(R.id.num_flat_reg_form_label);
        officeNumLabel.setText(R.string.num_flat);

        registrationData = RegistrationData.getInstance();
        TextView okRegFormButton = (TextView) findViewById(R.id.ok_reg_form_btn);

        if (registrationData.isPersonalCabinetType()) {
            okRegFormButton.setText(R.string.register);
        }else{
            okRegFormButton.setText(R.string.enter);
        }

        TextView registrationLabel = (TextView) findViewById(R.id.registrationText);
        registrationLabel.setText(R.string.registration);

        TextView loginLabel = (TextView) findViewById(R.id.login);
        loginLabel.setText(R.string.login);

        /**
         * YOUR DATA in MenuFragment
         * */

        TextView withDelivery = (TextView) findViewById(R.id.withDelivery);
        withDelivery.setText(R.string.with_delivery);

        TextView withoutDelivery = (TextView) findViewById(R.id.withoutDelivery);
        withoutDelivery.setText(R.string.without_delivery);

        TextView name = (TextView) findViewById(R.id.name);
        name.setText(R.string.enter_name);

        TextView chooseCity = (TextView) findViewById(R.id.city);
        chooseCity.setText(R.string.choose_city);

        TextView streetText = (TextView) findViewById(R.id.streetText);
        streetText.setText(R.string.address);

        TextView numHouse = (TextView) findViewById(R.id.numHouse);
        numHouse.setText(R.string.num_house);

        TextView numFlat = (TextView) findViewById(R.id.numFlat);
        numFlat.setText(R.string.num_flat);

        TextView email = (TextView) findViewById(R.id.email);
        email.setText(R.string.your_email);

        TextView phone = (TextView) findViewById(R.id.phone);
        phone.setText(R.string.your_phone);
    }

    public SMCRestaurantActivity getSlidingMenuConfig() {
        return slidingMenuConfig;
    }

    public void setSlidingMenuConfig(SMCRestaurantActivity slidingMenuConfig) {
        this.slidingMenuConfig = slidingMenuConfig;
    }




    @Override
    protected void onResume() {
        super.onResume();

        if (!onResumeFlag){
            onResumeFlag = true;
            return;
        }
        garbageNum.setText(String.valueOf(garbage.getTotal()));
    }

    public boolean isGarbageFlag() {
        return garbageFlag;
    }

    public void setGarbageFlag(boolean garbageFlag) {
        this.garbageFlag = garbageFlag;
    }
}
