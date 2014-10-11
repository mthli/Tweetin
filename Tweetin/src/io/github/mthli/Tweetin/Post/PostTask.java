package io.github.mthli.Tweetin.Post;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ToggleButton;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Flag;
import twitter4j.GeoLocation;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;

import java.io.File;

public class PostTask extends AsyncTask<Void, Integer, Boolean> {

    private PostActivity postActivity;
    private boolean isCheckIn;
    private boolean isSelectPic;
    private String text;
    private String picPath;

    private Twitter twitter;
    private StatusUpdate update;

    private int postFlag;
    private long quoteStatusId;
    private String quoteScreenName;
    private long replyStatusId;
    private String replyScreenName;

    private NotificationManager notificationManager;
    private Notification.Builder builder;
    private static final int POST_ID = Flag.POST_ID;

    public PostTask(PostActivity postActivity) {
        this.postActivity = postActivity;
        this.isCheckIn = false;
        this.isSelectPic = false;
        this.picPath = null;
        this.postFlag = 0;
        this.quoteStatusId = 0;
        this.quoteScreenName = null;
        this.replyStatusId = 0;
        this.replyScreenName = null;
    }

    @Override
    protected void onPreExecute() {
        EditText postEdit = postActivity.getPostEdit();
        ToggleButton checkIn = postActivity.getCheckIn();
        ToggleButton selectPic = postActivity.getSelectPic();
        if (checkIn.isChecked()) {
            isCheckIn = true;
        }
        if (selectPic.isChecked()) {
            isSelectPic = true;
        }
        text = postEdit.getText().toString();
        picPath = postActivity.getPicPath();

        twitter = postActivity.getTwitter();

        postFlag = postActivity.getPostFlag();
        quoteStatusId = postActivity.getQuoteStatusId();
        quoteScreenName = postActivity.getQuoteScreenName();
        replyStatusId = postActivity.getReplyStatusId();
        replyScreenName = postActivity.getReplyScreenName();

        notificationManager = (NotificationManager) postActivity
                .getSystemService(Context.NOTIFICATION_SERVICE);
        LocationManager locationManager = (LocationManager) postActivity
                .getSystemService(Context.LOCATION_SERVICE);
        builder = new Notification.Builder(postActivity);
        builder.setSmallIcon(R.drawable.ic_post_notification);
        builder.setTicker(
                postActivity.getString(R.string.post_notification_ing)
        );
        builder.setContentTitle(
                postActivity.getString(R.string.post_notification_ing)
        );
        builder.setContentText(text);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(POST_ID, notification);

        update = new StatusUpdate(text);
        if (isCheckIn) {
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
        if (isSelectPic) {
            File file = new File(picPath);
            update.setMedia(file);
        }

        if (postFlag == Flag.POST_REPLY && text.contains(replyScreenName)) {
            update.setInReplyToStatusId(replyStatusId);
        }

        if (postFlag == Flag.POST_RETWEET_QUOTE && text.contains(quoteScreenName)) {
            update.setInReplyToStatusId(quoteStatusId);
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            twitter.updateStatus(update);

            builder.setSmallIcon(R.drawable.ic_post_notification);
            builder.setTicker(
                    postActivity.getString(R.string.post_notification_successful)
            );
            builder.setContentTitle(
                    postActivity.getString(R.string.post_notification_successful)
            );
            builder.setContentText(text);
            Notification notification = builder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(POST_ID, notification);
            notificationManager.cancel(POST_ID);
        } catch (Exception e) {
            builder.setSmallIcon(R.drawable.ic_post_notification);
            builder.setTicker(
                    postActivity.getString(R.string.post_notification_failed)
            );
            builder.setContentTitle(
                    postActivity.getString(R.string.post_notification_failed)
            );
            builder.setContentText(text);
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
        /* Do nothing */
    }
}
