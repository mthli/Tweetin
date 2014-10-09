package io.github.mthli.Tweetin.Detail;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.mthli.Tweetin.R;
import twitter4j.GeoLocation;
import twitter4j.Place;
import twitter4j.Twitter;

import java.text.SimpleDateFormat;

public class DetailTask extends AsyncTask<Void, Integer, Boolean> {
    private DetailFragment detailFragment;
    private View view;
    private Context context;

    private Twitter twitter;
    private long useId;
    private long tweetId;
    private long userId;
    private long replyTo;

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

    private CircleImageView thisStatusAvatar;
    private TextView thisStatusCreatedAt;
    private TextView thisStatusName;
    private TextView thisStatusScreenName;
    private TextView thisStatusProtect;
    private TextView thisStatusText;
    private ImageView thisStatusPicture;
    private TextView thisStatusCheckIn;
    private TextView thisStatusRetweetedByName;

    private Button detailRetweet;

    public DetailTask(DetailFragment detailFragment) {
        this.detailFragment = detailFragment;
    }

    private void init() {
        /* detail_replay_to_status
        replyToStatusAvatar = (CircleImageView) view.findViewById(
                R.id.detail_reply_to_status_avatar
        );
        replyToStatusCreatedAt = (TextView) view.findViewById(
                R.id.detail_reply_to_status_created_at
        );
        replyToStatusName = (TextView) view.findViewById(
                R.id.detail_reply_to_status_name
        );
        replyToStatusScreenName = (TextView) view.findViewById(
                R.id.detail_reply_to_status_screen_name
        );
        replyToStatusProtect = (TextView) view.findViewById(
                R.id.detail_reply_to_status_protect
        );
        replyToStatusText = (TextView) view.findViewById(
                R.id.detail_reply_to_status_text
        );
        replyToStatusPicture = (ImageView) view.findViewById(
                R.id.detail_reply_to_status_picture
        );
        replyToStatusCheckIn = (TextView) view.findViewById(
                R.id.detail_reply_to_status_check_in
        );
        replyToStatusRetweetedByName = (TextView) view.findViewById(
                R.id.detail_reply_to_status_retweeted_by_name
        );
        */

        /* detail_this_status */
        thisStatusAvatar = (CircleImageView) view.findViewById(
                R.id.detail_this_status_avatar
        );
        thisStatusCreatedAt = (TextView) view.findViewById(
                R.id.detail_this_status_created_at
        );
        thisStatusName = (TextView) view.findViewById(
                R.id.detail_this_status_name
        );
        thisStatusScreenName = (TextView) view.findViewById(
                R.id.detail_this_status_screen_name
        );
        thisStatusProtect = (TextView) view.findViewById(
                R.id.detail_this_status_protect
        );
        thisStatusText = (TextView) view.findViewById(
                R.id.detail_this_status_text
        );
        thisStatusPicture = (ImageView) view.findViewById(
                R.id.detail_this_status_picture
        );
        thisStatusCheckIn = (TextView) view.findViewById(
                R.id.detail_this_status_check_in
        );
        thisStatusRetweetedByName = (TextView) view.findViewById(
                R.id.detail_this_status_retweeted_by_name
        );

        /* detail_option */
        detailRetweet = (Button) view.findViewById(
                R.id.detail_retweet
        );
    }
    @Override
    protected void onPreExecute() {
        view = detailFragment.getContentView();
        context = detailFragment.getContentView().getContext();
        init();

        twitter = ((DetailActivity) detailFragment.getActivity())
                .getTwitter();
        useId = ((DetailActivity) detailFragment.getActivity())
                .getUseId();
        userId = detailFragment.getUserId();
        tweetId = detailFragment.getTweetId();
        replyTo = detailFragment.getReplyTo();

        if (replyTo == -1) {
            /* Do something */
        }
        detailFragment.setContentShown(false);
    }

    private twitter4j.Status replyToStatus;
    private twitter4j.Status thisStatus;
    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            if (replyTo != -1) {
                replyToStatus = twitter.showStatus(replyTo);
            }
            thisStatus = twitter.showStatus(tweetId);
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

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            if (replyTo == -1) {
                /* Do something */
            } else {
                /* Do something */
            }

