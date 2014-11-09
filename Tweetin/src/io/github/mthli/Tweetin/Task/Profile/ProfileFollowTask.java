package io.github.mthli.Tweetin.Task.Profile;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import io.github.mthli.Tweetin.Activity.ProfileActivity;
import io.github.mthli.Tweetin.Fragment.ProfileFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Anim.ActivityAnim;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import twitter4j.Twitter;
import twitter4j.User;

public class ProfileFollowTask extends AsyncTask<Void, Integer, Boolean> {
    private ProfileFragment profileFragment;
    private Context context;

    private Twitter twitter;
    private User user;
    private boolean isFollowing;

    private NotificationManager notificationManager;
    private Notification.Builder builder;

    public ProfileFollowTask(
            ProfileFragment profileFragment,
            boolean isFollowing,
            User user
    ) {
        this.profileFragment = profileFragment;
        this.user = user;
        this.isFollowing = isFollowing;
    }

    @Override
    protected void onPreExecute() {
        context = profileFragment.getContentView().getContext();

        twitter = ((ProfileActivity) profileFragment.getActivity()).getTwitter();

        notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.ic_tweet_notification);
        if (isFollowing) {
            builder.setTicker(context.getString(R.string.profile_notification_un_follow_ing));
            builder.setContentTitle(context.getString(R.string.profile_notification_un_follow_ing));
        } else {
            builder.setTicker(context.getString(R.string.profile_notification_follow_ing));
            builder.setContentTitle(context.getString(R.string.profile_notification_follow_ing));
        }
        builder.setContentText(user.getName() + " @" + user.getScreenName());
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(Flag.NOTIFICATION_PROGRESS_ID, notification);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (isFollowing) {
            try {
                twitter.destroyFriendship(user.getId());

                builder.setSmallIcon(R.drawable.ic_tweet_notification);
                builder.setTicker(context.getString(R.string.profile_notification_un_follow_successful));
                builder.setContentTitle(context.getString(R.string.profile_notification_un_follow_successful));
                builder.setContentText(user.getName() + " @" + user.getScreenName());
                Notification notification = builder.build();
                notification.flags = Notification.FLAG_AUTO_CANCEL;
                notificationManager.notify(Flag.NOTIFICATION_PROGRESS_ID, notification);
                notificationManager.cancel(Flag.NOTIFICATION_PROGRESS_ID);
            } catch (Exception e) {
                builder.setSmallIcon(R.drawable.ic_tweet_notification);
                builder.setTicker(context.getString(R.string.profile_notification_un_follow_failed));
                builder.setContentTitle(context.getString(R.string.profile_notification_un_follow_failed));
                builder.setContentText(user.getName() + " @" + user.getScreenName());
                Notification notification = builder.build();
                notification.flags = Notification.FLAG_AUTO_CANCEL;
                notificationManager.notify(Flag.NOTIFICATION_PROGRESS_ID, notification);

                return false;
            }
        } else {
            try {
                twitter.createFriendship(user.getId());

                builder.setSmallIcon(R.drawable.ic_tweet_notification);
                builder.setTicker(context.getString(R.string.profile_notification_follow_successful));
                builder.setContentTitle(context.getString(R.string.profile_notification_follow_successful));
                builder.setContentText(user.getName() + " @" + user.getScreenName());
                Notification notification = builder.build();
                notification.flags = Notification.FLAG_AUTO_CANCEL;
                notificationManager.notify(Flag.NOTIFICATION_PROGRESS_ID, notification);
                notificationManager.cancel(Flag.NOTIFICATION_PROGRESS_ID);
            } catch (Exception e) {
                builder.setSmallIcon(R.drawable.ic_tweet_notification);
                builder.setTicker(context.getString(R.string.profile_notification_follow_failed));
                builder.setContentTitle(context.getString(R.string.profile_notification_follow_failed));
                builder.setContentText(user.getName() + " @" + user.getScreenName());
                Notification notification = builder.build();
                notification.flags = Notification.FLAG_AUTO_CANCEL;
                notificationManager.notify(Flag.NOTIFICATION_PROGRESS_ID, notification);

                return false;
            }
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
            ActivityAnim anim = new ActivityAnim();
            profileFragment.allTaskDown();
            profileFragment.getActivity().finish();
            anim.rightOut(profileFragment.getActivity());
        }
    }
}
