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

import java.util.List;

public class DeleteTask extends AsyncTask<Void, Integer, Boolean> {
    private Activity activity;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private Tweet tweet;
    private int position;

    public DeleteTask(Activity activity, TweetAdapter tweetAdapter, List<Tweet> tweetList, int position) {
        this.activity = activity;

        this.tweetAdapter = tweetAdapter;
        this.tweetList = tweetList;
        this.position = position;
    }

    @Override
    protected void onPreExecute() {
        tweet = tweetList.get(position);

        NotificationUnit.show(activity, R.drawable.ic_notification_delete, R.string.notification_delete_ing, tweet.getText());
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            TwitterUnit.getTwitterFromSharedPreferences(activity).destroyStatus(tweet.getStatusId());

            DataUnit.updateByDelete(activity, tweet);

            NotificationUnit.show(activity, R.drawable.ic_notification_delete, R.string.notification_delete_successful, tweet.getText());
            NotificationUnit.cancel(activity);
        } catch (Exception e) {
            NotificationUnit.show(activity, R.drawable.ic_notification_delete, R.string.notification_delete_failed, tweet.getText());

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
            tweetList.remove(position);
            tweetAdapter.notifyDataSetChanged();

            if (activity instanceof DetailActivity) {
                /* Do something */
            }
        }
    }
}
