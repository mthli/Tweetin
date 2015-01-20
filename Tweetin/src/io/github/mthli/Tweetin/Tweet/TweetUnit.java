package io.github.mthli.Tweetin.Tweet;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.widget.Toast;
import com.twitter.Extractor;
import io.github.mthli.Tweetin.Data.DataRecord;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Twitter.TwitterUnit;
import twitter4j.*;

import java.util.List;

public class TweetUnit {
    private Context context;

    private String useScreenName;
    private String me;

    public TweetUnit(Context context) {
        this.context = context;

        this.useScreenName = TwitterUnit.getUseScreenNameFromSharedPreferences(context);
        this.me = context.getString(R.string.tweet_info_retweeted_by_me);
    }

    public String getPictureURLFromStatus(Status status) {
        URLEntity[] urlEntities;
        MediaEntity[] mediaEntities;

        if (status.isRetweet()) {
            urlEntities = status.getRetweetedStatus().getURLEntities();
            mediaEntities = status.getRetweetedStatus().getMediaEntities();
        } else {
            urlEntities = status.getURLEntities();
            mediaEntities = status.getMediaEntities();
        }

        /* Support for *.png and *.jpg */
        for (MediaEntity mediaEntity : mediaEntities) {
            if (mediaEntity.getType().equals(context.getString(R.string.picture_media_type))) {
                return mediaEntity.getMediaURL();
            }
        }

        /* Support for Instagram */
        for (URLEntity urlEntity : urlEntities) {
            String expandedURL = urlEntity.getExpandedURL();
            if (expandedURL.startsWith(context.getString(R.string.picture_instagram_prefix))) {
                return expandedURL + context.getString(R.string.picture_instagram_suffix);
            }
        }

        return null;
    }

    public String getDetailTextFromStatus(Status status) {
        URLEntity[] urlEntities;
        MediaEntity[] mediaEntities;

        String text;

        if (status.isRetweet()) {
            urlEntities = status.getRetweetedStatus().getURLEntities();
            mediaEntities = status.getRetweetedStatus().getMediaEntities();
            text = status.getRetweetedStatus().getText();
        } else {
            urlEntities = status.getURLEntities();
            mediaEntities = status.getMediaEntities();
            text = status.getText();
        }

        for (URLEntity urlEntity : urlEntities) {
            text = text.replace(
                    urlEntity.getURL(),
                    urlEntity.getExpandedURL()
            );
        }

        for (MediaEntity mediaEntity : mediaEntities) {
            text = text.replace(
                    mediaEntity.getURL(),
                    mediaEntity.getMediaURL()
            );
        }

        return text;
    }

