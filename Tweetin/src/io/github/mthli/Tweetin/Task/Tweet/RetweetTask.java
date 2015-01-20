package io.github.mthli.Tweetin.Task.Tweet;

import android.app.Activity;
import android.os.AsyncTask;
import io.github.mthli.Tweetin.Data.DataUnit;
import io.github.mthli.Tweetin.Notification.NotificationUnit;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.Tweet;
import io.github.mthli.Tweetin.Tweet.TweetAdapter;
import io.github.mthli.Tweetin.Twitter.TwitterUnit;
import twitter4j.TwitterException;

import java.util.List;

public class RetweetTask extends AsyncTask<Void, Void, Boolean> {
    private Activity activity;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private int position;
    private Tweet tweet;

    private String me;

    public RetweetTask(Activity activity, TweetAdapter tweetAdapter, List<Tweet> tweetList, int position) {
        this.activity = activity;

        this.tweetAdapter = tweetAdapter;
        this.tweetList = tweetList;
        this.position = position;
        this.tweet = tweetList.get(position);

        this.me = activity.getString(R.string.tweet_info_retweeted_by_me);
    }

    @Override
    protected void onPreExecute() {
        NotificationUnit.show(activity, R.drawable.ic_notification_retweet, R.string.notification_retweet_ing, tweet.getText());
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            TwitterUnit.getTwitterFromSharedPreferences(activity).retweetStatus(tweet.getStatusId());

            tweet.setRetweetedByName(me);
            tweet.setRetweetedByScreenName(TwitterUnit.getUseScreenNameFromSharedPreferences(activity));

            DataUnit.updateByRetweet(activity, tweet);

            NotificationUnit.show(activity, R.drawable.ic_notification_retweet, R.string.notification_retweet_successful, tweet.getText());
            NotificationUnit.cancel(activity);
        } catch (TwitterException t) {
            NotificationUnit.show(activity, R.drawable.ic_notification_retweet, R.string.notification_retweet_failed, tweet.getText());
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
        tweetAdapter.notifyDataSetChanged();
    }
}
