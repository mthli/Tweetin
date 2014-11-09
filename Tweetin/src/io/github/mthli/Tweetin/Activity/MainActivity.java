package io.github.mthli.Tweetin.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;
import io.github.mthli.Tweetin.Fragment.DiscoveryFragment;
import io.github.mthli.Tweetin.Fragment.FavoriteFragment;
import io.github.mthli.Tweetin.Fragment.MentionFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Fragment.SettingFragment;
import io.github.mthli.Tweetin.Fragment.TimelineFragment;
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
