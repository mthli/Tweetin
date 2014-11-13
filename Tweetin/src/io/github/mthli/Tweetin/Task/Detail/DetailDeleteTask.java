package io.github.mthli.Tweetin.Task.Detail;

import android.os.AsyncTask;
import io.github.mthli.Tweetin.Activity.DetailActivity;
import io.github.mthli.Tweetin.Unit.Database.DatabaseUnit;
import io.github.mthli.Tweetin.Unit.Notification.NotificationUnit;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import twitter4j.Twitter;

import java.util.List;

public class DetailDeleteTask extends AsyncTask<Void, Integer, Boolean> {
    private DetailActivity detailActivity;
    private Twitter twitter;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private Tweet oldTweet;
    private int position;

    private NotificationUnit notificationUnit;

    public DetailDeleteTask(
            DetailActivity detailActivity,
            int position
    ) {
        this.detailActivity = detailActivity;
        this.position = position;
    }

    @Override
    protected void onPreExecute() {
        twitter = detailActivity.getTwitter();

        tweetAdapter = detailActivity.getTweetAdapter();
        tweetList = detailActivity.getTweetList();
        oldTweet = tweetList.get(position);

        notificationUnit = new NotificationUnit(detailActivity, oldTweet);
        notificationUnit.deleting();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            twitter.destroyStatus(oldTweet.getStatusId());

            DatabaseUnit.deleteRecord(detailActivity, oldTweet);
            notificationUnit.deleteSuccessful();
        } catch (Exception e) {
            notificationUnit.deleteFailed();

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

            if (oldTweet.getStatusId() == detailActivity.getTweetFromIntent().getStatusId()) {
                detailActivity.setDeleteAtDetail(true);
                detailActivity.finishDetail();
            }
        }
    }
}
