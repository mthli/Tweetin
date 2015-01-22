package io.github.mthli.Tweetin.Task.TweetList;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;
import io.github.mthli.Tweetin.Flag.FlagUnit;
import io.github.mthli.Tweetin.Fragment.TweetList.InReplyToFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.Tweet;
import io.github.mthli.Tweetin.Tweet.TweetAdapter;
import io.github.mthli.Tweetin.Tweet.TweetUnit;
import io.github.mthli.Tweetin.Twitter.TwitterUnit;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InReplyToTask extends AsyncTask<Void, Void, Boolean> {
    private InReplyToFragment inReplyToFragment;
    private Context context;

    private SwipeRefreshLayout swipeRefreshLayout;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private List<twitter4j.Status> statusList;
    private TweetUnit tweetUnit;

    private Twitter twitter;
    private Tweet currentTweet;

    private String error;

    public InReplyToTask(InReplyToFragment inReplyToFragment) {
        this.inReplyToFragment = inReplyToFragment;
        this.context = inReplyToFragment.getContext();

        this.swipeRefreshLayout = inReplyToFragment.getSwipeRefreshLayout();

        this.tweetAdapter = inReplyToFragment.getTweetAdapter();
        this.tweetList = inReplyToFragment.getTweetList();
        this.statusList = new ArrayList<twitter4j.Status>();
        this.tweetUnit = new TweetUnit(inReplyToFragment.getActivity());

        this.twitter = TwitterUnit.getTwitterFromSharedPreferences(context);
        this.currentTweet = inReplyToFragment.getCurrentTweet();

        this.error = context.getString(R.string.in_reply_to_error_get_tweets_failed);
    }

    @Override
    protected void onPreExecute() {
        inReplyToFragment.setLoadTaskStatus(FlagUnit.TASK_RUNNING);

        if (TwitterUnit.getUseScreenNameFromSharedPreferences(context) == null) {
            inReplyToFragment.setContentEmpty(true);
            inReplyToFragment.setEmptyText(R.string.fragment_error_get_authorization_failed);
            inReplyToFragment.setContentShown(false);

            cancel(true);
            return;
        }

        inReplyToFragment.setPreviousPosition(0);

        swipeRefreshLayout.setRefreshing(true);
    }

    private List<twitter4j.Status> getInReplyToStatusList(long inReplyToStatusId) throws TwitterException {
        List<twitter4j.Status> inReplyToStatusList = new ArrayList<twitter4j.Status>();

        while (inReplyToStatusId > 0l) {
            twitter4j.Status status = twitter.showStatus(inReplyToStatusId);
            inReplyToStatusList.add(status);

            inReplyToStatusId = status.getInReplyToStatusId();
            if (isCancelled()) {
                return inReplyToStatusList;
            }
        }
        Collections.reverse(inReplyToStatusList);

        return inReplyToStatusList;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (isCancelled()) {
            return false;
        }

        try {
            if (currentTweet.getInReplyToStatusId() > 0l) {
                statusList = getInReplyToStatusList(currentTweet.getInReplyToStatusId());
            }
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
            tweetList.add(currentTweet);

            if (tweetList.size() <= 0) {
                inReplyToFragment.setContentEmpty(true);
                inReplyToFragment.setEmptyText(R.string.fragment_list_empty);
                inReplyToFragment.setContentShown(true);
                inReplyToFragment.setLoadTaskStatus(FlagUnit.TASK_IDLE);
                return;
            }

            tweetAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
        }

        inReplyToFragment.setLoadTaskStatus(FlagUnit.TASK_IDLE);
    }
}