package io.github.mthli.Tweetin.Task.Unit;

import android.app.Activity;
import android.os.AsyncTask;
import io.github.mthli.Tweetin.Activity.DetailActivity;
import io.github.mthli.Tweetin.Activity.MainActivity;
import io.github.mthli.Tweetin.Database.Favorite.FavoriteAction;
import io.github.mthli.Tweetin.Unit.Database.DatabaseUnit;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import io.github.mthli.Tweetin.Unit.Notification.NotificationUnit;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import io.github.mthli.Tweetin.Unit.Tweet.TweetUnit;
import twitter4j.Twitter;

import java.util.List;

public class CancelTask extends AsyncTask<Void, Integer, Boolean> {

    private Activity activity;
    private Twitter twitter;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private Tweet tweet;
    private int position;

    private NotificationUnit notificationUnit;

    public CancelTask(
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
        notificationUnit.cancelFavoriting();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            twitter.destroyFavorite(tweet.getStatusId());
            tweet.setFavorite(false);

            DatabaseUnit.updatedByFavorite(activity, tweet);
            FavoriteAction action = new FavoriteAction(activity);
            action.openDatabase(true);
            action.deleteRecord(tweet);
            action.closeDatabase();

            notificationUnit.cancelFavoriteSuccessful();
        } catch (Exception e) {
            notificationUnit.cancelFavoriteFailed();

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
            if (activity instanceof MainActivity
                    && ((MainActivity) activity).fragmentFlag == Flag.IN_FAVORITE_FRAGMENT) {
                tweetList.remove(position);
            }
            tweetAdapter.notifyDataSetChanged();

            if (activity instanceof DetailActivity) {
                if (tweet.getStatusId() == TweetUnit.getTweetFromIntent(activity).getStatusId()) {
                    ((DetailActivity) activity).setFavoriteAtDetail(false);
                    ((DetailActivity) activity).finishDetail();
                }
            }
        }
    }
}
