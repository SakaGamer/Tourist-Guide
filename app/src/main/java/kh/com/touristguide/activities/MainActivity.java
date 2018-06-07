package kh.com.touristguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import kh.com.touristguide.R;
import kh.com.touristguide.fragments.AllProvinceFragment;
import kh.com.touristguide.fragments.HomeFragment;
import kh.com.touristguide.fragments.NearMeFragment;
import kh.com.touristguide.fragments.RecommendFragment;
import kh.com.touristguide.fragments.SavePlaceFragment;
import kh.com.touristguide.helpers.ConstantValue;
import kh.com.touristguide.helpers.KeyboardHelper;
import kh.com.touristguide.helpers.LocaleHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private static int selectedNav = R.id.nav_home;
    private static String CURRENT_TAG = ConstantValue.HOME_FRAGMENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(R.string.app_name);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // when drawer open, hide keyboard
                KeyboardHelper.hide(getApplicationContext(), getCurrentFocus());
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // navigation view
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // navigation view header
        final View navHeader = navigationView.getHeaderView(0);
        final ImageView imageView = navHeader.findViewById(R.id.nav_header_img_user_profile);
        final TextView textUsername = navHeader.findViewById(R.id.nav_header_text_name);
        final Button btnSignOut = navHeader.findViewById(R.id.nav_header_text_sign_out);
        imageView.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);

        // listener when user authentication changed
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    textUsername.setText(user.getEmail());
                    textUsername.setVisibility(View.VISIBLE);
                    btnSignOut.setVisibility(View.VISIBLE);
                } else {
                    Log.d("app", "not logged in :( ");
                    textUsername.setVisibility(View.GONE);
                    btnSignOut.setVisibility(View.GONE);
                }
            }
        };

        // get last state if saved
        if (savedInstanceState == null) {
            // if no saved state, load fragment-popular as default
            selectedNav = R.id.nav_home;
            CURRENT_TAG = ConstantValue.HOME_FRAGMENT;
            loadFragment(selectedNav);
        } else {
            // if there is saved state, load last saved state
            selectedNav = savedInstanceState.getInt("selected_nav");
            loadFragment(selectedNav);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save selected fragment state
        outState.putInt("selected_nav", selectedNav);
        outState.putString(ConstantValue.SELECTED_LANGUAGE,
                LocaleHelper.getPersistedLanguage(getApplicationContext()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        // add user authentication listener
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        String theme =
    }

    @Override
    protected void onStop() {
        super.onStop();
        // remove user authentication listener
        auth.removeAuthStateListener(authStateListener);
    }

    @Override
    public void onBackPressed() {
        // load fragment-popular when back key pressed in other fragment
        if (selectedNav != R.id.nav_home) {
            selectedNav = R.id.nav_home;
            CURRENT_TAG = ConstantValue.HOME_FRAGMENT;
            loadFragment(selectedNav);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search: {
                startActivity(new Intent(getApplicationContext(), SearchResultActivity.class));
                break;
            }
            case R.id.menu_feedback: {
                startActivity(new Intent(getApplicationContext(), FeedBackActivity.class));
                break;
            }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            // popular fragment
            selectedNav = id;
            CURRENT_TAG = ConstantValue.HOME_FRAGMENT;
        } else if (id == R.id.nav_near_me) {
            // near you fragment
            selectedNav = id;
            CURRENT_TAG = ConstantValue.NEAR_ME_FRAGMENT;
        } else if (id == R.id.nav_save) {
            // save place fragment
            selectedNav = id;
            CURRENT_TAG = ConstantValue.SAVE_PLACE_FRAGMENT;
        } else if (id == R.id.nav_provinces) {
            // all provinces fragment
            selectedNav = id;
            CURRENT_TAG = ConstantValue.ALL_PROVINCE_FRAGMENT;
        } else if (id == R.id.nav_recommend) {
            // recommend fragment
            selectedNav = id;
            CURRENT_TAG = ConstantValue.RECOMMEND_FRAGMENT;
        } else if (id == R.id.nav_settings) {
            // start activity settings
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        } else if (id == R.id.nav_suggest) {
            // start activity feedback
            startActivity(new Intent(getApplicationContext(), SuggestNewPlaceActivity.class));
        } else if (id == R.id.nav_about) {
            // start activity about
            startActivity(new Intent(getApplicationContext(), AboutActivity.class));
        } else {
            selectedNav = R.id.nav_home;
        }
        loadFragment(selectedNav);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        int clickId = v.getId();
        if (clickId == R.id.nav_header_img_user_profile) {
            // if user click on navigation image, go ahead to sign in activity
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        } else if (clickId == R.id.nav_header_text_sign_out) {
            auth.signOut();
        }
    }

//    private void prepareLanguage() {
//        String defaultLanguage = LocaleHelper.getPersistedData(getApplicationContext());
//        Log.d("app", "main getPersistedData: " + defaultLanguage);
//        Log.d("app", "main Locale.getDefault: " + Locale.getDefault().toString());
//        if (defaultLanguage != null) {
//            if (!Locale.getDefault().toString().equals(defaultLanguage)) {
//                changeLocale(defaultLanguage);
////                recreate();
//            }
//        }
//    }

//    private void changeLocale(String languageToLoad) {
//        Locale locale = new Locale(languageToLoad);
//        Locale.setDefault(locale);
//        Configuration config = new Configuration();
//        config.locale = locale;
//        getBaseContext().getResources().updateConfiguration(config,
//                getBaseContext().getResources().getDisplayMetrics());
//        LocaleHelper.persist(getBaseContext(), languageToLoad);
////        recreate();
//    }

    private void loadFragment(int selectedNav) {
        // if user select the current navigation menu again,
        // don't do anything just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            setCurrentTitle(selectedNav);
            drawer.closeDrawers();
            return;
        }
        if (selectedNav == R.id.nav_home) {
            setFragment(new HomeFragment(), ConstantValue.HOME_FRAGMENT);
        } else if (selectedNav == R.id.nav_near_me) {
            setFragment(new NearMeFragment(), ConstantValue.NEAR_ME_FRAGMENT);
        } else if (selectedNav == R.id.nav_save) {
            setFragment(new SavePlaceFragment(), ConstantValue.SAVE_PLACE_FRAGMENT);
        } else if (selectedNav == R.id.nav_provinces) {
            setFragment(new AllProvinceFragment(), ConstantValue.ALL_PROVINCE_FRAGMENT);
        } else if (selectedNav == R.id.nav_recommend) {
            setFragment(new RecommendFragment(), ConstantValue.RECOMMEND_FRAGMENT);
        }
        // set checked navigation menu
        navigationView.setCheckedItem(selectedNav);
        // set title the same to selected navigation menu
        setCurrentTitle(selectedNav);
    }

    private void setFragment(Fragment fragment, String tag) {
        // replace main content with selected fragment
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.main_layout, fragment, tag)
                .commit();
    }

    private void setCurrentTitle(int selectedNav) {
        if (selectedNav == R.id.nav_home) {
            // popular
            setTitle(R.string.app_name);
        } else if (selectedNav == R.id.nav_near_me) {
            // near you
            setTitle(R.string.near_me);
        } else if (selectedNav == R.id.nav_save) {
            // save place
            setTitle(R.string.saved_place);
        } else if (selectedNav == R.id.nav_recommend) {
            // recommend
            setTitle(R.string.recommendation);
        } else {
            // tourist guide as default
            setTitle(R.string.app_name);
        }
    }

    // end main class
}
