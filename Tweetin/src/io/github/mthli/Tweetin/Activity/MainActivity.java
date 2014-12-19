package io.github.mthli.Tweetin.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import io.github.mthli.Tweetin.Fragment.FavoriteFragment;
import io.github.mthli.Tweetin.Fragment.MentionFragment;
import io.github.mthli.Tweetin.Fragment.TimelineFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Task.Initialize.GetAccessTokenTask;
import io.github.mthli.Tweetin.Custom.BadgeView;

public class MainActivity extends FragmentActivity {

    private int fragmentFlag = Flag.IN_TIMELINE_FRAGMENT;
    public int getFragmentFlag() {
        return fragmentFlag;
    }
    public void setFragmentFlag(int fragmentFlag) {
        this.fragmentFlag = fragmentFlag;
    }

    private TimelineFragment timelineFragment;
    public TimelineFragment getTimelineFragment() {
        return timelineFragment;
    }

    private MentionFragment mentionFragment;
    public MentionFragment getMentionFragment() {
        return mentionFragment;
    }

    private FavoriteFragment favoriteFragment;
    public FavoriteFragment getFavoriteFragment() {
        return favoriteFragment;
    }

    public Fragment getCurrentFragment() {
        switch (fragmentFlag) {
            case Flag.IN_TIMELINE_FRAGMENT:
                return timelineFragment;
            case Flag.IN_MENTION_FRAGMENT:
                return mentionFragment;
            case Flag.IN_FAVORITE_FRAGMENT:
                return favoriteFragment;
            default:
                return timelineFragment;
        }
    }

    public Fragment getFragmentFromPosition(int position) {
        switch (position) {
            case Flag.IN_TIMELINE_FRAGMENT:
                return timelineFragment;
            case Flag.IN_MENTION_FRAGMENT:
                return mentionFragment;
            case Flag.IN_FAVORITE_FRAGMENT:
                return favoriteFragment;
            default:
                return timelineFragment;
        }
    }

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private Toolbar toolbar;

    private RelativeLayout searchView;
    private EditText searchViewEditText;

    private ViewPager viewPager;
    private TabHost tabHost;
    private TabWidget tabWidget;
    private View tabIndicator;

    private float elevation = 0f;

    private void initUserInterface() {
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setElevation(elevation * 2);
        setActionBar(toolbar);

        searchView = (RelativeLayout) findViewById(R.id.search_view);
        searchViewEditText = (EditText) findViewById(R.id.search_view_edittext);
        ImageButton searchViewClear = (ImageButton) findViewById(R.id.search_view_clear);
        searchView.setElevation(0);

        searchViewClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchViewEditText.setText(null);
            }
        });

        viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        viewPager.setOffscreenPageLimit(5);
        viewPager.setAdapter(new MainPagerAdapter(this));

        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();
        tabHost.setElevation(elevation * 2);

        tabWidget = (TabWidget) findViewById(android.R.id.tabs);
        tabWidget.setStripEnabled(false);
        tabWidget.setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);

        tabIndicator = findViewById(R.id.tab_indicator);

        String[] tabs = getResources().getStringArray(R.array.tabs);
        for (int i = 0; i < 3; i++) {
            BadgeView badgeView = new BadgeView(this);
            badgeView.setText(tabs[i]);

            tabHost.addTab(
                    tabHost.newTabSpec(String.valueOf(i))
                            .setIndicator(badgeView)
                            .setContent(android.R.id.tabcontent)
            );
        }

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                fragmentFlag = Integer.valueOf(tabId);
                viewPager.setCurrentItem(fragmentFlag);
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            private int scrollingState = ViewPager.SCROLL_STATE_IDLE;

            private void updateIndicatorPosition(int position, float positionOffset) {
                View tabView = tabWidget.getChildTabViewAt(position);
                int indicatorWidth = tabView.getWidth();
                int indicatorLeft = (int) ((position + positionOffset) * indicatorWidth);

                final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) tabIndicator.getLayoutParams();
                layoutParams.width = indicatorWidth;
                layoutParams.setMargins(indicatorLeft, 0, 0, 0);
                tabIndicator.setLayoutParams(layoutParams);
            }

            @Override
            public void onPageSelected(int position) {
                if (scrollingState == ViewPager.SCROLL_STATE_IDLE) {
                    updateIndicatorPosition(position, 0);
                }
                fragmentFlag = position;
                tabHost.setCurrentTab(fragmentFlag);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                scrollingState = state;
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                updateIndicatorPosition(position, positionOffset);
            }
        });
    }

    private void setCustomThemeFirst() {

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

    private void setCustomThemeSecond() {
        ColorStateList colorStateList;

        int spColorValue = sharedPreferences.getInt(
                getString(R.string.sp_color),
                0
        );

        switch (spColorValue) {
            case Flag.COLOR_BLUE:
                tabHost.setBackgroundColor(getResources().getColor(R.color.blue_500));
                colorStateList = getResources().getColorStateList(R.color.tab_widget_selector_blue);
                break;
            case Flag.COLOR_ORANGE:
                tabHost.setBackgroundColor(getResources().getColor(R.color.orange_500));
                colorStateList = getResources().getColorStateList(R.color.tab_widget_selector_orange);
                break;
            case Flag.COLOR_PINK:
                tabHost.setBackgroundColor(getResources().getColor(R.color.pink_500));
                colorStateList = getResources().getColorStateList(R.color.tab_widget_selector_pink);
                break;
            case Flag.COLOR_PURPLE:
                tabHost.setBackgroundColor(getResources().getColor(R.color.purple_500));
                colorStateList = getResources().getColorStateList(R.color.tab_widget_selector_purple);
                break;
            case Flag.COLOR_TEAL:
                tabHost.setBackgroundColor(getResources().getColor(R.color.teal_500));
                colorStateList = getResources().getColorStateList(R.color.tab_widget_selector_teal);
                break;
            default:
                tabHost.setBackgroundColor(getResources().getColor(R.color.blue_500));
                colorStateList = getResources().getColorStateList(R.color.tab_widget_selector_blue);
                break;
        }

        for (int i = 0; i < 3; i++) {
            BadgeView badgeView = (BadgeView) tabHost.getTabWidget().getChildTabViewAt(i);
            badgeView.setCustomTheme(colorStateList);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(
                getString(R.string.sp_name),
                MODE_PRIVATE
        );
        editor = sharedPreferences.edit();

        setCustomThemeFirst();
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
        }

        timelineFragment = new TimelineFragment();
        mentionFragment = new MentionFragment();
        favoriteFragment = new FavoriteFragment();

        elevation = getResources().getDisplayMetrics().density;

        initUserInterface();
        setCustomThemeSecond();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = MainActivity.this.getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
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

            searchView.setElevation(elevation * 4);
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
                    searchView.setElevation(0);
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
