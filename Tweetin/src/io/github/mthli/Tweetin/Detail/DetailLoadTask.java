package io.github.mthli.Tweetin.Detail;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.Tweet;
import twitter4j.MediaEntity;
import twitter4j.Twitter;
import twitter4j.URLEntity;

public class DetailLoadTask extends AsyncTask<Void, Integer, Boolean> {

    private DetailActivity detailActivity;
    private Twitter twitter;
    private long useId;

    private twitter4j.Status thisStatus;
    private twitter4j.Status replyToStatus;

    private Tweet thisTweet;
    private String thisText;
    private TextView thisStatusText;
    private boolean thisHasPicture;
    private String thisPictureURL;
    private ImageView thisStatusPicture;
    /* Do something with detailRetweet button */
    private Button detailRetweet;

     /*
    private CircleImageView replyToStatusAvatar;
    private TextView replyToStatusCreatedAt;
    private TextView replyToStatusName;
    private TextView replyToStatusScreenName;
    private TextView replyToStatusProtect;
    private TextView replyToStatusText;
    private ImageView replyToStatusPicture;
    private TextView replyToStatusCheckIn;
    private TextView replyToStatusRetweetedByName;
    */

    public DetailLoadTask(DetailActivity detailActivity) {
        this.detailActivity = detailActivity;
        this.useId = 0;
        this.thisPictureURL = null;
        this.thisHasPicture = false;
    }

    @Override
    protected void onPreExecute() {
        twitter = detailActivity.getTwitter();
        useId = detailActivity.getUseId();
        thisStatusText = detailActivity.getThisStatusText();
        thisStatusPicture = detailActivity.getThisStatusPicture();
        thisTweet = detailActivity.getThisTweet();
        detailRetweet = detailActivity.getDetailRetweet();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            thisStatus = twitter.showStatus(thisTweet.getTweetId());

            URLEntity[] urlEntities = thisStatus.getURLEntities();
            thisText = thisStatus.getText();
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
                        thisPictureURL = entity.getMediaURL();
                        thisHasPicture = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            /* Do something */
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
            thisStatusText.setText(thisText);
            if (thisHasPicture) {
                Glide.with(detailActivity).load(thisPictureURL)
                        .crossFade().into(thisStatusPicture);
                thisStatusPicture.setVisibility(View.VISIBLE);
            }
            /* Do something */
        } else {
            /* Do something */
        }
    }

    private void replyToStatusAdapter() {
        /* Do something */
    }
}
