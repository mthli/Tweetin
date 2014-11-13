package io.github.mthli.Tweetin.Task.Unit;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import io.github.mthli.Tweetin.Activity.DetailActivity;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Database.DatabaseUnit;
import io.github.mthli.Tweetin.Unit.Notification.NotificationUnit;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import io.github.mthli.Tweetin.Unit.Tweet.TweetUnit;
import twitter4j.Twitter;

import java.util.List;

public class RetweetTask extends AsyncTask<Void, Integer, Boolean> {
    private Activity activity;
    private Twitter twitter;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private Tweet tweet;
    private int position;

    private NotificationUnit notificationUnit;

    public RetweetTask(
            Activity activity,
            Twitter twitter,
            TweetAdapter tweetAdapter,
            List<Tweet> tweetList,
            int position
    ) {
        this.activity = activity;
        this.twitter = twitter;
        this.tweetAdapter = tweetAdapter;
        this.tweetList = tweetList;
        this.position = position;
    }

    @Override
    protected void onPreExecute() {
        tweet = tweetList.get(position);

        notificationUnit = new NotificationUnit(activity, tweet);
        notificationUnit.retweeting();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            twitter.retweetStatus(tweet.getStatusId());

            SharedPreferences sharedPreferences = activity
                    .getSharedPreferences(
                            activity.getString(R.string.sp_name),
                            Context.MODE_PRIVATE
                    );
            long useId = sharedPreferences.getLong(
                    activity.getString(R.string.sp_use_id),
                    -1l
            );
            tweet.setRetweetedByUserId(useId);
            tweet.setRetweet(true);
            tweet.setRetweetedByUserName(
                    activity.getString(R.string.tweet_info_retweeted_by_me)
            );

            DatabaseUnit.updatedByRetweet(activity, tweet);
            notificationUnit.retweetSuccessful();
        } catch (Exception e) {
            notificationUnit.retweetFailed();

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
            tweetAdapter.notifyDataSetChanged();

            if (activity instanceof DetailActivity) {
                if (tweet.getStatusId() == TweetUnit.getTweetFromIntent(activity).getStatusId()) {
                    ((DetailActivity) activity).setRetweetAtDetail(true);
                }
            }
        }
    }
}
