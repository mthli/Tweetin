package io.github.mthli.Tweetin.Main;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;
import com.devspark.progressfragment.ProgressFragment;
import com.melnykov.fab.FloatingActionButton;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.Tweet;
import io.github.mthli.Tweetin.Tweet.TweetAdapter;
import io.github.mthli.Tweetin.Tweet.TweetLoadTask;
import io.github.mthli.Tweetin.Tweet.TweetUpToRefreshTask;

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

    private TweetLoadTask tweetLoadTask;
    private TweetUpToRefreshTask tweetUpToRefreshTask;
    private int taskFlag = Flag.TWEET_TASK_DIED;
    public int getTaskFlag() {
        return taskFlag;
    }
    public void setTaskFlag(int taskFlag) {
        this.taskFlag = taskFlag;
    }

    public boolean isSomeTaskAlive() {
        if (
                (tweetLoadTask != null && tweetLoadTask.getStatus() == AsyncTask.Status.RUNNING) ||
                        (tweetUpToRefreshTask != null && tweetUpToRefreshTask.getStatus() == AsyncTask.Status.RUNNING)
                ) {
            return true;
        }
        return false;
    }

    public void allTaskDown() {
        if (tweetLoadTask != null && tweetLoadTask.getStatus() == AsyncTask.Status.RUNNING) {
            tweetLoadTask.cancel(true);
        }
        if (tweetUpToRefreshTask != null && tweetUpToRefreshTask.getStatus() == AsyncTask.Status.RUNNING) {
            tweetUpToRefreshTask.cancel(true);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.main_fragment);
        view = getContentView();
        setContentEmpty(false);
        setContentShown(true);

        listView = (ListView) view.findViewById(R.id.main_fragment_timeline);

        fab = (FloatingActionButton) view.findViewById(
                R.id.button_floating_action
        );
        fab.attachToListView(listView);
        fab.show();

        srl = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        srl.setColorSchemeResources(
                R.color.pink_500,
                R.color.light_blue_a400,
                R.color.indigo_700,
                R.color.indigo_300
        );
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tweetLoadTask = new TweetLoadTask(MainFragment.this, true);
                tweetLoadTask.execute();
            }
        });

        tweetAdapter = new TweetAdapter(
                view.getContext(),
                R.layout.tweet,
                tweetList
        );
        listView.setAdapter(tweetAdapter);
        tweetAdapter.notifyDataSetChanged();

        /* Do something */
        tweetLoadTask = new TweetLoadTask(MainFragment.this, false);
        tweetLoadTask.execute();
    }
}
