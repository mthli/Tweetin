package io.github.mthli.Tweetin.Detail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.Tweet;
import twitter4j.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailLoadTask extends AsyncTask<Void, Integer, Boolean> {

    private DetailActivity detailActivity;
    private Twitter twitter;

    private twitter4j.Status replyToStatus;
    private String replyToText;
    private boolean replyToHasPicture;
    private String  replyToPictureURL;

    private Tweet thisTweet;
    private twitter4j.Status thisStatus;
    private String thisText;
    private TextView thisStatusText;
    private boolean thisHasPicture;
    private String thisPictureURL;
    private ImageView thisStatusPicture;

    public DetailLoadTask(DetailActivity detailActivity) {

        this.detailActivity = detailActivity;

        this.replyToText = null;
        this.replyToHasPicture = false;
        this.replyToPictureURL = null;

        this.thisText = null;
        this.thisHasPicture = false;
        this.thisPictureURL = null;
    }

    @Override
    protected void onPreExecute() {
        twitter = detailActivity.getTwitter();
        thisStatusText = detailActivity.getThisStatusText();
        thisStatusPicture = detailActivity.getThisStatusPicture();
        thisTweet = detailActivity.getThisTweet();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            if (thisTweet.getReplyTo() != -1) {
                replyToStatus = twitter.showStatus(thisTweet.getReplyTo());
                URLEntity[] urlEntities = replyToStatus.getURLEntities();
                if (replyToStatus.isRetweet()) {
                    replyToText = replyToStatus.getRetweetedStatus().getText();
                } else {
                    replyToText = replyToStatus.getText();
                }
                if (urlEntities.length > 0) {
                    for (URLEntity entity : urlEntities) {
                        replyToText = replyToText.replace(
                                entity.getURL(),
                                entity.getExpandedURL()
                        );
                    }
                }
                MediaEntity[] mediaEntities = replyToStatus.getMediaEntities();
                if (mediaEntities.length > 0) {
                    for (MediaEntity entity : mediaEntities) {
                        replyToText = replyToText.replace(
                                entity.getURL(),
                                entity.getMediaURL()
                        );
                        if (entity.getType().equals(detailActivity
                                .getString(R.string.detail_expand_url_picture_key))) {
                            replyToHasPicture = true;
                            replyToPictureURL = entity.getMediaURL();
                            break;
                        }
                    }
                }
            }

            if (isCancelled()) {
                return false;
            }

            thisStatus = twitter.showStatus(thisTweet.getTweetId());
            URLEntity[] urlEntities = thisStatus.getURLEntities();
            thisText = thisTweet.getText();
            if (urlEntities.length > 0) {
                for (URLEntity entity : urlEntities) {
                    thisText = thisText.replace(
                            entity.getURL(),
                            entity.getExpandedURL()
                    );
                }
            }
            MediaEntity[] mediaEntities = thisStatus.getMediaEntities();
            if (mediaEntities.length > 0) {
                for (MediaEntity entity : mediaEntities) {
                    thisText = thisText.replace(
                            entity.getURL(),
                            entity.getMediaURL()
                    );
                    if (entity.getType().equals(detailActivity
                                    .getString(R.string.detail_expand_url_picture_key))) {
                        thisHasPicture = true;
                        thisPictureURL = entity.getMediaURL();
                        break;
                    }
                }
            }
        } catch (Exception e) {
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

    /* detail_reply_to_status */
    private LinearLayout replyToStatusLayout;
    private CircleImageView replyToStatusAvatar;
    private TextView replyToStatusCreatedAt;
    private TextView replyToStatusName;
    private TextView replyToStatusScreenName;
    private TextView replyToStatusProtect;
    private TextView replyToStatusText;
    private ImageView replyToStatusPicture;
    private TextView replyToStatusCheckIn;
    private TextView replyToStatusRetweetedByName;
    private void findView() {
        /* detail_reply_to_status */
        replyToStatusLayout = (LinearLayout) detailActivity.findViewById(
                R.id.detail_reply_to_status
        );
        replyToStatusAvatar = (CircleImageView) detailActivity.findViewById(
                R.id.detail_reply_to_status_avatar
        );
        replyToStatusCreatedAt = (TextView) detailActivity.findViewById(
                R.id.detail_reply_to_status_created_at
        );
        replyToStatusName = (TextView) detailActivity.findViewById(
                R.id.detail_reply_to_status_name
        );
        replyToStatusScreenName = (TextView) detailActivity.findViewById(
                R.id.detail_reply_to_status_screen_name
        );
        replyToStatusProtect = (TextView) detailActivity.findViewById(
                R.id.detail_reply_to_status_protect
        );
        replyToStatusText = (TextView) detailActivity.findViewById(
                R.id.detail_reply_to_status_text
        );
        replyToStatusPicture = (ImageView) detailActivity.findViewById(
                R.id.detail_reply_to_status_picture
        );
        replyToStatusCheckIn = (TextView) detailActivity.findViewById(
                R.id.detail_reply_to_status_check_in
        );
        replyToStatusRetweetedByName = (TextView) detailActivity.findViewById(
                R.id.detail_reply_to_status_retweeted_by_name
        );
    }
    private String getShortCreatedAt(String createdAt) {
        SimpleDateFormat format = new SimpleDateFormat(
                detailActivity.getString(R.string.tweet_date_format)
        );
        Date date = new Date();
        String str = format.format(date);
        String[] arrD = str.split(" ");
        String[] arrC = createdAt.split(" ");
        if (arrD[1].equals(arrC[1])) {
            return arrC[0];
        } else {
            return createdAt;
        }
    }
    private Bitmap fixBitmap(Bitmap bitmap) {
        WindowManager manager = (WindowManager) detailActivity
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        /* Maybe do something with Bitmap OOM 2048 * 2048 */
        if (bitmapWidth < screenWidth) {
            float percent = ((float) screenWidth) / ((float) bitmapWidth);
            if (bitmapHeight * percent <= 2048) {
                Matrix matrix = new Matrix();
                matrix.postScale(percent, percent);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
            }
        }

        return bitmap;
    }
    private void replyToStatusAdapter() {
        replyToStatusLayout.setVisibility(View.VISIBLE);
        SimpleDateFormat format = new SimpleDateFormat(
                detailActivity.getString(R.string.detail_date_format)
        );
        RequestQueue queue = Volley.newRequestQueue(detailActivity);
        if (replyToStatus.isRetweet()) {
            Glide.with(detailActivity)
                    .load(replyToStatus.getRetweetedStatus().getUser().getBiggerProfileImageURL())
                    .crossFade()
                    .into(replyToStatusAvatar);
            replyToStatusCreatedAt.setText(
                    getShortCreatedAt(
                            format.format(replyToStatus.getRetweetedStatus().getCreatedAt())
                    )
            );
            replyToStatusName.setText(
                    replyToStatus.getRetweetedStatus().getUser().getName()
            );
            replyToStatusScreenName.setText(
                    "@" + replyToStatus.getRetweetedStatus().getUser().getScreenName()
            );
            if (replyToStatus.getRetweetedStatus().getUser().isProtected()) {
                replyToStatusProtect.setVisibility(View.VISIBLE);
            }
            replyToStatusText.setText(replyToText);
            if (replyToHasPicture) {
                ImageRequest request = new ImageRequest(
                        replyToPictureURL,
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap bitmap) {
                                bitmap = fixBitmap(bitmap);
                                replyToStatusPicture.setImageBitmap(bitmap);
                                replyToStatusPicture.setVisibility(View.VISIBLE);
                            }
                        },
                        0,
                        0,
                        Bitmap.Config.ARGB_8888,
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                /* Do nothing */
                            }
                        }
                );
                queue.add(request);
            }
            Place place = replyToStatus.getRetweetedStatus().getPlace();
            if (place != null) {
                replyToStatusCheckIn.setText(
                        place.getFullName()
                );
                replyToStatusCheckIn.setVisibility(View.VISIBLE);
            }
            replyToStatusRetweetedByName.setText(
                    replyToStatus.getRetweetedStatus().getUser().getName()
            );
            replyToStatusRetweetedByName.setVisibility(View.VISIBLE);
        } else {
            Glide.with(detailActivity)
                    .load(replyToStatus.getUser().getBiggerProfileImageURL())
                    .crossFade()
                    .into(replyToStatusAvatar);
            replyToStatusCreatedAt.setText(
                    getShortCreatedAt(
                            format.format(replyToStatus.getCreatedAt())
                    )
            );
            replyToStatusName.setText(
                    replyToStatus.getUser().getName()
            );
            replyToStatusScreenName.setText(
                    "@" + replyToStatus.getUser().getScreenName()
            );
            if (replyToStatus.getUser().isProtected()) {
                replyToStatusProtect.setVisibility(View.VISIBLE);
            }
            replyToStatusText.setText(replyToText);
            if (replyToHasPicture) {
                ImageRequest request = new ImageRequest(
                        replyToPictureURL,
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap bitmap) {
                                bitmap = fixBitmap(bitmap);
                                replyToStatusPicture.setImageBitmap(bitmap);
                                replyToStatusPicture.setVisibility(View.VISIBLE);
                            }
                        },
                        0,
                        0,
                        Bitmap.Config.ARGB_8888,
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                /* Do nothing */
                            }
                        }
                );
                queue.add(request);
            }
            Place place = replyToStatus.getPlace();
            if (place != null) {
                replyToStatusCheckIn.setText(
                        place.getFullName()
                );
                replyToStatusCheckIn.setVisibility(View.VISIBLE);
            }
        }
        if (replyToStatus.isRetweetedByMe() || replyToStatus.isRetweeted()) {
            replyToStatusRetweetedByName.setText(
                    detailActivity.getString(R.string.detail_retweeted_by_me)
            );
            replyToStatusRetweetedByName.setVisibility(View.VISIBLE);
        }
    }
    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            thisStatusText.setText(thisText);
            if (thisHasPicture) {
                RequestQueue queue = Volley.newRequestQueue(detailActivity);
                ImageRequest request = new ImageRequest(
                        thisPictureURL,
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap bitmap) {
                                bitmap = fixBitmap(bitmap);
                                thisStatusPicture.setImageBitmap(bitmap);
                                thisStatusPicture.setVisibility(View.VISIBLE);
                            }
                        },
                        0,
                        0,
                        Bitmap.Config.ARGB_8888,
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                /* Do nothing */
                            }
                        }
                );
                queue.add(request);
            }
            if (thisTweet.getReplyTo() != -1) {
                findView();
                replyToStatusAdapter();
            }
        }
    }
}