            thisStatusAdapter();

            detailFragment.setContentEmpty(false);
            detailFragment.setContentShown(true);
        } else {
            detailFragment.setContentEmpty(true);
            detailFragment.setEmptyText(R.string.detail_get_tweets_failed);
            detailFragment.setContentShown(true);
        }
    }

    private void replyToStatusAdapter() {
        /* Do something */
    }

    private void thisStatusAdapter() {
        thisStatusAvatar.setVisibility(View.VISIBLE);
        thisStatusCreatedAt.setVisibility(View.VISIBLE);
        thisStatusName.setVisibility(View.VISIBLE);
        thisStatusScreenName.setVisibility(View.VISIBLE);
        thisStatusText.setVisibility(View.VISIBLE);

        /* Do something with today */
        SimpleDateFormat format = new SimpleDateFormat(
                context.getString(R.string.tweet_date_format)
        );
        if (thisStatus.isRetweet()) {
            String avatarUrl = thisStatus.getRetweetedStatus()
                    .getUser().getBiggerProfileImageURL();
            Glide.with(context)
                    .load(avatarUrl)
                    .crossFade()
                    .into(thisStatusAvatar);
            String createdAt = format.format(
                    thisStatus.getRetweetedStatus().getCreatedAt()
            );
            thisStatusCreatedAt.setText(createdAt);
            thisStatusName.setText(
                    thisStatus.getRetweetedStatus().getUser().getName()
            );
            thisStatusScreenName.setText(
                    "@" + thisStatus.getRetweetedStatus().getUser().getScreenName()
            );
            if (thisStatus.getRetweetedStatus().getUser().isProtected()) {
                thisStatusProtect.setVisibility(View.VISIBLE);
            } else {
                thisStatusProtect.setVisibility(View.GONE);
            }
            thisStatusText.setText(
                    thisStatus.getRetweetedStatus().getText()
            );

            /* Do something with Picture */

            /* Do something with Ckeck In */
            Place place = thisStatus.getPlace();
            if (place != null) {
                thisStatusCheckIn.setVisibility(View.VISIBLE);
                thisStatusCheckIn.setText(
                        thisStatus.getPlace().getFullName()
                );
            } else {
                thisStatusCheckIn.setVisibility(View.GONE);
            }

            thisStatusRetweetedByName.setVisibility(View.VISIBLE);
            thisStatusRetweetedByName.setText(
                    thisStatus.getUser().getName()
            );
        } else {
            String avatarUrl = thisStatus.getUser()
                    .getBiggerProfileImageURL();
            Glide.with(context)
                    .load(avatarUrl)
                    .crossFade()
                    .into(thisStatusAvatar);

            String createdAt = format.format(
                    thisStatus.getCreatedAt()
            );
            thisStatusCreatedAt.setText(createdAt);

            thisStatusName.setText(
                    thisStatus.getUser().getName()
            );
            thisStatusScreenName.setText(
                    "@" + thisStatus.getUser().getScreenName()
            );

            if (thisStatus.getUser().isProtected()) {
                thisStatusProtect.setVisibility(View.VISIBLE);
            } else {
                thisStatusProtect.setVisibility(View.GONE);
            }

            thisStatusText.setText(
                    thisStatus.getText()
            );

            /* Do something with Picture */

            /* Do something with Ckeck In */
            Place place = thisStatus.getPlace();
            if (place != null) {
                thisStatusCheckIn.setVisibility(View.VISIBLE);
                thisStatusCheckIn.setText(
                        thisStatus.getPlace().getFullName()
                );
            } else {
                thisStatusCheckIn.setVisibility(View.GONE);
            }
            thisStatusRetweetedByName.setVisibility(View.GONE);
        }
        if (thisStatus.isRetweetedByMe()) {
            thisStatusRetweetedByName.setVisibility(View.VISIBLE);
            thisStatusRetweetedByName.setText(
                    context.getString(R.string.tweet_retweeted_by_me)
            );
            /* Do something with button */
        }
        if (userId == useId) {
            /* Do something with button */
        }
    }

}
