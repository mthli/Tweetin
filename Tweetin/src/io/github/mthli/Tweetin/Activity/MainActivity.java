package io.github.mthli.Tweetin.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.google.samples.apps.iosched.ui.widget.SlidingTabLayout;
import io.github.mthli.Tweetin.Custom.ViewUnit;
import io.github.mthli.Tweetin.Flag.Flag;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Task.GetAccessTokenTask;

public class MainActivity extends FragmentActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private RelativeLayout searchView;
    private EditText searchViewEditText;

    private ViewPager viewPager;
    private MainPagerAdapter mainPagerAdapter;

    public void setCustomTheme() {
        int spColorValue = sharedPreferences.getInt(
                getString(R.string.sp_color),
                0
        );

        switch (spColorValue) {
            case Flag.COLOR_BLUE:
                setTheme(R.style.BaseAppTheme_Blue);
                break;
            case Flag.COLOR_ORANGE:
                setTheme(R.style.BaseAppTheme_Orange);
                break;
            case Flag.COLOR_PINK:
                setTheme(R.style.BaseAppTheme_Pink);
                break;
            case Flag.COLOR_PURPLE:
                setTheme(R.style.BaseAppTheme_Purple);
                break;
            case Flag.COLOR_TEAL:
                setTheme(R.style.BaseAppTheme_Teal);
                break;
            default:
                setTheme(R.style.BaseAppTheme_Blue);
                break;
        }
    }

    public void initUI() {
        View header = findViewById(R.id.main_header);
        ViewCompat.setElevation(header, 2); //

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setActionBar(toolbar);

        searchView = (RelativeLayout) findViewById(R.id.search_view);
        searchViewEditText = (EditText) findViewById(R.id.search_view_edittext);
        ImageButton searchViewClear = (ImageButton) findViewById(R.id.search_view_clear);
        ViewCompat.setElevation(searchView, ViewUnit.getElevation(this, 4));

        searchViewClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchViewEditText.setText(null);
            }
        });

        viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        viewPager.setOffscreenPageLimit(5);
        mainPagerAdapter = new MainPagerAdapter(this);
        viewPager.setAdapter(mainPagerAdapter);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setCustomTabView(R.layout.badge_view, R.id.badge_view_textview);
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.white));
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(
                getString(R.string.sp_tweetin),
                MODE_PRIVATE
        );
        editor = sharedPreferences.edit();

        setCustomTheme();
        setContentView(R.layout.main);

        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(getString(R.string.app_callback_url))) {

            String oAuthVerifier = uri.getQueryParameter(
                    getString(R.string.app_oauth_verifier)
            );

            GetAccessTokenTask getAccessTokenTask = new GetAccessTokenTask(
                    this,
                    oAuthVerifier
            );
            getAccessTokenTask.execute();
        } else {
            initUI();
        }
    }

    private void showSearchView(boolean show) {
        View view = findViewById(R.id.main_menu_search);

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE
        );

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int cx = (int) (location[0] + view.getPivotX() / 1.5);
        int cy = (int) (location[1] - view.getPivotY() / 4);

        if (show) {
            Animator anim = ViewAnimationUtils.createCircularReveal(
                    searchView,
                    cx,
                    cy,
                    0,
                    searchView.getWidth()
            );

            searchView.setVisibility(View.VISIBLE);
            anim.start();

            if (searchView.requestFocus()) {
                inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } else {
            Animator anim = ViewAnimationUtils.createCircularReveal(
                    searchView,
                    cx,
                    cy,
                    searchView.getWidth(),
                    0
            );

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    searchViewEditText.setText(null);
                    searchView.setVisibility(View.INVISIBLE);
                }
            });

            anim.start();

            inputMethodManager.hideSoftInputFromWindow(
                    getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS
            );
        }
    }

    private boolean shouldHideSearchView(View view, MotionEvent motionEvent) {
        if (view != null && (view instanceof EditText)) {
            int[] location = new int[2];
            searchView.getLocationOnScreen(location);

            int left = location[0];
            int right = left + searchView.getWidth();
            int top = location[1];
            int bottom = top + searchView.getHeight();

            if (left <= motionEvent.getRawX() && motionEvent.getRawX() <= right && top <= motionEvent.getRawY() && motionEvent.getRawY() <= bottom) {
                return false;
            } else {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = MainActivity.this.getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.main_menu_search:
                showSearchView(true);
                break;
            case R.id.main_menu_post:
                /* Do something */
                break;
            case R.id.main_menu_setting:
                /* Do something */
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();

            if (shouldHideSearchView(view, motionEvent)) {
                showSearchView(false);
            }
        }

        return super.dispatchTouchEvent(motionEvent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (searchView.isShown()) {
                showSearchView(false);
            } else {
                finish();
            }
        }

        return false;
    }
}
