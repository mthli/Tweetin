package io.github.mthli.Tweetin.Task.Detail;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import io.github.mthli.Tweetin.Activity.DetailActivity;
import io.github.mthli.Tweetin.Database.Favorite.FavoriteAction;
import io.github.mthli.Tweetin.Database.Mention.MentionAction;
import io.github.mthli.Tweetin.Database.Timeline.TimelineAction;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import twitter4j.Twitter;

import java.util.List;

public class DetailRetweetTask extends AsyncTask<Void, Integer, Boolean> {
    private DetailActivity detailActivity;
    private Twitter twitter;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private Tweet oldTweet;
    private Tweet newTweet;
    private int position;

    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;

    public DetailRetweetTask(
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

        notificationManager = (NotificationManager) detailActivity
                .getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(detailActivity);
        builder.setSmallIcon(R.drawable.ic_tweet_notification);
        builder.setTicker(
                detailActivity.getString(R.string.tweet_notification_rewteet_ing)
        );
        builder.setContentTitle(
                detailActivity.getString(R.string.tweet_notification_rewteet_ing)
        );
        builder.setContentText(oldTweet.getText());
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(Flag.NOTIFICATION_PROGRESS_ID, notification);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            twitter.retweetStatus(oldTweet.getStatusId());

            newTweet = new Tweet();
            newTweet.setStatusId(oldTweet.getStatusId());
            newTweet.setReplyToStatusId(oldTweet.getReplyToStatusId());
            newTweet.setUserId(oldTweet.getUserId());
            newTweet.setRetweetedByUserId(detailActivity.getUseId());
            newTweet.setAvatarURL(oldTweet.getAvatarURL());
            newTweet.setCreatedAt(oldTweet.getCreatedAt());
            newTweet.setName(oldTweet.getName());
            newTweet.setScreenName(oldTweet.getScreenName());
            newTweet.setProtect(oldTweet.isProtect());
            newTweet.setCheckIn(oldTweet.getCheckIn());
            newTweet.setPhotoURL(oldTweet.getPhotoURL());
            newTweet.setText(oldTweet.getText());
            newTweet.setRetweet(true);
            newTweet.setRetweetedByUserName(
                    detailActivity.getString(R.string.tweet_info_retweeted_by_me)
            );
            newTweet.setFavorite(oldTweet.isFavorite());

            TimelineAction action = new TimelineAction(detailActivity);
            action.openDatabase(true);
            action.updatedByRetweet(oldTweet.getStatusId()); //
            action.closeDatabase();
            MentionAction mentionAction = new MentionAction(detailActivity);
            mentionAction.openDatabase(true);
            mentionAction.updatedByRetweet(oldTweet.getStatusId()); //
            mentionAction.closeDatabase();
            FavoriteAction favoriteAction = new FavoriteAction(detailActivity);
            favoriteAction.openDatabase(true);
            favoriteAction.updatedByRetweet(oldTweet.getStatusId()); //
            favoriteAction.closeDatabase();

            builder.setSmallIcon(R.drawable.ic_tweet_notification);
            builder.setTicker(
                    detailActivity.getString(R.string.tweet_notification_retweet_successful)
            );
            builder.setContentTitle(
                    detailActivity.getString(R.string.tweet_notification_retweet_successful)
            );
            builder.setContentText(oldTweet.getText());
            Notification notification = builder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(Flag.NOTIFICATION_PROGRESS_ID, notification);
            notificationManager.cancel(Flag.NOTIFICATION_PROGRESS_ID);
        } catch (Exception e) {
            builder.setSmallIcon(R.drawable.ic_tweet_notification);
            builder.setTicker(
                    detailActivity.getString(R.string.tweet_notification_retweet_failed)
            );
            builder.setContentTitle(
                    detailActivity.getString(R.string.tweet_notification_retweet_failed)
            );
            builder.setContentText(oldTweet.getText());
            Notification notification = builder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(Flag.NOTIFICATION_PROGRESS_ID, notification);

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
            tweetList.add(position, newTweet);
            tweetAdapter.notifyDataSetChanged();

            if (oldTweet.getStatusId() == detailActivity.getTweetFromIntent().getStatusId()) {
                detailActivity.setRetweetFromDetail(true);
            }
        }
    }
}
