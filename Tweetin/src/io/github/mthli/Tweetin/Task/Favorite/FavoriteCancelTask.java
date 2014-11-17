package io.github.mthli.Tweetin.Task.Favorite;

import android.content.Context;
import android.os.AsyncTask;
import io.github.mthli.Tweetin.Fragment.FavoriteFragment;
import io.github.mthli.Tweetin.Unit.Database.DatabaseUnit;
import io.github.mthli.Tweetin.Unit.Notification.NotificationUnit;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import twitter4j.Twitter;

import java.util.List;

public class FavoriteCancelTask extends AsyncTask<Void, Integer, Boolean> {
    private FavoriteFragment favoriteFragment;
    private Context context;
    private Twitter twitter;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private Tweet tweet;
    private int position;

    private NotificationUnit notificationUnit;

    public FavoriteCancelTask(
            FavoriteFragment favoriteFragment,
            int position
    ) {
        this.favoriteFragment = favoriteFragment;
        this.position = position;
    }

    @Override
    protected void onPreExecute() {
        context = favoriteFragment.getContentView().getContext();
        twitter = favoriteFragment.getTwitter();

        tweetAdapter = favoriteFragment.getTweetAdapter();
        tweetList = favoriteFragment.getTweetList();
        tweet = tweetList.get(position);

        notificationUnit = new NotificationUnit(context, tweet);
        notificationUnit.cancelFavoriting();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            twitter.destroyFavorite(tweet.getStatusId());

            DatabaseUnit.deleteRecord(context, tweet);
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
            tweetList.remove(position);
            tweetAdapter.notifyDataSetChanged();
        }
    }
}
