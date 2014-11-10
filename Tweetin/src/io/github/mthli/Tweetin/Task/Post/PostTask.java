package io.github.mthli.Tweetin.Task.Post;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.EditText;
import android.widget.ToggleButton;
import io.github.mthli.Tweetin.Activity.PostActivity;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import twitter4j.GeoLocation;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;

import java.io.File;

public class PostTask extends AsyncTask<Void, Integer, Boolean> {
    private PostActivity postActivity;
    private boolean checkIn;
    private boolean photo;
    private String text;
    private String photoPath;

    private Twitter twitter;
    private StatusUpdate update;

    private int postFlag;
    private long statusId;
    private String screenName;

    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;

    public PostTask(PostActivity postActivity) {
        this.postActivity = postActivity;
        this.checkIn = false;
        this.photo = false;
        this.photoPath = null;
        this.postFlag = 0;
        this.statusId = 0;
        this.screenName = null;
    }

    @Override
    protected void onPreExecute() {
        EditText postEdit = postActivity.getPostEdit();
        ToggleButton postCheckInButton = postActivity.getPostCheckInButton();
        ToggleButton postPhotoButton = postActivity.getPostPhotoButton();
        if (postCheckInButton.isChecked()) {
            checkIn = true;
        }
        if (postPhotoButton.isChecked()) {
            photo = true;
        }
        text = postEdit.getText().toString();
        photoPath = postActivity.getPhotoPath();

        twitter = postActivity.getTwitter();

        postFlag = postActivity.getPostFlag();
        statusId = postActivity.getStatusId();
        screenName = postActivity.getScreenName();

        notificationManager = (NotificationManager) postActivity
                .getSystemService(Context.NOTIFICATION_SERVICE);
        LocationManager locationManager = (LocationManager) postActivity
                .getSystemService(Context.LOCATION_SERVICE);
        builder = new NotificationCompat.Builder(postActivity);
        builder.setSmallIcon(R.drawable.ic_tweet_notification);
        builder.setTicker(
                postActivity.getString(R.string.post_notification_post_ing)
        );
        builder.setContentTitle(
                postActivity.getString(R.string.post_notification_post_ing)
        );
        builder.setContentText(text);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(Flag.NOTIFICATION_PROGRESS_ID, notification);

        update = new StatusUpdate(text);
        if (checkIn) {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000,
                    0,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            /* Do nothing */
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                            /* Do nothing */
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                            /* Do nothing */
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                            /* Do nothing */
                        }
                    }
            );
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                GeoLocation geo = new GeoLocation(
                        location.getLatitude(),
                        location.getLongitude()
                );
                update.setLocation(geo);
            }
        }
        if (photo) {
            File file = new File(photoPath);
            update.setMedia(file);
        }
        if (
                (postFlag == Flag.POST_REPLY || postFlag == Flag.POST_QUOTE)
                        && text.contains(screenName)
                ) {
            update.setInReplyToStatusId(statusId);
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            twitter.updateStatus(update);

            builder.setSmallIcon(R.drawable.ic_tweet_notification);
            builder.setTicker(
                    postActivity.getString(R.string.post_notification_post_successful)
            );
            builder.setContentTitle(
                    postActivity.getString(R.string.post_notification_post_successful)
            );
            builder.setContentText(text);
            Notification notification = builder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(Flag.NOTIFICATION_PROGRESS_ID, notification);
            notificationManager.cancel(Flag.NOTIFICATION_PROGRESS_ID);
        } catch (Exception e) {
            builder.setSmallIcon(R.drawable.ic_tweet_notification);
            builder.setTicker(
                    postActivity.getString(R.string.post_notification_post_failed)
            );
            builder.setContentTitle(
                    postActivity.getString(R.string.post_notification_post_failed)
            );
            builder.setContentText(text);
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
        /* Do nothing */
    }
}
