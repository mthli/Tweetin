package io.github.mthli.Tweetin.Task.Detail;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;
import io.github.mthli.Tweetin.Activity.DetailActivity;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import twitter4j.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DetailInitTask extends AsyncTask<Void, Integer, Boolean> {

    private DetailActivity detailActivity;
    private Twitter twitter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList = new ArrayList<Tweet>();
    private twitter4j.Status currentStatus;
    private Tweet currentTweet = new Tweet();

    public DetailInitTask(DetailActivity detailActivity) {
        this.detailActivity = detailActivity;
    }

    @Override
    protected void onPreExecute() {
        if (detailActivity.getRefreshFlag() == Flag.DETAIL_TASK_RUNNING) {
            onCancelled();
        } else {
            detailActivity.setRefreshFlag(Flag.DETAIL_TASK_RUNNING);
        }

        twitter = detailActivity.getTwitter();

        swipeRefreshLayout = detailActivity.getSwipeRefreshLayout();

        tweetAdapter = detailActivity.getTweetAdapter();
        tweetList = detailActivity.getTweetList();
        currentTweet = detailActivity.getTweetFromIntent();

        swipeRefreshLayout.setRefreshing(true);
    }


    private List<twitter4j.Status> getReplyToStatusList(long replyToStatusId) {
        List<twitter4j.Status> statusList = new ArrayList<twitter4j.Status>();
        while (replyToStatusId > 0) {
            try {
                twitter4j.Status status = twitter.showStatus(replyToStatusId);
                statusList.add(status);
                replyToStatusId = status.getInReplyToStatusId();
                if (isCancelled()) {
                    return statusList;
                }
            } catch (Exception e) {
                return statusList;
            }
        }
        Collections.reverse(statusList);

        return statusList;
    }
    private List<twitter4j.Status> replyToStatusList = new ArrayList<twitter4j.Status>();
    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            if (currentTweet.getReplyToStatusId() > 0) {
                replyToStatusList = getReplyToStatusList(currentTweet.getReplyToStatusId());
            }
            if (isCancelled()) {
                return false;
            }

            currentStatus = twitter.showStatus(currentTweet.getStatusId());
            if (isCancelled()) {
                return false;
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

    private Tweet getTweetWithDetails(twitter4j.Status status) {
        URLEntity[] urlEntities;
        MediaEntity[] mediaEntities;
        SimpleDateFormat format = new SimpleDateFormat(
                detailActivity.getString(R.string.tweet_date_format)
        );
        Tweet tweet = new Tweet();
        if (status.isRetweet()) {
            tweet.setStatusId(status.getId());
            tweet.setReplyToStatusId(
                    status.getRetweetedStatus().getInReplyToStatusId()
            );
            tweet.setUserId(
                    status.getRetweetedStatus().getUser().getId()
            );
            tweet.setRetweetedByUserId(status.getUser().getId());
            tweet.setAvatarURL(
                    status.getRetweetedStatus().getUser().getBiggerProfileImageURL()
            );
            tweet.setCreatedAt(
                    format.format(status.getRetweetedStatus().getCreatedAt())
            );
            tweet.setName(
                    status.getRetweetedStatus().getUser().getName()
            );
            tweet.setScreenName(
                    "@" + status.getRetweetedStatus().getUser().getScreenName()
            );
            tweet.setProtect(
                    status.getRetweetedStatus().getUser().isProtected()
            );
            Place place = status.getRetweetedStatus().getPlace();
            if (place != null) {
                tweet.setCheckIn(place.getFullName());
            } else {
                tweet.setCheckIn(null);
            }

            /* Do something */
            urlEntities = status.getRetweetedStatus().getURLEntities();
            mediaEntities = status.getRetweetedStatus().getMediaEntities();
            String photoURL = null;
            String text = status.getRetweetedStatus().getText();
            if (urlEntities.length > 0) {
                for (URLEntity urlEntity : urlEntities) {
                    text = text.replace(
                            urlEntity.getURL(),
                            urlEntity.getExpandedURL()
                    );
                }
            }
            if (mediaEntities.length > 0) {
                for (MediaEntity mediaEntity : mediaEntities) {
                    text = text.replace(
                            mediaEntity.getURL(),
                            mediaEntity.getMediaURL()
                    );
                    if (mediaEntity.getType().equals("photo")) {
                        photoURL = mediaEntity.getMediaURL();
                        break;
                    }
                }
            }
            tweet.setPhotoURL(photoURL);
            tweet.setText(text);

            tweet.setRetweet(true);
            tweet.setRetweetedByUserName(
                    status.getUser().getName()
            );
            tweet.setFavorite(status.getRetweetedStatus().isFavorited());
        } else {
            tweet.setStatusId(status.getId());
            tweet.setReplyToStatusId(status.getInReplyToStatusId());
            tweet.setUserId(status.getUser().getId());
            tweet.setRetweetedByUserId(-1l);
            tweet.setAvatarURL(status.getUser().getBiggerProfileImageURL());
            tweet.setCreatedAt(
                    format.format(status.getCreatedAt())
            );
            tweet.setName(status.getUser().getName());
            tweet.setScreenName("@" + status.getUser().getScreenName());
            tweet.setProtect(status.getUser().isProtected());
            Place place = status.getPlace();
            if (place != null) {
                tweet.setCheckIn(place.getFullName());
            } else {
                tweet.setCheckIn(null);
            }

            /* Do something */
            urlEntities = status.getURLEntities();
            mediaEntities = status.getMediaEntities();
            String photoURL = null;
            String text = status.getText();
            if (urlEntities.length > 0) {
                for (URLEntity urlEntity : urlEntities) {
                    text = text.replace(
                            urlEntity.getURL(),
                            urlEntity.getExpandedURL()
                    );
                }
            }
            if (mediaEntities.length > 0) {
                for (MediaEntity mediaEntity : mediaEntities) {
                    text = text.replace(
                            mediaEntity.getURL(),
                            mediaEntity.getMediaURL()
                    );
                    if (mediaEntity.getType().equals("photo")) {
                        photoURL = mediaEntity.getMediaURL();
                        break;
                    }
                }
            }
            tweet.setPhotoURL(photoURL);
            tweet.setText(text);

            tweet.setRetweet(false);
            tweet.setRetweetedByUserName(null);
            tweet.setFavorite(status.isFavorited());
        }
        if (status.isRetweetedByMe() || status.isRetweeted()) {
            tweet.setRetweetedByUserId(detailActivity.getUseId());
            tweet.setRetweet(true);
            tweet.setRetweetedByUserName(
                    detailActivity.getString(R.string.tweet_info_retweeted_by_me)
            );
        }

        return tweet;
    }
    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {

            if (detailActivity.getIntent().getBooleanExtra(
                    detailActivity.getString(R.string.detail_intent_from_notification),
                    false
            )) {
                SharedPreferences sharedPreferences = detailActivity.getSharedPreferences();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(
                        detailActivity.getString(R.string.sp_latest_mention_id),
                        detailActivity.getTweetFromIntent().getStatusId()
                ).commit();
            }

            currentTweet = getTweetWithDetails(currentStatus);
            tweetList.clear();
            if (replyToStatusList.size() > 0) {
                for (twitter4j.Status status : replyToStatusList) {
                    tweetList.add(getTweetWithDetails(status));
                }
            }
            tweetList.add(currentTweet);
            tweetAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(
                    detailActivity,
                    R.string.detail_error_get_detail_failed,
                    Toast.LENGTH_SHORT
            ).show();
        }
        swipeRefreshLayout.setRefreshing(false);
        detailActivity.setRefreshFlag(Flag.DETAIL_TASK_IDLE);
    }
}
