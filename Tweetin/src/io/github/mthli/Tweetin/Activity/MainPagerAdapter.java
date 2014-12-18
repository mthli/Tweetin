package io.github.mthli.Tweetin.Activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private static final int PAGES = 3;

    private MainActivity mainActivity;

    public MainPagerAdapter(
            MainActivity mainActivity
    ) {
        super(mainActivity.getSupportFragmentManager());

        this.mainActivity = mainActivity;
    }

    @Override
    public int getCount() {
        return PAGES;
    }

    @Override
    public Fragment getItem(int position) {
        return mainActivity.getFragmentFromPosition(position);
    }
}
