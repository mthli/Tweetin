package io.github.mthli.Tweetin.Unit.Tweet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import io.github.mthli.Tweetin.Activity.DetailActivity;
import io.github.mthli.Tweetin.Database.Favorite.FavoriteRecord;
import io.github.mthli.Tweetin.Database.Mention.MentionRecord;
import io.github.mthli.Tweetin.Database.Timeline.TimelineRecord;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Anim.ActivityAnim;
import twitter4j.*;
import twitter4j.auth.AccessToken;

import java.text.SimpleDateFormat;
import java.util.List;

public class TweetUnit {

    public static Twitter getTwitterFromSharedPreferences(
            Context context
    ) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.sp_name),
                Context.MODE_PRIVATE
        );
        String consumerKey = sharedPreferences.getString(
                context.getString(R.string.sp_consumer_key),
                null
        );
        String consumerSecret = sharedPreferences.getString(
                context.getString(R.string.sp_consumer_secret),
                null
        );
        String accessToken = sharedPreferences.getString(
                context.getString(R.string.sp_access_token),
                null
        );
        String accessTokenSecret = sharedPreferences.getString(
                context.getString(R.string.sp_access_token_secret),
                null
        );
        TwitterFactory factory = new TwitterFactory();
        Twitter twitter = factory.getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        AccessToken token = new AccessToken(accessToken, accessTokenSecret);
        twitter.setOAuthAccessToken(token);

        return twitter;
    }

    public static long getUseIdFromeSharedPreferences(
            Context context
    ) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.sp_name),
                Context.MODE_PRIVATE
        );
        long useId = sharedPreferences.getLong(
                context.getString(R.string.sp_use_id),
                -1l
        );

        return useId;
    }

    public static Tweet getTweetFromIntent(
            Activity activity
    ) {
        Intent intent = activity.getIntent();
        long statusId = intent.getLongExtra(
                activity.getString(R.string.detail_intent_status_id),
                -1l
        );
        long replyToStatusId = intent.getLongExtra(
                activity.getString(R.string.detail_intent_reply_to_status_id),
                -1l
        );
        long userId = intent.getLongExtra(
                activity.getString(R.string.detail_intent_user_id),
                -1l
        );
        long retweetedByUserId = intent.getLongExtra(
                activity.getString(R.string.detail_intent_retweeted_by_user_id),
                -1l
        );
        String avatarURL = intent.getStringExtra(
                activity.getString(R.string.detail_intent_avatar_url)
        );
        String createdAt = intent.getStringExtra(
                activity.getString(R.string.detail_intent_created_at)
        );
        String name = intent.getStringExtra(
                activity.getString(R.string.detail_intent_name)
        );
        String screenName = intent.getStringExtra(
                activity.getString(R.string.detail_intent_screen_name)
        );
        boolean protect = intent.getBooleanExtra(
                activity.getString(R.string.detail_intent_protect),
                false
        );
        String checkIn = intent.getStringExtra(
                activity.getString(R.string.detail_intent_check_in)
        );
        String photoURL = intent.getStringExtra(
                activity.getString(R.string.detail_intent_photo_url)
        );
        String text = intent.getStringExtra(
                activity.getString(R.string.detail_intent_text)
        );
        boolean retweet = intent.getBooleanExtra(
                activity.getString(R.string.detail_intent_retweet),
                false
        );
        String retweetedByUserName = intent.getStringExtra(
                activity.getString(R.string.detail_intent_retweeted_by_user_name)
        );
        boolean favorite = intent.getBooleanExtra(
                activity.getString(R.string.detail_intent_favorite),
                false
        );
        Tweet tweet = new Tweet();
        tweet.setStatusId(statusId);
        tweet.setReplyToStatusId(replyToStatusId);
        tweet.setUserId(userId);
        tweet.setRetweetedByUserId(retweetedByUserId);
        tweet.setAvatarURL(avatarURL);
        tweet.setCreatedAt(createdAt);
        tweet.setName(name);
        tweet.setScreenName(screenName);
        tweet.setProtect(protect);
        tweet.setCheckIn(checkIn);
        tweet.setPhotoURL(photoURL);
        tweet.setText(text);
        tweet.setRetweet(retweet);
        tweet.setRetweetedByUserName(retweetedByUserName);
        tweet.setFavorite(favorite);

        return tweet;
    }

    public static Tweet getTweetWithDetail(
            Context context,
            twitter4j.Status status
    ) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.sp_name),
                Context.MODE_PRIVATE
        );
        long useId = sharedPreferences.getLong(
                context.getString(R.string.sp_use_id),
                -1l
        );

        URLEntity[] urlEntities;
        MediaEntity[] mediaEntities;
        SimpleDateFormat format = new SimpleDateFormat(
                context.getString(R.string.tweet_date_format)
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
            tweet.setRetweetedByUserId(useId);
            tweet.setRetweet(true);
            tweet.setRetweetedByUserName(
                    context.getString(R.string.tweet_info_retweeted_by_me)
            );
        }

        return tweet;
    }

    public static void tweetToDetailActivity(
            Activity activity,
            List<Tweet> tweetList,
            int position
    ) {
        ActivityAnim anim = new ActivityAnim();
        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtra(
                activity.getString(R.string.detail_intent_from_position),
                position
        );
        Tweet tweet = tweetList.get(position);
        intent.putExtra(
                activity.getString(R.string.detail_intent_status_id),
                tweet.getStatusId()
        );
        intent.putExtra(
                activity.getString(R.string.detail_intent_reply_to_status_id),
                tweet.getReplyToStatusId()
        );
        intent.putExtra(
                activity.getString(R.string.detail_intent_user_id),
                tweet.getUserId()
        );
        intent.putExtra(
                activity.getString(R.string.detail_intent_retweeted_by_user_id),
                tweet.getRetweetedByUserId()
        );
        intent.putExtra(
                activity.getString(R.string.detail_intent_avatar_url),
                tweet.getAvatarURL()
        );
        intent.putExtra(
                activity.getString(R.string.detail_intent_created_at),
                tweet.getCreatedAt()
        );
        intent.putExtra(
                activity.getString(R.string.detail_intent_name),
                tweet.getName()
        );
        intent.putExtra(
                activity.getString(R.string.detail_intent_screen_name),
                tweet.getScreenName()
        );
        intent.putExtra(
                activity.getString(R.string.detail_intent_protect),
                tweet.isProtect()
        );
        intent.putExtra(
                activity.getString(R.string.detail_intent_check_in),
                tweet.getCheckIn()
        );
        intent.putExtra(
                activity.getString(R.string.detail_intent_photo_url),
                tweet.getPhotoURL()
        );
        intent.putExtra(
                activity.getString(R.string.detail_intent_text),
                tweet.getText()
        );
        intent.putExtra(
                activity.getString(R.string.detail_intent_retweet),
                tweet.isRetweet()
        );
        intent.putExtra(
                activity.getString(R.string.detail_intent_retweeted_by_user_name),
                tweet.getRetweetedByUserName()
        );
        intent.putExtra(
                activity.getString(R.string.detail_intent_favorite),
                tweet.isFavorite()
        );
        activity.startActivityForResult(intent, 0);
        anim.rightIn(activity);
    }

    public static Tweet getTweetFromRecord(
            TimelineRecord record
    ) {
        Tweet tweet = new Tweet();
        tweet.setStatusId(record.getStatusId());
        tweet.setReplyToStatusId(record.getReplyToStatusId());
        tweet.setUserId(record.getUserId());
        tweet.setRetweetedByUserId(record.getRetweetedByUserId());
        tweet.setAvatarURL(record.getAvatarURL());
        tweet.setCreatedAt(record.getCreatedAt());
        tweet.setName(record.getName());
        tweet.setScreenName(record.getScreenName());
        tweet.setProtect(record.isProtect());
        tweet.setCheckIn(record.getCheckIn());
        tweet.setPhotoURL(record.getPhotoURL());
        tweet.setText(record.getText());
        tweet.setRetweet(record.isRetweet());
        tweet.setRetweetedByUserName(record.getRetweetedByUserName());
        tweet.setFavorite(record.isFavorite());

        return tweet;
    }
    public static Tweet getTweetFromRecord(
            MentionRecord record
    ) {
        Tweet tweet = new Tweet();
        tweet.setStatusId(record.getStatusId());
        tweet.setReplyToStatusId(record.getReplyToStatusId());
        tweet.setUserId(record.getUserId());
        tweet.setRetweetedByUserId(record.getRetweetedByUserId());
        tweet.setAvatarURL(record.getAvatarURL());
        tweet.setCreatedAt(record.getCreatedAt());
        tweet.setName(record.getName());
        tweet.setScreenName(record.getScreenName());
        tweet.setProtect(record.isProtect());
        tweet.setCheckIn(record.getCheckIn());
        tweet.setPhotoURL(record.getPhotoURL());
        tweet.setText(record.getText());
        tweet.setRetweet(record.isRetweet());
        tweet.setRetweetedByUserName(record.getRetweetedByUserName());
        tweet.setFavorite(record.isFavorite());

        return tweet;
    }
    public static Tweet getTweetFromRecord(
            FavoriteRecord record
    ) {
        Tweet tweet = new Tweet();
        tweet.setStatusId(record.getStatusId());
        tweet.setReplyToStatusId(record.getReplyToStatusId());
        tweet.setUserId(record.getUserId());
        tweet.setRetweetedByUserId(record.getRetweetedByUserId());
        tweet.setAvatarURL(record.getAvatarURL());
        tweet.setCreatedAt(record.getCreatedAt());
        tweet.setName(record.getName());
        tweet.setScreenName(record.getScreenName());
        tweet.setProtect(record.isProtect());
        tweet.setCheckIn(record.getCheckIn());
        tweet.setPhotoURL(record.getPhotoURL());
        tweet.setText(record.getText());
        tweet.setRetweet(record.isRetweet());
        tweet.setRetweetedByUserName(record.getRetweetedByUserName());
        tweet.setFavorite(record.isFavorite());

        return tweet;
    }

}
