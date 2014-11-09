package io.github.mthli.Tweetin.Task.Mention;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import io.github.mthli.Tweetin.Database.Favorite.FavoriteAction;
import io.github.mthli.Tweetin.Database.Mention.MentionAction;
import io.github.mthli.Tweetin.Database.Timeline.TimelineAction;
import io.github.mthli.Tweetin.Fragment.MentionFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import twitter4j.Twitter;

import java.util.List;

public class MentionFavoriteTask extends AsyncTask<Void, Integer, Boolean> {
    private MentionFragment mentionFragment;
    private Context context;
    private Twitter twitter;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private Tweet oldTweet;
    private Tweet newTweet;
    private int position;

    private NotificationManager notificationManager;
    private Notification.Builder builder;

    public MentionFavoriteTask(
            MentionFragment mentionFragment,
            int position
    ) {
        this.mentionFragment = mentionFragment;
        this.position = position;
    }

    @Override
    protected void onPreExecute() {
        context = mentionFragment.getContentView().getContext();
        twitter = mentionFragment.getTwitter();

        tweetAdapter = mentionFragment.getTweetAdapter();
        tweetList = mentionFragment.getTweetList();
        oldTweet = tweetList.get(position);

        notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.ic_tweet_notification);
        builder.setTicker(
                context.getString(R.string.tweet_notification_add_favorite_ing)
        );
        builder.setContentTitle(
                context.getString(R.string.tweet_notification_add_favorite_ing)
        );
        builder.setContentText(oldTweet.getText());
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(Flag.NOTIFICATION_PROGRESS_ID, notification);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            twitter.createFavorite(oldTweet.getStatusId());

            newTweet = new Tweet();
            newTweet.setStatusId(oldTweet.getStatusId());
            newTweet.setReplyToStatusId(oldTweet.getReplyToStatusId());
            newTweet.setUserId(oldTweet.getUserId());
            newTweet.setRetweetedByUserId(oldTweet.getRetweetedByUserId());
            newTweet.setAvatarURL(oldTweet.getAvatarURL());
            newTweet.setCreatedAt(oldTweet.getCreatedAt());
            newTweet.setName(oldTweet.getName());
            newTweet.setScreenName(oldTweet.getScreenName());
            newTweet.setProtect(oldTweet.isProtect());
            newTweet.setCheckIn(oldTweet.getCheckIn());
            newTweet.setText(oldTweet.getText());
            newTweet.setRetweet(oldTweet.isRetweet());
            newTweet.setRetweetedByUserName(
                    oldTweet.getRetweetedByUserName()
            );
            newTweet.setFavorite(true);

            TimelineAction action = new TimelineAction(context);
            action.openDatabase(true);
            action.updatedByFavorite(oldTweet.getStatusId()); //
            action.closeDatabase();
            MentionAction mentionAction = new MentionAction(context);
            mentionAction.openDatabase(true);
            mentionAction.updatedByFavorite(oldTweet.getStatusId()); //
            mentionAction.closeDatabase();
            FavoriteAction favoriteAction = new FavoriteAction(context);
            favoriteAction.openDatabase(true);
            favoriteAction.updatedByFavorite(oldTweet.getStatusId()); //
            favoriteAction.closeDatabase();

            builder.setSmallIcon(R.drawable.ic_tweet_notification);
            builder.setTicker(
                    context.getString(R.string.tweet_notification_add_favorite_successful)
            );
            builder.setContentTitle(
                    context.getString(R.string.tweet_notification_add_favorite_successful)
            );
            builder.setContentText(oldTweet.getText());
            Notification notification = builder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(Flag.NOTIFICATION_PROGRESS_ID, notification);
            notificationManager.cancel(Flag.NOTIFICATION_PROGRESS_ID);
        } catch (Exception e) {
            builder.setSmallIcon(R.drawable.ic_tweet_notification);
            builder.setTicker(
                    context.getString(R.string.tweet_notification_add_favorite_failed)
            );
            builder.setContentTitle(
                    context.getString(R.string.tweet_notification_add_favorite_failed)
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
        }
    }
}
