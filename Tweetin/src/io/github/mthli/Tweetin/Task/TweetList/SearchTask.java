package io.github.mthli.Tweetin.Task.TweetList;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;
import io.github.mthli.Tweetin.Flag.FlagUnit;
import io.github.mthli.Tweetin.Fragment.TweetList.SearchFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.Tweet;
import io.github.mthli.Tweetin.Tweet.TweetAdapter;
import io.github.mthli.Tweetin.Tweet.TweetUnit;
import io.github.mthli.Tweetin.Twitter.TwitterUnit;
import twitter4j.Query;
import twitter4j.TwitterException;

import java.util.ArrayList;
import java.util.List;

public class SearchTask extends AsyncTask<Void, Void, Boolean> {
    private SearchFragment searchFragment;
    private Context context;

    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean swipeRefresh;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private List<twitter4j.Status> statusList;
    private TweetUnit tweetUnit;

    private String error;

    public SearchTask(SearchFragment searchFragment, boolean swipeRefresh) {
        this.searchFragment = searchFragment;
        this.context = searchFragment.getContext();

        this.swipeRefreshLayout = searchFragment.getSwipeRefreshLayout();
        this.swipeRefresh = swipeRefresh;

        this.tweetAdapter = searchFragment.getTweetAdapter();
        this.tweetList = searchFragment.getTweetList();
        this.statusList = new ArrayList<twitter4j.Status>();
        this.tweetUnit = new TweetUnit(searchFragment.getActivity());

        this.error = context.getString(R.string.fragment_error_search_failed);
    }

    @Override
    protected void onPreExecute() {
        searchFragment.setLoadTaskStatus(FlagUnit.TASK_RUNNING);

        if (TwitterUnit.getUseScreenNameFromSharedPreferences(context) == null) {
            searchFragment.setContentEmpty(true);
            searchFragment.setEmptyText(R.string.fragment_error_get_authorization_failed);
            searchFragment.setContentShown(false);

            cancel(true);
            return;
        }

        searchFragment.setPreviousPosition(0);

        if (!swipeRefresh) {
            searchFragment.setContentShown(false);
        } else if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (isCancelled()) {
            return false;
        }

        try {
            Query query = new Query(searchFragment.getKeyWord());
            query.setCount(100);
            statusList = TwitterUnit.getTwitterFromSharedPreferences(context).search(query).getTweets();
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
            tweetList.clear();
            for (twitter4j.Status status : statusList) {
                tweetList.add(tweetUnit.getTweetFromStatus(status));
            }

            if (tweetList.size() <= 0) {
                searchFragment.setContentEmpty(true);
                searchFragment.setEmptyText(R.string.fragment_list_empty);
                searchFragment.setContentShown(true);
                searchFragment.setLoadTaskStatus(FlagUnit.TASK_IDLE);
                return;
            }

            if (!swipeRefresh) {
                searchFragment.setContentEmpty(false);
                tweetAdapter.notifyDataSetChanged();
                searchFragment.setContentShown(true);
            } else {
                swipeRefreshLayout.setRefreshing(false);
                tweetAdapter.notifyDataSetChanged();
            }
        } else {
            if (!swipeRefresh) {
                searchFragment.setContentEmpty(true);
                searchFragment.setEmptyText(error);
                searchFragment.setContentShown(true);
            } else {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        }

        searchFragment.setLoadTaskStatus(FlagUnit.TASK_IDLE);
    }
}
