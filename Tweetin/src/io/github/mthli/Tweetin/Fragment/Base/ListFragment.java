package io.github.mthli.Tweetin.Fragment.Base;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import com.devspark.progressfragment.ProgressFragment;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Task.Profile.ProfileTask;
import io.github.mthli.Tweetin.View.ViewUnit;

public class ListFragment extends ProgressFragment {
    protected Context context;
    public Context getContext() {
        return context;
    }

    protected SlidingUpPanelLayout slidingUpPanelLayout;
    public SlidingUpPanelLayout getSlidingUpPanelLayout() {
        return slidingUpPanelLayout;
    }

    protected SwipeRefreshLayout swipeRefreshLayout;
    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    protected ListView listView;
    public ListView getListView() {
        return listView;
    }

    protected ImageButton fab;
    public ImageButton getFab() {
        return fab;
    }

    protected View profile;
    public View getProfile() {
        return profile;
    }

    private ProfileTask profileTask;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.list_fragment);

        setContentEmpty(false);
        setContentShownNoAnimation(true);

        context = getActivity();

        initUI();
    }

    private void initUI() {
        slidingUpPanelLayout = (SlidingUpPanelLayout) getContentView().findViewById(R.id.list_fragment_sliding_layout);
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        slidingUpPanelLayout.setShadowHeight(0);

        profile = LayoutInflater.from(context).inflate(R.layout.profile, null, false);

        FrameLayout drag = (FrameLayout) getContentView().findViewById(R.id.list_fragment_drag);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        drag.addView(profile, layoutParams);

        swipeRefreshLayout = (SwipeRefreshLayout) getContentView().findViewById(R.id.list_fragment_swipe_container);
        swipeRefreshLayout.setProgressViewOffset(false, 0, ViewUnit.getToolbarHeight(getActivity()));
        ViewUnit.setSwipeRefreshLayoutTheme(getActivity(), swipeRefreshLayout);

        listView = (ListView) getContentView().findViewById(R.id.list_fragment_listview);

        fab = (ImageButton) getContentView().findViewById(R.id.list_fragment_fab);
        ViewCompat.setElevation(fab, ViewUnit.getElevation(getActivity(), 2));
        fab.setVisibility(View.GONE);

        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {}

            @Override
            public void onPanelCollapsed(View panel) {
                if (profileTask != null && profileTask.getStatus() == AsyncTask.Status.RUNNING) {
                    profileTask.cancel(true);
                }
                slidingUpPanelLayout.setShadowHeight(0);
            }

            @Override
            public void onPanelExpanded(View panel) {}

            @Override
            public void onPanelAnchored(View panel) {}

            @Override
            public void onPanelHidden(View panel) {
                if (profileTask != null && profileTask.getStatus() == AsyncTask.Status.RUNNING) {
                    profileTask.cancel(true);
                }
                slidingUpPanelLayout.setShadowHeight(0);
            }
        });
    }

    public void showProfile(String screenName) {
        if (screenName == null) {
            return;
        }

        cancelProfileTask();

        profile.findViewById(R.id.profile_progress_bar).setVisibility(View.VISIBLE);
        profile.findViewById(R.id.profile_all).setVisibility(View.GONE);

        slidingUpPanelLayout.setShadowHeight((int) ViewUnit.getElevation(context, 2));
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);

        profileTask = new ProfileTask(getActivity(), profile, screenName);
        profileTask.execute();
    }

    protected void cancelProfileTask() {
        if (profileTask != null && profileTask.getStatus() == AsyncTask.Status.RUNNING) {
            profileTask.cancel(true);
        }
    }
}
