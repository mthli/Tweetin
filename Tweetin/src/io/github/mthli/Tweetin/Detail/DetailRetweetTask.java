package io.github.mthli.Tweetin.Detail;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import io.github.mthli.Tweetin.Database.Main.MainAction;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.Base.Tweet;
import io.github.mthli.Tweetin.Unit.Flag;
import twitter4j.Twitter;

public class DetailRetweetTask extends AsyncTask<Void, Integer, Boolean> {
    private DetailActivity detailActivity;

    private Twitter twitter;
    private long useId;
    private Tweet tweet;

    private NotificationManager notificationManager;
    private Notification.Builder builder;
    private static final int POST_ID = Flag.POST_ID;

    public DetailRetweetTask(DetailActivity detailActivity) {
        this.detailActivity = detailActivity;
        this.useId = 0;
    }

    @Override
    protected void onPreExecute() {
        twitter = detailActivity.getTwitter();
        useId = detailActivity.getUseId();
        tweet = detailActivity.getThisTweet();

        notificationManager = (NotificationManager) detailActivity
                .getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new Notification.Builder(detailActivity);
        builder.setSmallIcon(R.drawable.ic_post_notification);
        builder.setTicker(
                detailActivity.getString(R.string.detail_retweet_ing)
        );
        builder.setContentTitle(
                detailActivity.getString(R.string.detail_retweet_ing)
        );
        builder.setContentText(tweet.getText());
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(POST_ID, notification);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            twitter.retweetStatus(tweet.getTweetId());

            Tweet newTweet = new Tweet();
            newTweet.setTweetId(tweet.getTweetId());
            newTweet.setUserId(tweet.getUserId());
            newTweet.setAvatarUrl(tweet.getAvatarUrl());
            newTweet.setCreatedAt(tweet.getCreatedAt());
            newTweet.setName(tweet.getName());
            newTweet.setScreenName(tweet.getScreenName());
            newTweet.setProtect(tweet.isProtected());
            newTweet.setText(tweet.getText());
            newTweet.setCheckIn(tweet.getCheckIn());
            newTweet.setRetweet(true);
            newTweet.setRetweetedByName(
                    detailActivity.getString(R.string.detail_retweeted_by_me)
            );
            newTweet.setRetweetedById(useId);
            newTweet.setReplyTo(tweet.getReplyTo());

            MainAction action = new MainAction(detailActivity);
            action.opewDatabase(true);
            action.updateByMe(tweet.getTweetId(), newTweet);
            action.closeDatabase();

            builder.setSmallIcon(R.drawable.ic_post_notification);
            builder.setTicker(
                    detailActivity.getString(R.string.detail_retweet_successful)
            );
            builder.setContentTitle(
                    detailActivity.getString(R.string.detail_retweet_successful)
            );
            builder.setContentText(tweet.getText());
            Notification notification = builder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(POST_ID, notification);
            notificationManager.cancel(POST_ID);
        } catch (Exception e) {
            builder.setSmallIcon(R.drawable.ic_post_notification);
            builder.setTicker(
                    detailActivity.getString(R.string.detail_retweet_failed)
            );
            builder.setContentTitle(
                    detailActivity.getString(R.string.detail_retweet_failed)
            );
            builder.setContentText(tweet.getText());
            Notification notification = builder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(POST_ID, notification);

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
        if (!result) {
            detailActivity.getThisStatusRetweetedByName().setVisibility(View.GONE);
            detailActivity.getDetailRetweet().setVisibility(View.VISIBLE);
        }
    }
}
