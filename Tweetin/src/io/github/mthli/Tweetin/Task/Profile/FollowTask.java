package io.github.mthli.Tweetin.Task.Profile;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import io.github.mthli.Tweetin.Notification.NotificationUnit;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Twitter.TwitterUnit;
import twitter4j.TwitterException;

public class FollowTask extends AsyncTask<Void, Void, Boolean> {
    private Activity activity;
    private View profile;
    private String screenName;

    public FollowTask(Activity activity, View profile, String screenName) {
        this.activity = activity;
        this.profile = profile;
        this.screenName = screenName;
    }

    @Override
    protected void onPreExecute() {
        NotificationUnit.show(activity, R.drawable.ic_notification_follow, R.string.notification_follow_ing,screenName);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            TwitterUnit.getTwitterFromSharedPreferences(activity).createFriendship(screenName);

            NotificationUnit.show(activity, R.drawable.ic_notification_follow, R.string.notification_follow_successful, screenName);
            NotificationUnit.cancel(activity);
        } catch (TwitterException t) {
            NotificationUnit.show(activity, R.drawable.ic_notification_follow, R.string.notification_follow_failed, screenName);
            return false;
        }

        if (isCancelled()) {
            return false;
        }
        return true;
    }

    @Override
    protected void onCancelled() {}

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            Button follow = (Button) profile.findViewById(R.id.profile_follow);
            follow.setText(activity.getString(R.string.profile_follow_unfollow));
        }
    }
}