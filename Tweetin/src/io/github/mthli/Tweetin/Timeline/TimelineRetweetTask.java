package io.github.mthli.Tweetin.Timeline;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import io.github.mthli.Tweetin.Database.Timeline.TimelineAction;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import twitter4j.Twitter;

import java.util.List;

public class TimelineRetweetTask extends AsyncTask<Void, Integer, Boolean> {
    private TimelineFragment timelineFragment;
    private Context context;
    private Twitter twitter;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private Tweet oldTweet;
    private Tweet newTweet;
    private int position = 0;

    private NotificationManager notificationManager;
    private Notification.Builder builder;

    public TimelineRetweetTask(
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
        /* Do something with Icon */
        builder.setTicker(
                context.getString(R.string.tweet_rewteet_ing)
        );
        builder.setContentTitle(
                context.getString(R.string.tweet_rewteet_ing)
        );
        builder.setContentText(oldTweet.getText());
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(Flag.POST_ID, notification);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            twitter.retweetStatus(oldTweet.getStatusId());

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
            newTweet.setRetweetedByUserName(oldTweet.getRetweetedByUserName());

            TimelineAction action = new TimelineAction(context);
            action.openDatabase(true);
            action.updatedByMe(oldTweet.getStatusId());
            action.closeDatabase();

            /* Do something with Icon */
            builder.setTicker(
                    context.getString(R.string.tweet_retweet_successful)
            );
            builder.setContentTitle(
                    context.getString(R.string.tweet_retweet_successful)
            );
            builder.setContentText(oldTweet.getText());
            Notification notification = builder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(Flag.POST_ID, notification);
            notificationManager.cancel(Flag.POST_ID);
        } catch (Exception e) {
            /* Do something with Icon */
            builder.setTicker(
                    context.getString(R.string.tweet_retweet_failed)
            );
            builder.setContentTitle(
                    context.getString(R.string.tweet_retweet_failed)
            );
            builder.setContentText(oldTweet.getText());
            Notification notification = builder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(Flag.POST_ID, notification);

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
