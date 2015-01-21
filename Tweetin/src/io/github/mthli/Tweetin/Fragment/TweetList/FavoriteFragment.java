package io.github.mthli.Tweetin.Fragment.TweetList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import io.github.mthli.Tweetin.Flag.FlagUnit;
import io.github.mthli.Tweetin.Fragment.Base.ListFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Task.TweetList.FavoriteFirstTask;
import io.github.mthli.Tweetin.Task.TweetList.FavoriteMoreTask;
import io.github.mthli.Tweetin.Tweet.Tweet;
import io.github.mthli.Tweetin.Tweet.TweetAdapter;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends ListFragment {
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

    private FavoriteFirstTask favoriteFirstTask;
    private FavoriteMoreTask favoriteMoreTask;

    private int loadTaskStatus = FlagUnit.TASK_IDLE;
    public void setLoadTaskStatus(int loadTaskStatus) {
        this.loadTaskStatus = loadTaskStatus;
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

        favoriteFirstTask = new FavoriteFirstTask(this, false);
        favoriteFirstTask.execute();
    }

    private void initUI() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isSomeLoadTasksRunning()) {
                    favoriteFirstTask = new FavoriteFirstTask(FavoriteFragment.this, true);
                    favoriteFirstTask.execute();
                }
            }
        });

        tweetAdapter = new TweetAdapter(this, R.layout.tweet, tweetList);
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

                if (totalItemCount > 7 && totalItemCount == firstVisibleItem + visibleItemCount && moveToBottom && !isSomeLoadTasksRunning()) {
                    favoriteMoreTask = new FavoriteMoreTask(FavoriteFragment.this);
                    favoriteMoreTask.execute();
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

    public boolean isSomeLoadTasksRunning() {
        return loadTaskStatus == FlagUnit.TASK_RUNNING;
    }

    public void cancelAllTasks() {
        if (favoriteFirstTask != null && favoriteFirstTask.getStatus() == AsyncTask.Status.RUNNING) {
            favoriteFirstTask.cancel(true);
        }
        if (favoriteMoreTask != null && favoriteMoreTask.getStatus() == AsyncTask.Status.RUNNING) {
            favoriteMoreTask.cancel(true);
        }
        cancelProfileTask();
    }
}
