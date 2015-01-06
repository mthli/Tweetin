package io.github.mthli.Tweetin.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;
import com.devspark.progressfragment.ProgressFragment;
import io.github.mthli.Tweetin.Custom.ViewUnit;
import io.github.mthli.Tweetin.Flag.Flag;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Task.*;
import io.github.mthli.Tweetin.Tweet.Tweet;
import io.github.mthli.Tweetin.Tweet.TweetAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends ProgressFragment {

    private int fragmentFlag;
    public int getFragmentFlag() {
        return fragmentFlag;
    }

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private View contentView;

    private SwipeRefreshLayout swipeRefreshLayout;
    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    private TweetAdapter tweetAdapter;
    public TweetAdapter getTweetAdapter() {
        return tweetAdapter;
    }

    private List<Tweet> tweetList = new ArrayList<Tweet>();
    public List<Tweet> getTweetList() {
        return tweetList;
    }

    private InitializeTask initializeTask;
    private LoadMoreTask loadMoreTask;
    private RetweetTask retweetTask;
    private FavoriteTask favoriteTask;
    private DeleteTask deleteTask;
    private RemoveTask removeTask;

    /* Do something */
    private int taskStatus;
    public int getTaskStatus() {
        return taskStatus;
    }
    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    private void setSwipeRefreshLayoutTheme() {
        int spColorValue = sharedPreferences.getInt(
                getString(R.string.sp_color),
                0
        );

        switch (spColorValue) {
            case Flag.COLOR_BLUE:
                swipeRefreshLayout.setColorSchemeResources(
                        R.color.blue_700,
                        R.color.blue_500,
                        R.color.blue_700,
                        R.color.blue_500
                );
                break;
            case Flag.COLOR_ORANGE:
                swipeRefreshLayout.setColorSchemeResources(
                        R.color.orange_700,
                        R.color.orange_500,
                        R.color.orange_700,
                        R.color.orange_500
                );
                break;
            case Flag.COLOR_PINK:
                swipeRefreshLayout.setColorSchemeResources(
                        R.color.pink_700,
                        R.color.pink_500,
                        R.color.pink_700,
                        R.color.pink_500
                );
                break;
            case Flag.COLOR_PURPLE:
                swipeRefreshLayout.setColorSchemeResources(
                        R.color.purple_700,
                        R.color.purple_500,
                        R.color.purple_700,
                        R.color.purple_500
                );
                break;
            case Flag.COLOR_TEAL:
                swipeRefreshLayout.setColorSchemeResources(
                        R.color.teal_700,
                        R.color.teal_500,
                        R.color.teal_700,
                        R.color.teal_500
                );
                break;
            default:
                swipeRefreshLayout.setColorSchemeResources(
                        R.color.blue_700,
                        R.color.blue_500,
                        R.color.blue_700,
                        R.color.blue_500
                );
                break;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        fragmentFlag = getArguments().getInt(
                getString(R.string.bundle_fragment_flag),
                Flag.IN_TIMELINE_FRAGMENT
        );

        sharedPreferences = getActivity().getSharedPreferences(
                getString(R.string.sp_tweetin),
                Context.MODE_PRIVATE
        );
        editor = sharedPreferences.edit();

        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.main_fragment);
        contentView = getContentView();
        setContentEmpty(false);
        setContentShown(true);

        initUI();

        initializeTask = new InitializeTask(this, false);
        initializeTask.execute();
    }

    private void initUI() {
        swipeRefreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.main_fragment_swipe_container);
        swipeRefreshLayout.setProgressViewOffset(false, 0, ViewUnit.getTabHeight(getActivity()));
        setSwipeRefreshLayoutTheme();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initializeTask = new InitializeTask(MainFragment.this, true);
                initializeTask.execute();
            }
        });

        ListView listView = (ListView) contentView.findViewById(R.id.main_fragment_listview);

        /* Do something with detail true or false */
        tweetAdapter = new TweetAdapter(
                getActivity(),
                R.layout.tweet,
                tweetList,
                false
        );
        listView.setAdapter(tweetAdapter);
        tweetAdapter.notifyDataSetChanged();

        /* Do something with *Listener */
    }
}
