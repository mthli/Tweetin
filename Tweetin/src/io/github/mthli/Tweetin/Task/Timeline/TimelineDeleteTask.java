package io.github.mthli.Tweetin.Task.Timeline;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import io.github.mthli.Tweetin.Database.Favorite.FavoriteAction;
import io.github.mthli.Tweetin.Database.Mention.MentionAction;
import io.github.mthli.Tweetin.Database.Timeline.TimelineAction;
import io.github.mthli.Tweetin.Fragment.Timeline.TimelineFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import twitter4j.Twitter;

import java.util.List;

public class TimelineDeleteTask extends AsyncTask<Void, Integer, Boolean> {
    private TimelineFragment timelineFragment;
    private Context context;
    private Twitter twitter;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private Tweet oldTweet;
    private int position;

    private NotificationManager notificationManager;
    private Notification.Builder builder;

    public TimelineDeleteTask(
            TimelineFragment timelineFragment,
            int position
    ) {
        this.timelineFragment = timelineFragment;
        this.position = position;
    }

    @Override
    protected void onPreExecute() {
        context = timelineFragment.getContentView().getContext();
        twitter = timelineFragment.getTwitter();

        tweetAdapter = timelineFragment.getTweetAdapter();
        tweetList = timelineFragment.getTweetList();
        oldTweet = tweetList.get(position);

        notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.ic_tweet_notification);
        builder.setTicker(
                context.getString(R.string.tweet_notification_delete_ing)
        );
        builder.setContentTitle(
                context.getString(R.string.tweet_notification_delete_ing)
        );
        builder.setContentText(oldTweet.getText());
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(Flag.NOTIFICATION_ID, notification);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            twitter.destroyStatus(oldTweet.getStatusId());

            TimelineAction timelineAction = new TimelineAction(context);
            timelineAction.openDatabase(true);
            timelineAction.deleteRecord(oldTweet.getStatusId()); //
            timelineAction.closeDatabase();
            MentionAction mentionAction = new MentionAction(context);
            mentionAction.openDatabase(true);
            mentionAction.deleteRecord(oldTweet.getStatusId()); //
            mentionAction.closeDatabase();
            FavoriteAction favoriteAction = new FavoriteAction(context);
            favoriteAction.openDatabase(true);
            favoriteAction.deleteRecord(oldTweet.getStatusId()); //
            favoriteAction.closeDatabase();

            builder.setSmallIcon(R.drawable.ic_tweet_notification);
            builder.setTicker(
                    context.getString(R.string.tweet_notification_delete_successful)
            );
            builder.setContentTitle(
                    context.getString(R.string.tweet_notification_delete_successful)
            );
            builder.setContentText(oldTweet.getText());
            Notification notification = builder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(Flag.NOTIFICATION_ID, notification);
            notificationManager.cancel(Flag.NOTIFICATION_ID);
        } catch (Exception e) {
            builder.setSmallIcon(R.drawable.ic_tweet_notification);
            builder.setTicker(
                    context.getString(R.string.tweet_notification_delete_failed)
            );
            builder.setContentTitle(
                    context.getString(R.string.tweet_notification_delete_failed)
            );
            builder.setContentText(oldTweet.getText());
            Notification notification = builder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(Flag.NOTIFICATION_ID, notification);

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