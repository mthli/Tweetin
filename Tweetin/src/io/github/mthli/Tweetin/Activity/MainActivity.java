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
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.samples.apps.iosched.ui.widget.SlidingTabLayout;
import com.nineoldandroids.view.*;
import com.nineoldandroids.view.ViewPropertyAnimator;
import io.github.mthli.Tweetin.Custom.ViewUnit;
import io.github.mthli.Tweetin.Flag.Flag;
import io.github.mthli.Tweetin.Fragment.BaseFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Task.GetAccessTokenTask;

public class MainActivity extends FragmentActivity implements ObservableScrollViewCallbacks {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private View header;
    private Toolbar toolbar;
    private RelativeLayout searchView;
    private EditText searchViewEditText;
    private SlidingTabLayout slidingTabLayout;
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
        header = findViewById(R.id.main_header);
        ViewCompat.setElevation(header, ViewUnit.getElevation(this, 2));

        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
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

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setCustomTabView(R.layout.badge_view, R.id.badge_view_textview);
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.white));
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);

        slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                /* Do nothing */
            }

            @Override
            public void onPageSelected(int i) {
                propagateToolbarState(isToolbarShown());
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                /* Do nothing */
            }
        });

        propagateToolbarState(isToolbarShown());
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

            ViewCompat.setElevation(searchView, ViewUnit.getElevation(this, 4));
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
                    ViewCompat.setElevation(searchView, 0);
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

    private int baseTranslationY;

    public boolean isToolbarShown() {
        return (ViewHelper.getTranslationY(header) == 0);
    }

    private void showToolbar() {
        if (ViewHelper.getTranslationY(header) != 0) {
            ViewPropertyAnimator.animate(header).cancel();
            ViewPropertyAnimator.animate(header).translationY(0).setDuration(256);
        }

        propagateToolbarState(true);
    }

    private void hideToolbar() {
        if (ViewHelper.getTranslationY(header) != -toolbar.getHeight()) {
            ViewPropertyAnimator.animate(header).cancel();
            ViewPropertyAnimator.animate(header).translationY(-toolbar.getHeight()).setDuration(256);
        }

        propagateToolbarState(false);
    }

    public BaseFragment getCurrentFragment() {
        return (BaseFragment) mainPagerAdapter.getFragmentFromPosition(viewPager.getCurrentItem());
    }

    public BaseFragment getFragmentFromPosition(int position) {
        return (BaseFragment) mainPagerAdapter.getFragmentFromPosition(position);
    }

    private void propagateToolbarState(boolean show) {
        mainPagerAdapter.setScrollY(show ? 0 : toolbar.getHeight());

        for (int i = 0; i < mainPagerAdapter.getCount(); i++) {
            if (i == viewPager.getCurrentItem() || getFragmentFromPosition(i) == null) {
                continue;
            }

            ObservableListView listView = getFragmentFromPosition(i).getListView();

            if (show) {
                if (listView.getCurrentScrollY() > 0) {
                    listView.setSelection(0);
                }
            } else {
                if (listView.getCurrentScrollY() < toolbar.getHeight()) {
                    listView.setSelection(1);
                }
            }
        }
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        if (dragging) {
            if (firstScroll) {
                if (-toolbar.getHeight() < ViewHelper.getTranslationY(header)) {
                    baseTranslationY = scrollY;
                }
            }

            int headerTranslationY = Math.min(0, Math.max(-toolbar.getHeight(), -(scrollY - baseTranslationY)));

            ViewPropertyAnimator.animate(header).cancel();
            ViewHelper.setTranslationY(header, headerTranslationY);
        }
    }

    @Override
    public void onDownMotionEvent() {
        /* Do nothing */
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        baseTranslationY = 0;

        ObservableListView listView = getCurrentFragment().getListView();

        if (scrollState == ScrollState.UP) {
            if (toolbar.getHeight() < listView.getCurrentScrollY()) {
                hideToolbar();
            } else if (toolbar.getHeight() > listView.getCurrentScrollY()) {
                showToolbar();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (toolbar.getHeight() < listView.getCurrentScrollY()) {
                showToolbar();
            }
        }
    }
}
