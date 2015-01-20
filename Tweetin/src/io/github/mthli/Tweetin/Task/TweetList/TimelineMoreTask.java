package io.github.mthli.Tweetin.Task.TweetList;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;
import io.github.mthli.Tweetin.Flag.FlagUnit;
import io.github.mthli.Tweetin.Fragment.TweetList.TimelineFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.Tweet;
import io.github.mthli.Tweetin.Tweet.TweetAdapter;
import io.github.mthli.Tweetin.Tweet.TweetUnit;
import io.github.mthli.Tweetin.Twitter.TwitterUnit;
import twitter4j.Paging;
import twitter4j.TwitterException;

import java.util.ArrayList;
import java.util.List;

public class TimelineMoreTask extends AsyncTask<Void, Void, Boolean> {
    private TimelineFragment timelineFragment;
    private Context context;

    private SwipeRefreshLayout swipeRefreshLayout;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private TweetUnit tweetUnit;

    private List<twitter4j.Status> statusList;
    private int nextPage;

    private String error;

    public TimelineMoreTask(TimelineFragment timelineFragment) {
        this.timelineFragment = timelineFragment;
        this.context = timelineFragment.getContext();

        this.swipeRefreshLayout = timelineFragment.getSwipeRefreshLayout();

        this.tweetAdapter = timelineFragment.getTweetAdapter();
        this.tweetList = timelineFragment.getTweetList();
        this.tweetUnit = new TweetUnit(context);

        this.statusList = new ArrayList<twitter4j.Status>();
        this.nextPage = timelineFragment.getNextPage();

        this.error = context.getString(R.string.fragment_error_get_timeline_data_failed);
    }

    @Override
    protected void onPreExecute() {
        timelineFragment.setTaskStatus(FlagUnit.TASK_RUNNING);

        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (isCancelled()) {
            return false;
        }

        try {
            statusList = TwitterUnit.getTwitterFromSharedPreferences(context).getHomeTimeline(new Paging(nextPage, 40));
        } catch (TwitterException t) {
            error = t.getMessage();
            return false;
        }

        if (isCancelled()) {
            return false;
        }
        return true;
    }

    @Override
    protected void onCancelled() {}

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            for (twitter4j.Status status : statusList) {
                tweetList.add(tweetUnit.getTweetFromStatus(status));
            }
            tweetAdapter.notifyDataSetChanged();

            timelineFragment.setNextPage(++nextPage);
        } else {
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
        }

        swipeRefreshLayout.setRefreshing(false);
        timelineFragment.setTaskStatus(FlagUnit.TASK_IDLE);
    }
}
