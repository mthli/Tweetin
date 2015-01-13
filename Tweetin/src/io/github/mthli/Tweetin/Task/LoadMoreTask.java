package io.github.mthli.Tweetin.Task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import io.github.mthli.Tweetin.Flag.FlagUnit;
import io.github.mthli.Tweetin.Fragment.BaseFragment;
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
    private BaseFragment baseFragment;
    private int fragmentFlag;
    private Context context;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private TweetUnit tweetUnit;

    private int nextPage;

    private SwipeRefreshLayout swipeRefreshLayout;

    public LoadMoreTask(BaseFragment baseFragment) {
        this.baseFragment = baseFragment;
    }

    @Override
    protected void onPreExecute() {
        if (baseFragment.getFragmentFlag() == FlagUnit.IN_SEARCH_FRAGMENT) {
            onCancelled();
        }

        if (baseFragment.getTaskStatus() == FlagUnit.TASK_RUNNING) {
            onCancelled();
        } else {
            baseFragment.setTaskStatus(FlagUnit.TASK_RUNNING);
        }

        fragmentFlag = baseFragment.getFragmentFlag();
        context = baseFragment.getActivity();

        tweetAdapter = baseFragment.getTweetAdapter();
        tweetList = baseFragment.getTweetList();
        tweetUnit = new TweetUnit(context);

        nextPage = baseFragment.getNextPage();

        swipeRefreshLayout = baseFragment.getSwipeRefreshLayout();
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
    }

    private List<twitter4j.Status> getStatusList() throws TwitterException {
        Twitter twitter = TwitterUnit.getTwitterFromSharedPreferences(context);

        Paging paging = new Paging(nextPage, 40);

        switch (fragmentFlag) {
            case FlagUnit.IN_TIMELINE_FRAGMENT:
                return twitter.getHomeTimeline(paging);
            case FlagUnit.IN_MENTION_FRAGMENT:
                return twitter.getMentionsTimeline(paging);
            case FlagUnit.IN_FAVORITE_FRAGMENT:
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

        baseFragment.setNextPage(++nextPage);
        baseFragment.setTaskStatus(FlagUnit.TASK_IDLE);
    }
}
