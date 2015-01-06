package io.github.mthli.Tweetin.Task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import io.github.mthli.Tweetin.Flag.Flag;
import io.github.mthli.Tweetin.Fragment.MainFragment;
import io.github.mthli.Tweetin.Tweet.Tweet;
import io.github.mthli.Tweetin.Tweet.TweetAdapter;
import io.github.mthli.Tweetin.Tweet.TweetUnit;
import io.github.mthli.Tweetin.Twitter.TwitterUnit;
import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.ArrayList;
import java.util.List;

public class LoadMoreTask extends AsyncTask<Void, Integer, Boolean> {
    private MainFragment mainFragment;
    private int fragmentFlag;
    private Context context;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private TweetUnit tweetUnit;

    private SwipeRefreshLayout swipeRefreshLayout;

    public LoadMoreTask(MainFragment mainFragment) {
        this.mainFragment = mainFragment;
    }

    @Override
    protected void onPreExecute() {
        if (mainFragment.getTaskStatus() == Flag.TASK_RUNNING) {
            onCancelled();
        } else {
            mainFragment.setTaskStatus(Flag.TASK_RUNNING);
        }

        fragmentFlag = mainFragment.getFragmentFlag();
        context = mainFragment.getActivity();

        tweetAdapter = mainFragment.getTweetAdapter();
        tweetList = mainFragment.getTweetList();
        tweetUnit = new TweetUnit(context);

        swipeRefreshLayout = mainFragment.getSwipeRefreshLayout();
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
    }

    private List<twitter4j.Status> getStatusList() throws TwitterException {
        Twitter twitter = TwitterUnit.getTwitterFromSharedPreferences(context);

        int nextPage = tweetList.size() / 40 + 1;
        Paging paging = new Paging(nextPage, 40);

        switch (fragmentFlag) {
            case Flag.IN_TIMELINE_FRAGMENT:
                return twitter.getHomeTimeline(paging);
            case Flag.IN_MENTION_FRAGMENT:
                return twitter.getMentionsTimeline(paging);
            case Flag.IN_FAVORITE_FRAGMENT:
                return twitter.getFavorites(paging);
            default:
                return new ArrayList<twitter4j.Status>();
        }
    }

    private List<twitter4j.Status> statusList;

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            statusList = getStatusList();
        } catch (Exception e) {
            return false;
        }

        if (isCancelled()) {
            return false;
        }
        return true;
    }

    @Override
    protected void onCancelled() {
        /* Do nothing */
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        /* Do nothing */
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            for (twitter4j.Status status : statusList) {
                tweetList.add(tweetUnit.getTweetFromStatus(status));
            }
            tweetAdapter.notifyDataSetChanged();
        }

        swipeRefreshLayout.setRefreshing(false);

        mainFragment.setTaskStatus(Flag.TASK_IDLE);
    }
}
