package io.github.mthli.Tweetin.Task.Unit;

import android.app.Activity;
import android.os.AsyncTask;
import io.github.mthli.Tweetin.Activity.DetailActivity;
import io.github.mthli.Tweetin.Unit.Database.DatabaseUnit;
import io.github.mthli.Tweetin.Unit.Notification.NotificationUnit;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import io.github.mthli.Tweetin.Unit.Tweet.TweetUnit;
import twitter4j.Twitter;

import java.util.List;

public class FavoriteTask extends AsyncTask<Void, Integer, Boolean> {

    private Activity activity;
    private Twitter twitter;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private Tweet tweet;
    private int position;

    private NotificationUnit notificationUnit;

    public FavoriteTask(
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
        notificationUnit.favoriting();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            twitter.createFavorite(tweet.getStatusId());
            tweet.setFavorite(true);

            DatabaseUnit.updatedByFavorite(activity, tweet);
            notificationUnit.favoriteSuccessful();
        } catch (Exception e) {
            notificationUnit.favoriteFailed();

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
                    ((DetailActivity) activity).setFavoriteAtDetail(true);
                }
            }
        }
    }
}
