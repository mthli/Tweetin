package io.github.mthli.Tweetin.Main;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;
import io.github.mthli.Tweetin.Discovery.DiscoveryFragment;
import io.github.mthli.Tweetin.Favorite.FavoriteFragment;
import io.github.mthli.Tweetin.Mention.MentionFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Setting.SettingFragment;
import io.github.mthli.Tweetin.Timeline.TimelineFragment;
import io.github.mthli.Tweetin.Unit.Flag.Flag;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {
    private TimelineFragment timelineFragment;
    private MentionFragment mentionFragment;
    private FavoriteFragment favoriteFragment;
    private DiscoveryFragment discoveryFragment;
    private SettingFragment settingFragment;
    private int fragmentFlag = Flag.IN_TIMELINE_FRAGMENT;
    public int getFragmentFlag() {
        return fragmentFlag;
    }
    public void setFragmentFlag(int fragmentFlag) {
        this.fragmentFlag = fragmentFlag;
    }

    private ResideMenu resideMenu;
    private ResideMenuItem timelineItem;
    private ResideMenuItem mentionItem;
    private ResideMenuItem favoriteItem;
    private ResideMenuItem discoveryItem;
    private ResideMenuItem settingItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initResideMenu();

        /* Do something */
        timelineFragment = new TimelineFragment();
        mentionFragment = new MentionFragment();

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
            /* Do something */
        }
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

    /* Do something */
    private Fragment getCurrentFragment() {
        switch (fragmentFlag) {
            case Flag.IN_TIMELINE_FRAGMENT:
                return timelineFragment;
            case Flag.IN_MENTION_FRAGMENT:
                return mentionFragment;
            case Flag.IN_FAVORITE_FRAGMENT:
                return favoriteFragment;
            case Flag.IN_DISCOVERY_FRAGMENT:
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
                R.drawable.ic_action_timeline,
                R.string.reside_menu_item_timeline
        );
        mentionItem = new ResideMenuItem(
                this,
                R.drawable.ic_action_mention,
                R.string.reside_menu_item_mention
        );
        favoriteItem = new ResideMenuItem(
                this,
                R.drawable.ic_action_favorite,
                R.string.reside_menu_item_favorite
        );
        discoveryItem = new ResideMenuItem(
                this,
                R.drawable.ic_action_discovery,
                R.string.reside_menu_item_discovery
        );
        settingItem = new ResideMenuItem(
                this,
                R.drawable.ic_action_setting,
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
                    /* Do something */
                    resideMenu.closeMenu();
                } else {
                    resideMenu.closeMenu();
                }
            }
        });
        discoveryItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragmentFlag != Flag.IN_DISCOVERY_FRAGMENT) {
                    /* Do something */
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
                    /* Do something */
                    resideMenu.closeMenu();
                } else {
                    resideMenu.closeMenu();
                }
            }
        });
    }
}
