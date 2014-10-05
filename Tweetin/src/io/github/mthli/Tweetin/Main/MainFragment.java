package io.github.mthli.Tweetin.Main;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import com.devspark.progressfragment.ProgressFragment;
import com.melnykov.fab.FloatingActionButton;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.Tweet;
import io.github.mthli.Tweetin.Tweet.TweetAdapter;
import io.github.mthli.Tweetin.Tweet.TweetInitTask;
import io.github.mthli.Tweetin.Tweet.TweetMoreTask;
import io.github.mthli.Tweetin.Unit.TaskFlag;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends ProgressFragment {
    private View view;

    private FloatingActionButton fab;
    private SwipeRefreshLayout srl;
    public SwipeRefreshLayout getSrl() {
        return srl;
    }

    private ListView listView;
    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList = new ArrayList<Tweet>();
    public TweetAdapter getTweetAdapter() {
        return tweetAdapter;
    }
    public List<Tweet> getTweetList() {
        return tweetList;
    }

    private TweetInitTask tweetInitTask;
    private TweetMoreTask tweetMoreTask;
    private int taskFlag = TaskFlag.TWEET_TASK_DIED;
    public int getTaskFlag() {
        return taskFlag;
    }
    public void setTaskFlag(int taskFlag) {
        this.taskFlag = taskFlag;
    }

    public boolean isSomeTaskAlive() {
        if ((tweetInitTask != null && tweetInitTask.getStatus() == AsyncTask.Status.RUNNING)
                || (tweetMoreTask != null && tweetMoreTask.getStatus() == AsyncTask.Status.RUNNING)) {
            return true;
        }
        return false;
    }

    public void allTaskDown() {
        if (tweetInitTask != null && tweetInitTask.getStatus() == AsyncTask.Status.RUNNING) {
            tweetInitTask.cancel(true);
        }
        if (tweetMoreTask != null && tweetMoreTask.getStatus() == AsyncTask.Status.RUNNING) {
            tweetMoreTask.cancel(true);
        }
    }

    private boolean isMoveToButton = false;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.main_fragment);
        view = getContentView();
        setContentEmpty(false);
        setContentShown(true);

        listView = (ListView) view.findViewById(R.id.main_fragment_timeline);
        tweetAdapter = new TweetAdapter(
                view.getContext(),
                R.layout.tweet,
                tweetList
        );
        listView.setAdapter(tweetAdapter);
        tweetAdapter.notifyDataSetChanged();

        fab = (FloatingActionButton) view.findViewById(
                R.id.button_floating_action
        );
        fab.attachToListView(listView);
        fab.show();

        srl = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        srl.setColorSchemeResources(
                R.color.tumblr_ptr_red,
                R.color.tumblr_ptr_yellow,
                R.color.tumblr_ptr_blue,
                R.color.tumblr_ptr_green
        );
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tweetInitTask = new TweetInitTask(MainFragment.this, true);
                tweetInitTask.execute();
            }
        });

        tweetInitTask = new TweetInitTask(MainFragment.this, false);
        tweetInitTask.execute();

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int previous = 0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    /* Do nothing */
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (previous < firstVisibleItem) {
                    isMoveToButton = true;
                    fab.hide();
                }
                if (previous > firstVisibleItem) {
                    isMoveToButton = false;
                    fab.show();
                }
                previous = firstVisibleItem;

                if (totalItemCount == firstVisibleItem + visibleItemCount) {
                    if (!isSomeTaskAlive() && isMoveToButton) {
                        tweetMoreTask = new TweetMoreTask(MainFragment.this);
                        tweetMoreTask.execute();
                    }
                }
            }
        });
    }
}
