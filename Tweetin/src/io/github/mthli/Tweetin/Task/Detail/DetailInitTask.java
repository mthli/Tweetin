package io.github.mthli.Tweetin.Task.Detail;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;
import io.github.mthli.Tweetin.Activity.DetailActivity;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import io.github.mthli.Tweetin.Unit.Tweet.TweetUnit;
import twitter4j.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DetailInitTask extends AsyncTask<Void, Integer, Boolean> {

    private DetailActivity detailActivity;
    private Twitter twitter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList = new ArrayList<Tweet>();
    private twitter4j.Status currentStatus;
    private Tweet currentTweet = new Tweet();

    public DetailInitTask(DetailActivity detailActivity) {
        this.detailActivity = detailActivity;
    }

    @Override
    protected void onPreExecute() {
        if (detailActivity.getRefreshFlag() == Flag.DETAIL_TASK_RUNNING) {
            onCancelled();
        } else {
            detailActivity.setRefreshFlag(Flag.DETAIL_TASK_RUNNING);
        }

        twitter = detailActivity.getTwitter();

        swipeRefreshLayout = detailActivity.getSwipeRefreshLayout();

        tweetAdapter = detailActivity.getTweetAdapter();
        tweetList = detailActivity.getTweetList();
        currentTweet = TweetUnit.getTweetFromIntent(detailActivity);

        swipeRefreshLayout.setRefreshing(true);
    }


    private List<twitter4j.Status> getReplyToStatusList(long replyToStatusId) {
        List<twitter4j.Status> statusList = new ArrayList<twitter4j.Status>();
        while (replyToStatusId > 0) {
            try {
                twitter4j.Status status = twitter.showStatus(replyToStatusId);
                statusList.add(status);
                replyToStatusId = status.getInReplyToStatusId();
                if (isCancelled()) {
                    return statusList;
                }
            } catch (Exception e) {
                return statusList;
            }
        }
        Collections.reverse(statusList);

        return statusList;
    }
    private List<twitter4j.Status> replyToStatusList = new ArrayList<twitter4j.Status>();
    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            if (currentTweet.getReplyToStatusId() > 0) {
                replyToStatusList = getReplyToStatusList(currentTweet.getReplyToStatusId());
            }
            if (isCancelled()) {
                return false;
            }

            currentStatus = twitter.showStatus(currentTweet.getStatusId());
            if (isCancelled()) {
                return false;
            }
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

            if (detailActivity.getIntent().getBooleanExtra(
                    detailActivity.getString(R.string.detail_intent_from_notification),
                    false
            )) {
                SharedPreferences sharedPreferences = detailActivity.getSharedPreferences(
                        detailActivity.getString(R.string.sp_name),
                        Context.MODE_PRIVATE
                );
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(
                        detailActivity.getString(R.string.sp_latest_mention_id),
                        TweetUnit.getTweetFromIntent(detailActivity).getStatusId()
                ).commit();
            }

            TweetUnit tweetUnit = new TweetUnit(detailActivity);
            currentTweet = tweetUnit.getTweetFromStatus(currentStatus);
            tweetList.clear();
            if (replyToStatusList.size() > 0) {
                for (twitter4j.Status status : replyToStatusList) {
                    tweetList.add(
                            tweetUnit.getTweetFromStatus(status)
                    );
                }
            }
            tweetList.add(currentTweet);
            tweetAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(
                    detailActivity,
                    R.string.detail_error_get_detail_failed,
                    Toast.LENGTH_SHORT
            ).show();
        }
        swipeRefreshLayout.setRefreshing(false);
        detailActivity.setRefreshFlag(Flag.DETAIL_TASK_IDLE);
    }
}
