package io.github.mthli.Tweetin.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import io.github.mthli.Tweetin.Fragment.Base.ListFragment;
import io.github.mthli.Tweetin.Fragment.TweetList.FavoriteFragment;
import io.github.mthli.Tweetin.Fragment.TweetList.MentionFragment;
import io.github.mthli.Tweetin.Fragment.TweetList.TimelineFragment;
import io.github.mthli.Tweetin.View.ViewUnit;
import io.github.mthli.Tweetin.Flag.FlagUnit;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Task.OAuth.GetAccessTokenTask;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {
    private RelativeLayout searchView;
    private EditText searchViewEditText;

    private View mentionTab;
    private ViewPager viewPager;
    private MainPagerAdapter mainPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewUnit.setCustomTheme(this);
        setContentView(R.layout.main);

        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(getString(R.string.app_callback_url))) {
            String oAuthVerifier = uri.getQueryParameter(getString(R.string.app_oauth_verifier));
            GetAccessTokenTask getAccessTokenTask = new GetAccessTokenTask(this, oAuthVerifier);
            getAccessTokenTask.execute();
        } else {
            initUI();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.main_menu_search:
                showSearchView(true);
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
                return false;
            }
        }

        return super.dispatchTouchEvent(motionEvent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (searchView != null && searchView.isShown()) {
                showSearchView(false);
            } else {
                SlidingUpPanelLayout slidingUpPanelLayout = getCurrentListFragment().getSlidingUpPanelLayout();
                if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                } else {
                    cancelAllTasks();
                    finish();
                }
            }
        }

        return true;
    }

    @Override
    public void onDestroy() {
        cancelAllTasks();
        super.onDestroy();
    }

    public void initUI() {
        View header = findViewById(R.id.main_header);
        header.setVisibility(View.VISIBLE);
        ViewCompat.setElevation(header, ViewUnit.getElevation(this, 2));

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setActionBar(toolbar);
        getActionBar().setTitle(null);
        getActionBar().setDisplayShowHomeEnabled(false);

        searchView = (RelativeLayout) findViewById(R.id.search_view);
        searchViewEditText = (EditText) findViewById(R.id.search_view_edittext);
        ImageButton searchViewClear = (ImageButton) findViewById(R.id.search_view_clear);
        ViewCompat.setElevation(searchView, ViewUnit.getElevation(this, 4));

        searchViewEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String keyWord = searchViewEditText.getText().toString();

                    if (keyWord.length() > 0) {
                        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                        intent.putExtra(getString(R.string.search_intent_key_word), keyWord);
                        startActivity(intent);

                        showSearchView(false);
                    } else {
                        Toast.makeText(MainActivity.this, R.string.search_toast_empty, Toast.LENGTH_SHORT).show();
                    }
                }

                return true;
            }
        });

        searchViewClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchViewEditText.setText(null);
            }
        });
        searchViewClear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(MainActivity.this, getString(R.string.search_view_toast_clear), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        viewPager.setOffscreenPageLimit(5);
        mainPagerAdapter = new MainPagerAdapter(this);
        viewPager.setAdapter(mainPagerAdapter);

        final TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();

        LayoutInflater layoutInflater = getLayoutInflater();
        final List<ImageView> tabIconList = new ArrayList<ImageView>();

        View timelineTab = layoutInflater.inflate(R.layout.badge_view, null);
        ImageView timelineTabIcon = (ImageView) timelineTab.findViewById(R.id.badge_view_icon);
        timelineTabIcon.setImageResource(R.drawable.ic_tab_timeline);
        timelineTabIcon.setImageAlpha(255);
        timelineTab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(MainActivity.this, mainPagerAdapter.getPageTitle(0), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        tabIconList.add(timelineTabIcon);

        mentionTab = layoutInflater.inflate(R.layout.badge_view, null);
        ImageView mentionTabIcon = (ImageView) mentionTab.findViewById(R.id.badge_view_icon);
        mentionTabIcon.setImageResource(R.drawable.ic_tab_mention);
        mentionTabIcon.setImageAlpha(153);
        mentionTab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(MainActivity.this, mainPagerAdapter.getPageTitle(1), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        tabIconList.add(mentionTabIcon);

        View favoriteTab = layoutInflater.inflate(R.layout.badge_view, null);
        ImageView favoriteTabIcon = (ImageView) favoriteTab.findViewById(R.id.badge_view_icon);
        favoriteTabIcon.setImageResource(R.drawable.ic_tab_favorite);
        favoriteTabIcon.setImageAlpha(153);
        favoriteTab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(MainActivity.this, mainPagerAdapter.getPageTitle(2), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        tabIconList.add(favoriteTabIcon);

        tabHost.addTab(tabHost.newTabSpec("0").setIndicator(timelineTab).setContent(android.R.id.tabcontent));
        tabHost.addTab(tabHost.newTabSpec("1").setIndicator(mentionTab).setContent(android.R.id.tabcontent));
        tabHost.addTab(tabHost.newTabSpec("2").setIndicator(favoriteTab).setContent(android.R.id.tabcontent));

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                for (ImageView icon : tabIconList) {
                    icon.setImageAlpha(153);
                }
                tabIconList.get(Integer.valueOf(tabId)).setImageAlpha(255);

                if (isBadgeShown() && Integer.valueOf(tabId) == FlagUnit.IN_MENTION_FRAGMENT) {
                    MentionFragment mentionFragment = (MentionFragment) mainPagerAdapter.getListFragmentFromPosition(FlagUnit.IN_MENTION_FRAGMENT);
                    mentionFragment.getLatestMentions();

                    showBadge(false);
                }

                viewPager.setCurrentItem(Integer.valueOf(tabId));
            }
        });

        final TabWidget tabWidget = (TabWidget) findViewById(android.R.id.tabs);
        final View tabIndicator = findViewById(R.id.tab_indicator);

        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            private int scrollState = ViewPager.SCROLL_STATE_IDLE;

            private void updateIndicatorPosition(int position, float positionOffset) {
                View tabView = tabWidget.getChildTabViewAt(position);
                int tabIndicatorWidth = tabView.getWidth();
                int tabIndicatorLeft = (int) ((position + positionOffset) * tabIndicatorWidth);

                final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) tabIndicator.getLayoutParams();
                layoutParams.width = tabIndicatorWidth;
                layoutParams.setMargins(tabIndicatorLeft, 0, 0, 0);
                tabIndicator.setLayoutParams(layoutParams);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                scrollState = state;
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                updateIndicatorPosition(position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                if (scrollState == ViewPager.SCROLL_STATE_IDLE) {
                    updateIndicatorPosition(position, 0);
                }

                for (ImageView icon : tabIconList) {
                    icon.setImageAlpha(153);
                }
                tabIconList.get(position).setImageAlpha(255);

                if (isBadgeShown() && position == FlagUnit.IN_MENTION_FRAGMENT) {
                    MentionFragment mentionFragment = (MentionFragment) mainPagerAdapter.getListFragmentFromPosition(FlagUnit.IN_MENTION_FRAGMENT);
                    mentionFragment.getLatestMentions();

                    showBadge(false);
                }

                tabWidget.setCurrentTab(position);
            }
        });
    }

    public void showBadge(boolean show) {
        View bubble = mentionTab.findViewById(R.id.badge_view_bubble);
        if (show) {
            bubble.setVisibility(View.VISIBLE);
        } else {
            bubble.setVisibility(View.GONE);
        }
    }

    public boolean isBadgeShown() {
        return mentionTab.findViewById(R.id.badge_view_bubble).getVisibility() == View.VISIBLE;
    }

    private void showSearchView(boolean show) {
        View view = findViewById(R.id.main_menu_search);

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

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

            return !(left <= motionEvent.getRawX() && motionEvent.getRawX() <= right && top <= motionEvent.getRawY() && motionEvent.getRawY() <= bottom);
        }

        return false;
    }

    public ListFragment getCurrentListFragment() {
        return mainPagerAdapter.getListFragmentFromPosition(viewPager.getCurrentItem());
    }

    private void cancelAllTasks() {
        ((TimelineFragment) mainPagerAdapter.getListFragmentFromPosition(FlagUnit.IN_TIMELINE_FRAGMENT)).cancelAllTasks();
        ((MentionFragment) mainPagerAdapter.getListFragmentFromPosition(FlagUnit.IN_MENTION_FRAGMENT)).cancelAllTasks();
        ((FavoriteFragment) mainPagerAdapter.getListFragmentFromPosition(FlagUnit.IN_FAVORITE_FRAGMENT)).cancelAllTasks();
    }

    // TODO: onActivityResult();
}