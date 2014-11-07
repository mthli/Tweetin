package io.github.mthli.Tweetin.Task.Mention;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import io.github.mthli.Tweetin.Fragment.Mention.MentionFragment;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import twitter4j.Twitter;

import java.util.List;

public class MentionRetweetTask extends AsyncTask<Void, Integer, Boolean> {
    private MentionFragment mentionFragment;
    private Context context;
    private Twitter twitter;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private Tweet oldTweet;
    private Tweet newTweet;
    private int position = 0;

    private NotificationManager notificationManager;
    private Notification.Builder builder;

    public MentionRetweetTask(
            MentionFragment mentionFragment,
            int position
    ) {
        this.mentionFragment = mentionFragment;
        this.position = position;
    }

    @Override
    protected void onPreExecute() {
        /* Do something */
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        /* Do something */

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
            tweetList.add(position, newTweet);
            tweetAdapter.notifyDataSetChanged();
        }
    }
}