    public SpannableString getSpanFromText(String text) {
        Extractor extractor = new Extractor();
        List<String> urlList = extractor.extractURLs(text);
        List<String> userList = extractor.extractMentionedScreennames(text);
        List<String> tagList = extractor.extractHashtags(text);

        SpannableString span = new SpannableString(text);

        for (String url : urlList) {
            span.setSpan(
                    new TweetURLSpan(context, url),
                    text.indexOf(url),
                    text.indexOf(url) + url.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        for (String user : userList) {
            span.setSpan(
                    new TweetUserSpan(context, user),
                    text.indexOf(user),
                    text.indexOf(user) + user.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        for (String tag : tagList) {
            span.setSpan(
                    new TweetTagSpan(context, tag),
                    text.indexOf(tag),
                    text.indexOf(tag) + tag.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        return span;
    }

    public SpannableString getSpanFromTweet(Tweet tweet) {
        return getSpanFromText(tweet.getText());
    }

    public Tweet getTweetFromStatus(Status status) {
        Tweet tweet = new Tweet();

        if (status.isRetweet()) {
            tweet.setAvatarURL(status.getRetweetedStatus().getUser().getOriginalProfileImageURL());
            tweet.setName(status.getRetweetedStatus().getUser().getName());
            tweet.setScreenName(status.getRetweetedStatus().getUser().getScreenName());
            tweet.setCreatedAt(status.getRetweetedStatus().getCreatedAt().getTime());
            Place place = status.getRetweetedStatus().getPlace();
            if (place != null) {
                tweet.setCheckIn(place.getFullName());
            } else {
                tweet.setCheckIn(null);
            }
            tweet.setProtect(status.getRetweetedStatus().getUser().isProtected());
            tweet.setPictureURL(getPictureURLFromStatus(status.getRetweetedStatus()));
            tweet.setText(getDetailTextFromStatus(status.getRetweetedStatus()));
            tweet.setRetweetedByName(status.getUser().getName());
            tweet.setFavorite(status.getRetweetedStatus().isFavorited());

            tweet.setStatusId(status.getRetweetedStatus().getId());
            tweet.setInReplyToStatusId(status.getRetweetedStatus().getInReplyToStatusId());
            tweet.setRetweetedByScreenName(status.getUser().getScreenName());
        } else {
            tweet.setAvatarURL(status.getUser().getOriginalProfileImageURL());
            tweet.setName(status.getUser().getName());
            tweet.setScreenName(status.getUser().getScreenName());
            tweet.setCreatedAt(status.getCreatedAt().getTime());
            Place place = status.getPlace();
            if (place != null) {
                tweet.setCheckIn(place.getFullName());
            } else {
                tweet.setCheckIn(null);
            }
            tweet.setProtect(status.getUser().isProtected());
            tweet.setPictureURL(getPictureURLFromStatus(status));
            tweet.setText(getDetailTextFromStatus(status));
            tweet.setRetweetedByName(null);
            tweet.setFavorite(status.isFavorited());

            tweet.setStatusId(status.getId());
            tweet.setInReplyToStatusId(status.getInReplyToStatusId());
            tweet.setRetweetedByScreenName(null);
        }

        if (status.isRetweetedByMe() || status.isRetweeted()) {
            tweet.setRetweetedByName(me);
            tweet.setRetweetedByScreenName(useScreenName);
        }

        tweet.setDetail(false);

        return tweet;
    }

    public Tweet getTweetFromDataRecord(DataRecord record) {
        Tweet tweet = new Tweet();

        tweet.setAvatarURL(record.getAvatarURL());
        tweet.setName(record.getName());
        tweet.setScreenName(record.getScreenName());
        tweet.setCreatedAt(record.getCreatedAt());
        tweet.setCheckIn(record.getCheckIn());
        tweet.setProtect(record.isProtect());
        tweet.setPictureURL(record.getPictureURL());
        tweet.setText(record.getText());
        tweet.setRetweetedByName(record.getRetweetedByName());
        tweet.setFavorite(record.isFavorite());

        tweet.setStatusId(record.getStatusId());
        tweet.setInReplyToStatusId(record.getInReplyToStatusId());
        tweet.setRetweetedByScreenName(record.getRetweetedByScreenName());

        tweet.setDetail(false);

        return tweet;
    }

    public DataRecord getDataRecordFromTweet(Tweet tweet) {
        DataRecord record = new DataRecord();

        record.setAvatarURL(tweet.getAvatarURL());
        record.setName(tweet.getName());
        record.setScreenName(tweet.getScreenName());
        record.setCreatedAt(tweet.getCreatedAt());
        record.setCheckIn(tweet.getCheckIn());
        record.setProtect(tweet.isProtect());
        record.setPictureURL(tweet.getPictureURL());
        record.setText(tweet.getText());
        record.setRetweetedByName(tweet.getRetweetedByName());
        record.setFavorite(tweet.isFavorite());

        record.setStatusId(tweet.getStatusId());
        record.setInReplyToStatusId(tweet.getInReplyToStatusId());
        record.setRetweetedByScreenName(tweet.getRetweetedByScreenName());

        return record;
    }

    public DataRecord getDataRecordFromStatus(Status status) {
        DataRecord record = new DataRecord();

        if (status.isRetweet()) {
            record.setAvatarURL(status.getRetweetedStatus().getUser().getOriginalProfileImageURL());
            record.setName(status.getRetweetedStatus().getUser().getName());
            record.setScreenName(status.getRetweetedStatus().getUser().getScreenName());
            record.setCreatedAt(status.getRetweetedStatus().getCreatedAt().getTime());
            Place place = status.getRetweetedStatus().getPlace();
            if (place != null) {
                record.setCheckIn(place.getFullName());
            } else {
                record.setCheckIn(null);
            }
            record.setProtect(status.getRetweetedStatus().getUser().isProtected());
            record.setPictureURL(getPictureURLFromStatus(status.getRetweetedStatus()));
            record.setText(getDetailTextFromStatus(status.getRetweetedStatus()));
            record.setRetweetedByName(status.getUser().getName());
            record.setFavorite(status.getRetweetedStatus().isFavorited());

            record.setStatusId(status.getRetweetedStatus().getId());
            record.setInReplyToStatusId(status.getRetweetedStatus().getInReplyToStatusId());
            record.setRetweetedByScreenName(status.getUser().getScreenName());
        } else {
            record.setAvatarURL(status.getUser().getOriginalProfileImageURL());
            record.setName(status.getUser().getName());
            record.setScreenName(status.getUser().getScreenName());
            record.setCreatedAt(status.getCreatedAt().getTime());
            Place place = status.getPlace();
            if (place != null) {
                record.setCheckIn(place.getFullName());
            } else {
                record.setCheckIn(null);
            }
            record.setProtect(status.getUser().isProtected());
            record.setPictureURL(getPictureURLFromStatus(status));
            record.setText(getDetailTextFromStatus(status));
            record.setRetweetedByName(null);
            record.setFavorite(status.isFavorited());

            record.setStatusId(status.getId());
            record.setInReplyToStatusId(status.getInReplyToStatusId());
            record.setRetweetedByScreenName(null);
        }

        if (status.isRetweetedByMe() || status.isRetweeted()) {
            record.setRetweetedByName(me);
            record.setRetweetedByScreenName(useScreenName);
        }

        return record;
    }

    public Intent getIntentFromTweet(Tweet tweet, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(context.getString(R.string.tweet_intent_avatar_url), tweet.getAvatarURL());
        intent.putExtra(context.getString(R.string.tweet_intent_name), tweet.getName());
        intent.putExtra(context.getString(R.string.tweet_intent_screen_name), tweet.getScreenName());
        intent.putExtra(context.getString(R.string.tweet_intent_created_at), tweet.getCreatedAt());
        intent.putExtra(context.getString(R.string.tweet_intent_check_in), tweet.getCheckIn());
        intent.putExtra(context.getString(R.string.tweet_intent_protect), tweet.isProtect());
        intent.putExtra(context.getString(R.string.tweet_intent_picture_url), tweet.getPictureURL());
        intent.putExtra(context.getString(R.string.tweet_intent_text), tweet.getText());
        intent.putExtra(context.getString(R.string.tweet_intent_retweeted_by_name), tweet.getRetweetedByName());
        intent.putExtra(context.getString(R.string.tweet_intent_favorite), tweet.isFavorite());
        intent.putExtra(context.getString(R.string.tweet_intent_status_id), tweet.getStatusId());
        intent.putExtra(context.getString(R.string.tweet_intent_in_reply_to_status_id), tweet.getInReplyToStatusId());
        intent.putExtra(context.getString(R.string.tweet_intent_retweeted_by_screen_name), tweet.getRetweetedByScreenName());
        return intent;
    }

    public Tweet getTweetFromIntent(Intent intent) {
        Tweet tweet = new Tweet();
        tweet.setAvatarURL(intent.getStringExtra(context.getString(R.string.tweet_intent_avatar_url)));
        tweet.setName(intent.getStringExtra(context.getString(R.string.tweet_intent_name)));
        tweet.setScreenName(intent.getStringExtra(context.getString(R.string.tweet_intent_screen_name)));
        tweet.setCreatedAt(intent.getLongExtra(context.getString(R.string.tweet_intent_created_at), 0));
        tweet.setCheckIn(intent.getStringExtra(context.getString(R.string.tweet_intent_check_in)));
        tweet.setProtect(intent.getBooleanExtra(context.getString(R.string.tweet_intent_protect), false));
        tweet.setPictureURL(intent.getStringExtra(context.getString(R.string.tweet_intent_picture_url)));
        tweet.setText(intent.getStringExtra(context.getString(R.string.tweet_intent_text)));
        tweet.setRetweetedByName(intent.getStringExtra(context.getString(R.string.tweet_intent_retweeted_by_name)));
        tweet.setFavorite(intent.getBooleanExtra(context.getString(R.string.tweet_intent_favorite), false));
        tweet.setStatusId(intent.getLongExtra(context.getString(R.string.tweet_intent_status_id), -1));
        tweet.setInReplyToStatusId(intent.getLongExtra(context.getString(R.string.tweet_intent_in_reply_to_status_id), -1));
        tweet.setRetweetedByScreenName(intent.getStringExtra(context.getString(R.string.tweet_intent_retweeted_by_screen_name)));
        return tweet;
    }

    public void share(Tweet tweet) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "@" + tweet.getScreenName() + ": " + tweet.getText());
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.tweet_share_label)));
    }

    public void copy(Tweet tweet) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(context.getString(R.string.tweet_copy_label), tweet.getText());
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(context, R.string.tweet_toast_copy_successful, Toast.LENGTH_SHORT).show();
    }
}
