package io.github.mthli.Tweetin.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.*;
import com.devspark.progressfragment.ProgressFragment;
import io.github.mthli.Tweetin.Activity.PostActivity;
import io.github.mthli.Tweetin.View.ViewUnit;
import io.github.mthli.Tweetin.Flag.FlagUnit;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Task.*;
import io.github.mthli.Tweetin.Tweet.Tweet;
import io.github.mthli.Tweetin.Tweet.TweetAdapter;

import java.util.ArrayList;
import java.util.List;

public class BaseFragment extends ProgressFragment {
    private int fragmentFlag;
    public int getFragmentFlag() {
        return fragmentFlag;
    }

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

    private LoadFirstTask loadFirstTask;
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
        if (taskStatus == FlagUnit.TASK_RUNNING) {
            return true;
        }

        return false;
    }
    public void cancelAllTask() {
        /* Do something */
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        fragmentFlag = getArguments().getInt(
                getString(R.string.bundle_fragment_flag),
                FlagUnit.IN_TIMELINE_FRAGMENT
        );

        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.main_fragment);
        contentView = getContentView();
        setContentEmpty(false);
        setContentShown(true);

        initUI();

        loadFirstTask = new LoadFirstTask(this, false);
        loadFirstTask.execute();
    }

    private void showFAB(boolean show) {
        if ((fragmentFlag != FlagUnit.IN_TIMELINE_FRAGMENT) || (animator != null && animator.isRunning())) {
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
        ViewUnit.setSwipeRefreshLayoutTheme(getActivity(), swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFirstTask = new LoadFirstTask(BaseFragment.this, true);
                loadFirstTask.execute();
            }
        });

        fab = (ImageButton) contentView.findViewById(R.id.main_fragment_fab);
        if (fragmentFlag == FlagUnit.IN_TIMELINE_FRAGMENT) {
            fab.setVisibility(View.VISIBLE);
            ViewCompat.setElevation(fab, ViewUnit.getElevation(getActivity(), 2));

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), PostActivity.class);
                    intent.putExtra(getString(R.string.post_intent_post_flag), FlagUnit.POST_NEW);
                    startActivity(intent);
                }
            });
            fab.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Toast.makeText(getActivity(), R.string.fragment_toast_post_a_new_tweet, Toast.LENGTH_SHORT).show();

                    return true;
                }
            });
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
                    loadMoreTask = new LoadMoreTask(BaseFragment.this);
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
