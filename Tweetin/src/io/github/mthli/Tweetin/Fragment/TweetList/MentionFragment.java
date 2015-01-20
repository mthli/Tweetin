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
import io.github.mthli.Tweetin.Task.TweetList.MentionFirstTask;
import io.github.mthli.Tweetin.Task.TweetList.MentionMoreTask;
import io.github.mthli.Tweetin.Tweet.Tweet;
import io.github.mthli.Tweetin.Tweet.TweetAdapter;

import java.util.ArrayList;
import java.util.List;

public class MentionFragment extends ListFragment {
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

    private MentionFirstTask mentionFirstTask;
    private MentionMoreTask mentionMoreTask;

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

        mentionFirstTask = new MentionFirstTask(this, false);
        mentionFirstTask.execute();
    }

    private void initUI() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isSomeTasksRunning()) {
                    mentionFirstTask = new MentionFirstTask(MentionFragment.this, true);
                    mentionFirstTask.execute();
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

                if (totalItemCount > 7 && totalItemCount == firstVisibleItem + visibleItemCount && moveToBottom && !isSomeTasksRunning()) {
                    mentionMoreTask = new MentionMoreTask(MentionFragment.this);
                    mentionMoreTask.execute();
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
        if (mentionFirstTask != null && mentionFirstTask.getStatus() == AsyncTask.Status.RUNNING) {
            mentionFirstTask.cancel(true);
        }
        if (mentionMoreTask != null && mentionMoreTask.getStatus() == AsyncTask.Status.RUNNING) {
            mentionMoreTask.cancel(true);
        }
    }
}
