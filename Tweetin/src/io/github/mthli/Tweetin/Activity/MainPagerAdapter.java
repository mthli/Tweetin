package io.github.mthli.Tweetin.Activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import io.github.mthli.Tweetin.Flag.FlagUnit;
import io.github.mthli.Tweetin.Fragment.Base.ListFragment;
import io.github.mthli.Tweetin.Fragment.TweetList.FavoriteFragment;
import io.github.mthli.Tweetin.Fragment.TweetList.MentionFragment;
import io.github.mthli.Tweetin.Fragment.TweetList.TimelineFragment;
import io.github.mthli.Tweetin.R;

public class MainPagerAdapter extends FragmentPagerAdapter {
    private String[] titles;

    private SparseArray<Fragment> sparseArray;

    public MainPagerAdapter(MainActivity mainActivity) {
        super(mainActivity.getSupportFragmentManager());

        this.titles = mainActivity.getResources().getStringArray(R.array.tabs);

        this.sparseArray = new SparseArray<Fragment>();
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;

        switch (position) {
            case FlagUnit.IN_TIMELINE_FRAGMENT:
                fragment = new TimelineFragment();
                break;
            case FlagUnit.IN_MENTION_FRAGMENT:
                fragment = new MentionFragment();
                break;
            case FlagUnit.IN_FAVORITE_FRAGMENT:
                fragment = new FavoriteFragment();
                break;
            default:
                fragment = new ListFragment();
                break;
        }

        sparseArray.put(position, fragment);

        return fragment;
    }

    public Fragment getFragmentFromPosition(int position) {
        return sparseArray.get(position);
    }
}
