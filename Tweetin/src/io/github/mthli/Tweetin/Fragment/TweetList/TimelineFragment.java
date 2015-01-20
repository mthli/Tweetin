package io.github.mthli.Tweetin.Fragment.TweetList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.*;
import io.github.mthli.Tweetin.Activity.PostActivity;
import io.github.mthli.Tweetin.Flag.FlagUnit;
import io.github.mthli.Tweetin.Fragment.Base.ListFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Task.TweetList.TimelineFirstTask;
import io.github.mthli.Tweetin.Task.TweetList.TimelineMoreTask;
import io.github.mthli.Tweetin.Tweet.Tweet;
import io.github.mthli.Tweetin.Tweet.TweetAdapter;

import java.util.ArrayList;
import java.util.List;

public class TimelineFragment extends ListFragment {
    private TweetAdapter tweetAdapter;
    public TweetAdapter getTweetAdapter() {
        return tweetAdapter;
    }

    private List<Tweet> tweetList = new ArrayList<Tweet>();
    public List<Tweet> getTweetList() {
        return tweetList;
    }

    private int previousPosition = 0;
    public void setPreviousPosition(int previousPosition) {
        this.previousPosition = previousPosition;
    }

    private TimelineFirstTask timelineFirstTask;
    private TimelineMoreTask timelineMoreTask;

    private int taskStatus = FlagUnit.TASK_IDLE;
    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    private int nextPage = 2;
    public int getNextPage() {
        return nextPage;
    }
    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initUI();

        timelineFirstTask = new TimelineFirstTask(this, false);
        timelineFirstTask.execute();
    }

    private Animator animator;
    private void showFab(boolean show) {
        if (animator != null && animator.isRunning()) {
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

    private void initUI() {
        fab.setVisibility(View.VISIBLE);
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

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isSomeTasksRunning()) {
                    timelineFirstTask = new TimelineFirstTask(TimelineFragment.this, true);
                    timelineFirstTask.execute();
                }
            }
        });

        tweetAdapter = new TweetAdapter(getActivity(), R.layout.tweet, tweetList);
        listView.setAdapter(tweetAdapter);
        tweetAdapter.notifyDataSetChanged();

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private boolean moveToBottom = false;
            private int previousFirst = 0;

            private int currentFirst = 0;
            private int currentCount = 0;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE && (previousPosition < currentFirst || previousPosition > currentFirst + currentCount)) {
                    tweetList.get(previousPosition).setDetail(false);
                    tweetAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (previousFirst < firstVisibleItem) {
                    moveToBottom = true;
                }
                if (previousFirst > firstVisibleItem) {
                    moveToBottom = false;
                }
                previousFirst = firstVisibleItem;
                currentFirst = firstVisibleItem;
                currentCount = visibleItemCount;

                showFab(!moveToBottom);

                if (totalItemCount > 7 && totalItemCount == firstVisibleItem + visibleItemCount && moveToBottom && !isSomeTasksRunning()) {
                    timelineMoreTask = new TimelineMoreTask(TimelineFragment.this);
                    timelineMoreTask.execute();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int currentPosition, long id) {
                if (previousPosition == currentPosition) {
                    tweetList.get(currentPosition).setDetail(!tweetList.get(currentPosition).isDetail());
                } else {
                    tweetList.get(previousPosition).setDetail(false);
                    tweetList.get(currentPosition).setDetail(true);
                }
                tweetAdapter.notifyDataSetChanged();

                previousPosition = currentPosition;
            }
        });
    }

    public boolean isSomeTasksRunning() {
        return taskStatus == FlagUnit.TASK_RUNNING;
    }

    public void cancelAllTasks() {
        if (timelineFirstTask != null && timelineFirstTask.getStatus() == AsyncTask.Status.RUNNING) {
            timelineFirstTask.cancel(true);
        }
        if (timelineMoreTask != null && timelineMoreTask.getStatus() == AsyncTask.Status.RUNNING) {
            timelineMoreTask.cancel(true);
        }
    }
}
