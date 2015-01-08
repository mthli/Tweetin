package io.github.mthli.Tweetin.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
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

    private ImageButton fab;
    private Animator animator;

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
    private int taskStatus; //
    public int getTaskStatus() {
        return taskStatus;
    }
    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }
    public boolean isSomeTaskRunning() {
        if (taskStatus == Flag.TASK_RUNNING) {
            return true;
        }

        return false;
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

    private void showFAB(boolean show) {
        if ((fragmentFlag != Flag.IN_TIMELINE_FRAGMENT) || (animator != null && animator.isRunning())) {
            return;
        }

        if (show && fab.getVisibility() == View.INVISIBLE) {
            animator = ViewAnimationUtils.createCircularReveal(
                    fab,
                    (int) fab.getPivotX(),
                    (int) fab.getPivotY(),
                    0,
                    fab.getWidth()
            );

            fab.setVisibility(View.VISIBLE);
            animator.start();
        } else if (!show && fab.getVisibility() == View.VISIBLE) {
            animator = ViewAnimationUtils.createCircularReveal(
                    fab,
                    (int) fab.getPivotX(),
                    (int) fab.getPivotY(),
                    fab.getWidth(),
                    0
            );

            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    fab.setVisibility(View.INVISIBLE);
                }
            });

            animator.start();
        }
    }

    private int detailPosition = 0;

    private void initUI() {
        swipeRefreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.main_fragment_swipe_container);
        swipeRefreshLayout.setProgressViewOffset(false, 0, ViewUnit.getToolbarHeight(getActivity()));
        setSwipeRefreshLayoutTheme();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initializeTask = new InitializeTask(MainFragment.this, true);
                initializeTask.execute();
            }
        });

        fab = (ImageButton) contentView.findViewById(R.id.main_fragment_fab);
        if (fragmentFlag == Flag.IN_TIMELINE_FRAGMENT) {
            fab.setVisibility(View.VISIBLE);
            ViewCompat.setElevation(fab, ViewUnit.getElevation(getActivity(), 2));
        }

        ListView listView = (ListView) contentView.findViewById(R.id.main_fragment_listview);

        tweetAdapter = new TweetAdapter(
                getActivity(),
                R.layout.tweet,
                tweetList
        );
        listView.setAdapter(tweetAdapter);
        tweetAdapter.notifyDataSetChanged();

        /* Do something with *Listener */

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private boolean moveToBottom = false;
            private int previous = 0;

            private int first = 0;
            private int count = 0;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE && (detailPosition < first || detailPosition > first + count)) {
                    tweetList.get(detailPosition).setDetail(false);
                    tweetAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (previous < firstVisibleItem) {
                    moveToBottom = true;
                }
                if (previous > firstVisibleItem) { //
                    moveToBottom = false;
                }
                previous = firstVisibleItem;

                first = firstVisibleItem;
                count = visibleItemCount;

                showFAB(!moveToBottom);

                if (totalItemCount > 7 && totalItemCount == firstVisibleItem + visibleItemCount && !isSomeTaskRunning() && moveToBottom) {
                    loadMoreTask = new LoadMoreTask(MainFragment.this);
                    loadMoreTask.execute();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (tweetList.get(detailPosition).isDetail() && position != detailPosition) {
                    tweetList.get(detailPosition).setDetail(false);
                    tweetAdapter.notifyDataSetChanged();

                    detailPosition = position;

                    tweetList.get(position).setDetail(true);
                    tweetAdapter.notifyDataSetChanged();
                } else if (!tweetList.get(position).isDetail()) {
                    detailPosition = position;

                    tweetList.get(position).setDetail(true);
                    tweetAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
