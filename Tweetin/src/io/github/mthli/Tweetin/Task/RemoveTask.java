package io.github.mthli.Tweetin.Task;

import android.app.Activity;
import android.os.AsyncTask;
import io.github.mthli.Tweetin.Activity.DetailActivity;
import io.github.mthli.Tweetin.Data.DataUnit;
import io.github.mthli.Tweetin.Notification.NotificationUnit;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.Tweet;
import io.github.mthli.Tweetin.Tweet.TweetAdapter;
import io.github.mthli.Tweetin.Twitter.TwitterUnit;

public class RemoveTask extends AsyncTask<Void, Integer, Boolean> {
    private Activity activity;

    private TweetAdapter tweetAdapter;
    private Tweet tweet;

    public RemoveTask(Activity activity, TweetAdapter tweetAdapter, Tweet tweet) {
        this.activity = activity;

        this.tweetAdapter = tweetAdapter;
        this.tweet = tweet;
    }

    @Override
    protected void onPreExecute() {
        NotificationUnit.show(activity, R.drawable.ic_notification_favorite_default, R.string.notification_remove_favorite_ing, tweet.getText());
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            TwitterUnit.getTwitterFromSharedPreferences(activity).destroyFavorite(tweet.getStatusId());

            tweet.setFavorite(false);

            DataUnit.updateByFavorite(activity, tweet);

            NotificationUnit.show(activity, R.drawable.ic_notification_favorite_default, R.string.notification_remove_favorite_successful, tweet.getText());
            NotificationUnit.cancel(activity);
        } catch (Exception e) {
            NotificationUnit.show(activity, R.drawable.ic_notification_favorite_default, R.string.notification_remove_favorite_failed, tweet.getText());

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
                /* Do something */
            }
        }
    }
}
