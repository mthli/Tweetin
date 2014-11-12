package io.github.mthli.Tweetin.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;
import io.github.mthli.Tweetin.Database.Favorite.FavoriteAction;
import io.github.mthli.Tweetin.Database.Mention.MentionAction;
import io.github.mthli.Tweetin.Database.Timeline.TimelineAction;
import io.github.mthli.Tweetin.Fragment.*;
import io.github.mthli.Tweetin.Fragment.DiscoveryFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Anim.ActivityAnim;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {
    private TimelineFragment timelineFragment;
    private MentionFragment mentionFragment;
    private FavoriteFragment favoriteFragment;
    private DiscoveryFragment discoveryFragment;
    private SettingFragment settingFragment;
    private int fragmentFlag = Flag.IN_TIMELINE_FRAGMENT;

    private ResideMenu resideMenu;
    private ResideMenuItem timelineItem;
    private ResideMenuItem mentionItem;
    private ResideMenuItem favoriteItem;
    private ResideMenuItem discoveryItem;
    private ResideMenuItem settingItem;

    private void selectResideMenuItem(int targetFragmentFlag) {
        ImageView imageView;
        TextView textView;
        switch (fragmentFlag) {
            case Flag.IN_TIMELINE_FRAGMENT:
                imageView = (ImageView) timelineItem
                        .findViewById(R.id.iv_icon);
                textView = (TextView) timelineItem
                        .findViewById(R.id.tv_title);
                imageView.setImageDrawable(
                        getResources().getDrawable(R.drawable.ic_action_timeline_default)
                );
                textView.setTextColor(
                        getResources().getColor(R.color.white)
                );
                break;
            case Flag.IN_MENTION_FRAGMENT:
                imageView = (ImageView) mentionItem
                        .findViewById(R.id.iv_icon);
                textView = (TextView) mentionItem
                        .findViewById(R.id.tv_title);
                imageView.setImageDrawable(
                        getResources().getDrawable(R.drawable.ic_action_mention_default)
                );
                textView.setTextColor(
                        getResources().getColor(R.color.white)
                );
                break;
            case Flag.IN_FAVORITE_FRAGMENT:
                imageView = (ImageView) favoriteItem
                        .findViewById(R.id.iv_icon);
                textView = (TextView) favoriteItem
                        .findViewById(R.id.tv_title);
                imageView.setImageDrawable(
                        getResources().getDrawable(R.drawable.ic_action_favorite_default)
                );
                textView.setTextColor(
                        getResources().getColor(R.color.white)
                );
                break;
            case Flag.IN_DISCOVERY_FRAGMENT:
                imageView = (ImageView) discoveryItem
                        .findViewById(R.id.iv_icon);
                textView = (TextView) discoveryItem
                        .findViewById(R.id.tv_title);
                imageView.setImageDrawable(
                        getResources().getDrawable(R.drawable.ic_action_discovery_default)
                );
                textView.setTextColor(
                        getResources().getColor(R.color.white)
                );
                break;
            case Flag.IN_SETTING_FRAGMENT:
                imageView = (ImageView) settingItem
                        .findViewById(R.id.iv_icon);
                textView = (TextView) settingItem
                        .findViewById(R.id.tv_title);
                imageView.setImageDrawable(
                        getResources().getDrawable(R.drawable.ic_action_setting_default)
                );
                textView.setTextColor(
                        getResources().getColor(R.color.white)
                );
                break;
            default:
                break;
        }

        switch (targetFragmentFlag) {
            case Flag.IN_TIMELINE_FRAGMENT:
                imageView = (ImageView) timelineItem
                        .findViewById(R.id.iv_icon);
                textView = (TextView) timelineItem
                        .findViewById(R.id.tv_title);
                imageView.setImageDrawable(
                        getResources().getDrawable(R.drawable.ic_action_timeline_active)
                );
                textView.setTextColor(
                        getResources().getColor(R.color.blue)
                );
                break;
            case Flag.IN_MENTION_FRAGMENT:
                imageView = (ImageView) mentionItem
                        .findViewById(R.id.iv_icon);
                textView = (TextView) mentionItem
                        .findViewById(R.id.tv_title);
                imageView.setImageDrawable(
                        getResources().getDrawable(R.drawable.ic_action_mention_active)
                );
                textView.setTextColor(
                        getResources().getColor(R.color.blue)
                );
                break;
            case Flag.IN_FAVORITE_FRAGMENT:
                imageView = (ImageView) favoriteItem
                        .findViewById(R.id.iv_icon);
                textView = (TextView) favoriteItem
                        .findViewById(R.id.tv_title);
                imageView.setImageDrawable(
                        getResources().getDrawable(R.drawable.ic_action_favorite_active)
                );
                textView.setTextColor(
                        getResources().getColor(R.color.blue)
                );
                break;
            case Flag.IN_DISCOVERY_FRAGMENT:
                imageView = (ImageView) discoveryItem
                        .findViewById(R.id.iv_icon);
                textView = (TextView) discoveryItem
                        .findViewById(R.id.tv_title);
                imageView.setImageDrawable(
                        getResources().getDrawable(R.drawable.ic_action_discovery_active)
                );
                textView.setTextColor(
                        getResources().getColor(R.color.blue)
                );
                break;
            case Flag.IN_SETTING_FRAGMENT:
                imageView = (ImageView) settingItem
                        .findViewById(R.id.iv_icon);
                textView = (TextView) settingItem
                        .findViewById(R.id.tv_title);
                imageView.setImageDrawable(
                        getResources().getDrawable(R.drawable.ic_action_setting_active)
                );
                textView.setTextColor(
                        getResources().getColor(R.color.blue)
                );
                break;
            default:
                break;
        }
    }
    private Fragment getCurrentFragment() {
        switch (fragmentFlag) {
            case Flag.IN_TIMELINE_FRAGMENT:
                return timelineFragment;
            case Flag.IN_MENTION_FRAGMENT:
                return mentionFragment;
            case Flag.IN_FAVORITE_FRAGMENT:
                return favoriteFragment;
            case Flag.IN_DISCOVERY_FRAGMENT:
                EditText editText = discoveryFragment.getSearchBox();
                editText.clearFocus();
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(
                                editText.getWindowToken(),
                                0
                        );
                return discoveryFragment;
            case Flag.IN_SETTING_FRAGMENT:
                return settingFragment;
            default:
                return timelineFragment;
        }
    }
    private void initResideMenu() {
        resideMenu = new ResideMenu(this);
        resideMenu.setShadowVisible(false);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        resideMenu.setBackgroundColor(
                getResources().getColor(R.color.black)
        );
        resideMenu.attachToActivity(this);
        final List<ResideMenuItem> resideMenuItemList = new ArrayList<ResideMenuItem>();
        timelineItem = new ResideMenuItem(
                this,
                R.drawable.ic_action_timeline_active,
                R.string.reside_menu_item_timeline
        );
        TextView textView = (TextView) timelineItem
                .findViewById(R.id.tv_title);
        textView.setTextColor(
                getResources().getColor(R.color.blue)
        );
        mentionItem = new ResideMenuItem(
                this,
                R.drawable.ic_action_mention,
                R.string.reside_menu_item_mention
        );
        favoriteItem = new ResideMenuItem(
                this,
                R.drawable.ic_action_favorite_default,
                R.string.reside_menu_item_favorite
        );
        discoveryItem = new ResideMenuItem(
                this,
                R.drawable.ic_action_discovery_default,
                R.string.reside_menu_item_discovery
        );
        settingItem = new ResideMenuItem(
                this,
                R.drawable.ic_action_setting_default,
                R.string.reside_menu_item_setting
        );
        resideMenuItemList.add(timelineItem);
        resideMenuItemList.add(mentionItem);
        resideMenuItemList.add(favoriteItem);
        resideMenuItemList.add(discoveryItem);
        resideMenuItemList.add(settingItem);
        resideMenu.setMenuItems(
                resideMenuItemList,
                ResideMenu.DIRECTION_LEFT
        );

        timelineItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragmentFlag != Flag.IN_TIMELINE_FRAGMENT) {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                            .beginTransaction();
                    fragmentTransaction.hide(
                            getCurrentFragment()
                    ).show(timelineFragment);
                    selectResideMenuItem(Flag.IN_TIMELINE_FRAGMENT);
                    fragmentFlag = Flag.IN_TIMELINE_FRAGMENT;
                    fragmentTransaction.commit();
                    resideMenu.closeMenu();
                } else {
                    resideMenu.closeMenu();
                }
            }
        });
        mentionItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragmentFlag != Flag.IN_MENTION_FRAGMENT) {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                            .beginTransaction();
                    if (mentionFragment.isAdded()) {
                        fragmentTransaction.hide(
                                getCurrentFragment()
                        ).show(mentionFragment);
                    } else {
                        fragmentTransaction.hide(
                                getCurrentFragment()
                        ).add(android.R.id.content, mentionFragment);
                    }
                    selectResideMenuItem(Flag.IN_MENTION_FRAGMENT);
                    fragmentFlag = Flag.IN_MENTION_FRAGMENT;
                    fragmentTransaction.commit();
                    resideMenu.closeMenu();
                } else {
                    resideMenu.closeMenu();
                }
            }
        });
        favoriteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fragmentFlag != Flag.IN_FAVORITE_FRAGMENT) {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                            .beginTransaction();
                    if (favoriteFragment.isAdded()) {
                        fragmentTransaction.hide(
                                getCurrentFragment()
                        ).show(favoriteFragment);
                    } else {
                        fragmentTransaction.hide(
                                getCurrentFragment()
                        ).add(android.R.id.content, favoriteFragment);
                    }
                    selectResideMenuItem(Flag.IN_FAVORITE_FRAGMENT);
                    fragmentFlag = Flag.IN_FAVORITE_FRAGMENT;
                    fragmentTransaction.commit();
                    resideMenu.closeMenu();
                } else {
                    resideMenu.closeMenu();
                }
            }
        });
        discoveryItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fragmentFlag != Flag.IN_DISCOVERY_FRAGMENT) {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                            .beginTransaction();
                    if (discoveryFragment.isAdded()) {
                        fragmentTransaction.hide(
                                getCurrentFragment()
                        ).show(discoveryFragment);
                    } else {
                        fragmentTransaction.hide(
                                getCurrentFragment()
                        ).add(android.R.id.content, discoveryFragment);
                    }
                    selectResideMenuItem(Flag.IN_DISCOVERY_FRAGMENT);
                    fragmentFlag = Flag.IN_DISCOVERY_FRAGMENT;
                    fragmentTransaction.commit();
                    resideMenu.closeMenu();
                } else {
                    resideMenu.closeMenu();
                }
            }
        });
        settingItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragmentFlag != Flag.IN_SETTING_FRAGMENT) {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                            .beginTransaction();
                    if (settingFragment.isAdded()) {
                        fragmentTransaction.hide(
                                getCurrentFragment()
                        ).show(settingFragment);
                    } else {
                        fragmentTransaction.hide(
                                getCurrentFragment()
                        ).add(android.R.id.content, settingFragment);
                    }
                    selectResideMenuItem(Flag.IN_SETTING_FRAGMENT);
                    fragmentFlag = Flag.IN_SETTING_FRAGMENT;
                    fragmentTransaction.commit();
                    resideMenu.closeMenu();
                } else {
                    resideMenu.closeMenu();
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initResideMenu();
        timelineFragment = new TimelineFragment();
        mentionFragment = new MentionFragment();
        favoriteFragment = new FavoriteFragment();
        discoveryFragment = new DiscoveryFragment();
        settingFragment = new SettingFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.add(android.R.id.content, timelineFragment);
        fragmentFlag = Flag.IN_TIMELINE_FRAGMENT;
        fragmentTransaction.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            int position = data.getIntExtra(
                    getString(R.string.detail_intent_from_position),
                    -1
            );
            boolean deleteFromDetail = data.getBooleanExtra(
                    getString(R.string.detail_intent_is_delete_at_detail),
                    false
            );
            boolean retweetFromDetail = data.getBooleanExtra(
                    getString(R.string.detail_intent_is_retweet_at_detail),
                    false
            );
            boolean favoriteFromDetail = data.getBooleanExtra(
                    getString(R.string.detail_intent_is_favorite_at_detail),
                    false
            );
            TweetAdapter tweetAdapter;
            List<Tweet> tweetList;
            switch (fragmentFlag) {
                case Flag.IN_TIMELINE_FRAGMENT:
                    tweetAdapter = timelineFragment.getTweetAdapter();
                    tweetList = timelineFragment.getTweetList();
                    break;
                case Flag.IN_MENTION_FRAGMENT:
                    tweetAdapter = mentionFragment.getTweetAdapter();
                    tweetList = mentionFragment.getTweetList();
                    break;
                case Flag.IN_FAVORITE_FRAGMENT:
                    tweetAdapter = favoriteFragment.getTweetAdapter();
                    tweetList = favoriteFragment.getTweetList();
                    break;
                case Flag.IN_DISCOVERY_FRAGMENT:
                    tweetAdapter = discoveryFragment.getTweetAdapter();
                    tweetList = discoveryFragment.getTweetList();
                    break;
                default:
                    tweetAdapter = timelineFragment.getTweetAdapter();
                    tweetList = timelineFragment.getTweetList();
                    break;
            }
            if (position >= 0) {
                Tweet tweet = tweetList.get(position);
                if (retweetFromDetail) {
                    tweet.setRetweet(true);
                    tweet.setRetweetedByUserId(timelineFragment.getUseId());
                    tweet.setRetweetedByUserName(
                            getString(R.string.tweet_info_retweeted_by_me)
                    );
                }
                if (favoriteFromDetail) {
                    tweet.setFavorite(true);
                }
                if (deleteFromDetail) {
                    tweetList.remove(position);
                }
                tweetAdapter.notifyDataSetChanged();
            }
        }
    }

    private void cancelAllFragmentTask() {
        timelineFragment.cancelAllTask();
        mentionFragment.cancelAllTask();
        favoriteFragment.cancelAllTask();
        discoveryFragment.cancelAllTask();
    }
    private void clearAllDatabase() {
        TimelineAction timelineAction = new TimelineAction(this);
        timelineAction.openDatabase(true);
        timelineAction.deleteAll();
        timelineAction.closeDatabase();
        MentionAction mentionAction = new MentionAction(this);
        mentionAction.openDatabase(true);
        mentionAction.deleteAll();
        mentionAction.closeDatabase();
        FavoriteAction favoriteAction = new FavoriteAction(this);
        favoriteAction.openDatabase(true);
        favoriteAction.deleteAll();
        favoriteAction.closeDatabase();
    }
    private void clearSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.sp_name),
                MODE_PRIVATE
        );
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(
                getString(R.string.sp_use_id),
                -1l
        );
        editor.putString(
                getString(R.string.sp_consumer_key),
                null
        );
        editor.putString(
                getString(R.string.splash_consumer_secret),
                null
        );
        editor.putString(
                getString(R.string.sp_access_token),
                null
        );
        editor.putString(
                getString(R.string.sp_access_token_secret),
                null
        );
        editor.putBoolean(
                getString(R.string.sp_is_timeline_first),
                true
        );
        editor.putLong(
                getString(R.string.sp_latest_mention_id),
                -1l
        );
        editor.putBoolean(
                getString(R.string.sp_is_favorite_first),
                true
        );
        editor.commit();
    }
    public void signOut() {
        cancelAllFragmentTask();
        clearAllDatabase();
        clearSharedPreferences();

        Intent intent = new Intent(this, SplashActivity.class);
        ActivityAnim anim = new ActivityAnim();
        startActivity(intent);
        anim.rightOut(this);
        finish();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            cancelAllFragmentTask();
            finish();
        }

        return true;
    }
    @Override
    public void onDestroy() {
        cancelAllFragmentTask();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation== Configuration.ORIENTATION_LANDSCAPE) {
            /* Do nothing */
        }
        else{
            /* Do nothing */
        }
    }
}
