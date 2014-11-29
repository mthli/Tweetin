package io.github.mthli.Tweetin.Unit.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;

public class NotificationUnit {

    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;

    private Context context;
    private Tweet tweet;

    public NotificationUnit(
            Context context,
            Tweet tweet
    ) {
        this.context = context;
        this.tweet = tweet;

        this.notificationManager =  (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        this.builder = new NotificationCompat.Builder(context);
    }

    public void deleting() {
        builder.setSmallIcon(R.drawable.ic_notification_delete);
        builder.setTicker(
                context.getString(R.string.tweet_notification_delete_ing)
        );
        builder.setContentTitle(
                context.getString(R.string.tweet_notification_delete_ing)
        );
        builder.setContentText(tweet.getText());
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(Flag.NOTIFICATION_PROGRESS_ID, notification);
    }
    public void deleteSuccessful() {
        builder.setSmallIcon(R.drawable.ic_notification_delete);
        builder.setTicker(
                context.getString(R.string.tweet_notification_delete_successful)
        );
        builder.setContentTitle(
                context.getString(R.string.tweet_notification_delete_successful)
        );
        builder.setContentText(tweet.getText());
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(Flag.NOTIFICATION_PROGRESS_ID, notification);
        notificationManager.cancel(Flag.NOTIFICATION_PROGRESS_ID);
    }
    public void deleteFailed() {
        builder.setSmallIcon(R.drawable.ic_notification_delete);
        builder.setTicker(
                context.getString(R.string.tweet_notification_delete_failed)
        );
        builder.setContentTitle(
                context.getString(R.string.tweet_notification_delete_failed)
        );
        builder.setContentText(tweet.getText());
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(Flag.NOTIFICATION_PROGRESS_ID, notification);
    }

    public void retweeting() {
        builder.setSmallIcon(R.drawable.ic_notification_retweet);
        builder.setTicker(
                context.getString(R.string.tweet_notification_rewteet_ing)
        );
        builder.setContentTitle(
                context.getString(R.string.tweet_notification_rewteet_ing)
        );
        builder.setContentText(tweet.getText());
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(Flag.NOTIFICATION_PROGRESS_ID, notification);
    }
    public void retweetSuccessful() {
        builder.setSmallIcon(R.drawable.ic_notification_retweet);
        builder.setTicker(
                context.getString(R.string.tweet_notification_retweet_successful)
        );
        builder.setContentTitle(
                context.getString(R.string.tweet_notification_retweet_successful)
        );
        builder.setContentText(tweet.getText());
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(Flag.NOTIFICATION_PROGRESS_ID, notification);
        notificationManager.cancel(Flag.NOTIFICATION_PROGRESS_ID);
    }
    public void retweetFailed() {
        builder.setSmallIcon(R.drawable.ic_notification_retweet);
        builder.setTicker(
                context.getString(R.string.tweet_notification_retweet_failed)
        );
        builder.setContentTitle(
                context.getString(R.string.tweet_notification_retweet_failed)
        );
        builder.setContentText(tweet.getText());
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(Flag.NOTIFICATION_PROGRESS_ID, notification);
    }

    public void addFavoriting() {
        builder.setSmallIcon(R.drawable.ic_notification_favorite);
        builder.setTicker(
                context.getString(R.string.tweet_notification_add_favorite_ing)
        );
        builder.setContentTitle(
                context.getString(R.string.tweet_notification_add_favorite_ing)
        );
        builder.setContentText(tweet.getText());
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(Flag.NOTIFICATION_PROGRESS_ID, notification);
    }
    public void addFavoriteSuccessful() {
        builder.setSmallIcon(R.drawable.ic_notification_favorite);
        builder.setTicker(
                context.getString(R.string.tweet_notification_add_favorite_successful)
        );
        builder.setContentTitle(
                context.getString(R.string.tweet_notification_add_favorite_successful)
        );
        builder.setContentText(tweet.getText());
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(Flag.NOTIFICATION_PROGRESS_ID, notification);
        notificationManager.cancel(Flag.NOTIFICATION_PROGRESS_ID);
    }
    public void addFavoriteFailed() {
        builder.setSmallIcon(R.drawable.ic_notification_favorite);
        builder.setTicker(
                context.getString(R.string.tweet_notification_add_favorite_failed)
        );
        builder.setContentTitle(
                context.getString(R.string.tweet_notification_add_favorite_failed)
        );
        builder.setContentText(tweet.getText());
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(Flag.NOTIFICATION_PROGRESS_ID, notification);
    }
    public void cancelFavoriting() {
        builder.setSmallIcon(R.drawable.ic_notification_cancel);
        builder.setTicker(
                context.getString(R.string.tweet_notification_un_favorite_ing)
        );
        builder.setContentTitle(
                context.getString(R.string.tweet_notification_un_favorite_ing)
        );
        builder.setContentText(tweet.getText());
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(Flag.NOTIFICATION_PROGRESS_ID, notification);
    }
    public void cancelFavoriteSuccessful() {
        builder.setSmallIcon(R.drawable.ic_notification_cancel);
        builder.setTicker(
                context.getString(R.string.tweet_notification_un_favorite_successful)
        );
        builder.setContentTitle(
                context.getString(R.string.tweet_notification_un_favorite_successful)
        );
        builder.setContentText(tweet.getText());
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(Flag.NOTIFICATION_PROGRESS_ID, notification);
        notificationManager.cancel(Flag.NOTIFICATION_PROGRESS_ID);
    }
    public void cancelFavoriteFailed() {
        builder.setSmallIcon(R.drawable.ic_notification_cancel);
        builder.setTicker(
                context.getString(R.string.tweet_notification_un_favorite_failed)
        );
        builder.setContentTitle(
                context.getString(R.string.tweet_notification_un_favorite_failed)
        );
        builder.setContentText(tweet.getText());
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(Flag.NOTIFICATION_PROGRESS_ID, notification);
    }
}
