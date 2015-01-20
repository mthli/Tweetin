package io.github.mthli.Tweetin.Fragment.Base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import com.devspark.progressfragment.ProgressFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.View.ViewUnit;

public class ListFragment extends ProgressFragment {
    protected Context context;
    public Context getContext() {
        return context;
    }

    protected SwipeRefreshLayout swipeRefreshLayout;
    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    protected ImageButton fab;
    public ImageButton getFab() {
        return fab;
    }

    protected ListView listView;
    public ListView getListView() {
        return listView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.list_fragment);

        setContentEmpty(false);
        setContentShown(true);

        context = getActivity();

        initUI();
    }

    private void initUI() {
        swipeRefreshLayout = (SwipeRefreshLayout) getContentView().findViewById(R.id.list_fragment_swipe_container);
        swipeRefreshLayout.setProgressViewOffset(false, 0, ViewUnit.getToolbarHeight(getActivity()));
        ViewUnit.setSwipeRefreshLayoutTheme(getActivity(), swipeRefreshLayout);

        fab = (ImageButton) getContentView().findViewById(R.id.list_fragment_fab);
        ViewCompat.setElevation(fab, ViewUnit.getElevation(getActivity(), 2));
        fab.setVisibility(View.GONE);

        listView = (ListView) getContentView().findViewById(R.id.list_fragment_listview);
    }
}
