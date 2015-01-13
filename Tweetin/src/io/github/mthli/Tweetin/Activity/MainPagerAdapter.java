package io.github.mthli.Tweetin.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import io.github.mthli.Tweetin.Flag.FlagUnit;
import io.github.mthli.Tweetin.Fragment.BaseFragment;
import io.github.mthli.Tweetin.R;

public class MainPagerAdapter extends FragmentPagerAdapter {
    private MainActivity mainActivity;

    private String[] titles;

    private SparseArray<BaseFragment> sparseArray;

    public MainPagerAdapter(
            MainActivity mainActivity
    ) {
        super(mainActivity.getSupportFragmentManager());

        this.mainActivity = mainActivity;

        this.titles = mainActivity.getResources().getStringArray(R.array.tabs);

        this.sparseArray = new SparseArray<BaseFragment>();
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
        Bundle bundle = new Bundle();
        switch (position) {
            case FlagUnit.IN_TIMELINE_FRAGMENT:
                bundle.putInt(mainActivity.getString(R.string.bundle_fragment_flag), FlagUnit.IN_TIMELINE_FRAGMENT);
                break;
            case FlagUnit.IN_MENTION_FRAGMENT:
                bundle.putInt(mainActivity.getString(R.string.bundle_fragment_flag), FlagUnit.IN_MENTION_FRAGMENT);
                break;
            case FlagUnit.IN_FAVORITE_FRAGMENT:
                bundle.putInt(mainActivity.getString(R.string.bundle_fragment_flag), FlagUnit.IN_FAVORITE_FRAGMENT);
                break;
            default:
                bundle.putInt(mainActivity.getString(R.string.bundle_fragment_flag), FlagUnit.IN_TIMELINE_FRAGMENT);
                break;
        }

        BaseFragment baseFragment = new BaseFragment();
        baseFragment.setArguments(bundle);

        sparseArray.put(position, baseFragment);

        return baseFragment;
    }

    public BaseFragment getFragmentFromPosition(int position) {
        return sparseArray.get(position);
    }
}
