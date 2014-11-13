package io.github.mthli.Tweetin.Task.Timeline;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.SwipeRefreshLayout;
import io.github.mthli.Tweetin.Activity.DetailActivity;
import io.github.mthli.Tweetin.Database.Timeline.TimelineAction;
import io.github.mthli.Tweetin.Database.Timeline.TimelineRecord;
import io.github.mthli.Tweetin.Fragment.TimelineFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import io.github.mthli.Tweetin.Unit.Tweet.TweetUnit;
import twitter4j.Paging;
import twitter4j.Place;
import twitter4j.Twitter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TimelineInitTask extends AsyncTask<Void, Integer, Boolean> {
    private TimelineFragment timelineFragment;
    private Context context;
    private Twitter twitter;
    private long useId;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private List<TimelineRecord> timelineRecordList = new ArrayList<TimelineRecord>();

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean firstSignIn;
    private boolean pullToRefresh;

    public TimelineInitTask(
            TimelineFragment timelineFragment,
            boolean pullToRefresh
    ) {
        this.timelineFragment = timelineFragment;
        this.pullToRefresh = pullToRefresh;
    }

    @Override
    protected void onPreExecute() {
        if (timelineFragment.getRefreshFlag() == Flag.TIMELINE_TASK_RUNNING) {
            onCancelled();
        } else {
            timelineFragment.setRefreshFlag(Flag.TIMELINE_TASK_RUNNING);
        }

        context = timelineFragment.getContentView().getContext();
        twitter = timelineFragment.getTwitter();
        useId = timelineFragment.getUseId();

        tweetAdapter = timelineFragment.getTweetAdapter();
        tweetList = timelineFragment.getTweetList();

        sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.sp_name),
                Context.MODE_PRIVATE
        );
        editor = sharedPreferences.edit();
        swipeRefreshLayout = timelineFragment.getSwipeRefreshLayout();

        if (
                sharedPreferences.getBoolean(
                        context.getString(R.string.sp_is_timeline_first),
                        false
                )
        ) {
            firstSignIn = true;
            timelineFragment.setContentShown(false);
        } else {
            firstSignIn = false;
            if (!swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
        }

        if (!pullToRefresh) {
            TimelineAction action = new TimelineAction(context);
            action.openDatabase(false);
            timelineRecordList = action.getTimelineRecordList();
            action.closeDatabase();
            tweetList.clear();
            for (TimelineRecord record : timelineRecordList) {
                Tweet tweet = TweetUnit.getTweetFromRecord(record);
                tweetList.add(tweet);
            }
            tweetAdapter.notifyDataSetChanged();
        }
    }

    private twitter4j.Status mention;
    @Override
    protected Boolean doInBackground(Void... params) {
        List<twitter4j.Status> statusList;
        try {
            Paging paging = new Paging(1, 40);
            statusList = twitter.getHomeTimeline(paging);
            paging = new Paging(1, 1);
            List<twitter4j.Status> list = twitter.getMentionsTimeline(paging);
            mention = list.get(0);
        } catch (Exception e) {
            return false;
        }
        if (isCancelled()) {
            return false;
        }

        TimelineAction action = new TimelineAction(context);
        action.openDatabase(true);
        action.deleteAll();
        timelineRecordList.clear();
        SimpleDateFormat format = new SimpleDateFormat(
                context.getString(R.string.tweet_date_format)
        );
        for (twitter4j.Status status : statusList) {
            TimelineRecord record = new TimelineRecord();
            if (status.isRetweet()) {
                record.setStatusId(status.getId());
                record.setReplyToStatusId(
                        status.getRetweetedStatus().getInReplyToStatusId()
                );
                record.setUserId(
                        status.getRetweetedStatus().getUser().getId()
                );
                record.setRetweetedByUserId(status.getUser().getId());
                record.setAvatarURL(
                        status.getRetweetedStatus().getUser().getBiggerProfileImageURL()
                );
                record.setCreatedAt(
                        format.format(status.getRetweetedStatus().getCreatedAt())
                );
                record.setName(
                        status.getRetweetedStatus().getUser().getName()
                );
                record.setScreenName(
                        "@" + status.getRetweetedStatus().getUser().getScreenName()
                );
                record.setProtect(
                        status.getRetweetedStatus().getUser().isProtected()
                );
                Place place = status.getRetweetedStatus().getPlace();
                if (place != null) {
                    record.setCheckIn(place.getFullName());
                } else {
                    record.setCheckIn(null);
                }
                /* Do something with photoURL */
                record.setPhotoURL(null);
                record.setText(
                        status.getRetweetedStatus().getText()
                );
                record.setRetweet(true);
                record.setRetweetedByUserName(
                        status.getUser().getName()
                );
                record.setFavorite(status.getRetweetedStatus().isFavorited());
            } else {
                record.setStatusId(status.getId());
                record.setReplyToStatusId(status.getInReplyToStatusId());
                record.setUserId(status.getUser().getId());
                record.setRetweetedByUserId(-1l);
                record.setAvatarURL(status.getUser().getBiggerProfileImageURL());
                record.setCreatedAt(
                        format.format(status.getCreatedAt())
                );
                record.setName(status.getUser().getName());
                record.setScreenName("@" + status.getUser().getScreenName());
                record.setProtect(status.getUser().isProtected());
                Place place = status.getPlace();
                if (place != null) {
                    record.setCheckIn(place.getFullName());
                } else {
                    record.setCheckIn(null);
                }
                /* Do something with photoURL */
                record.setPhotoURL(null);
                record.setText(status.getText());
                record.setRetweet(false);
                record.setRetweetedByUserName(null);
                record.setFavorite(status.isFavorited());
            }
            if (status.isRetweetedByMe() || status.isRetweeted()) {
                record.setRetweetedByUserId(useId);
                record.setRetweet(true);
                record.setRetweetedByUserName(
                        context.getString(R.string.tweet_info_retweeted_by_me)
                );
            }
            action.addRecord(record);
            timelineRecordList.add(record);
        }
        action.closeDatabase();

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

    private Intent getMentionToDetailIntent() {
        SimpleDateFormat format = new SimpleDateFormat(
                context.getString(R.string.tweet_date_format)
        );
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(
                context.getString(R.string.detail_intent_from_position),
                -1
        );
        if (mention.isRetweet()) {
            intent.putExtra(
                    context.getString(R.string.detail_intent_status_id),
                    mention.getId()
            );
            intent.putExtra(
                    context.getString(R.string.detail_intent_reply_to_status_id),
                    mention.getRetweetedStatus().getInReplyToStatusId()
            );
            intent.putExtra(
                    context.getString(R.string.detail_intent_user_id),
                    mention.getRetweetedStatus().getUser().getId()
            );
            intent.putExtra(
                    context.getString(R.string.detail_intent_retweeted_by_user_id),
                    mention.getUser().getId()
            );
            intent.putExtra(
                    context.getString(R.string.detail_intent_avatar_url),
                    mention.getRetweetedStatus().getUser().getBiggerProfileImageURL()
            );
            intent.putExtra(
                    context.getString(R.string.detail_intent_created_at),
                    format.format(mention.getRetweetedStatus().getCreatedAt())
            );
            intent.putExtra(
                    context.getString(R.string.detail_intent_name),
                    mention.getRetweetedStatus().getUser().getName()
            );
            intent.putExtra(
                    context.getString(R.string.detail_intent_screen_name),
                    "@" + mention.getRetweetedStatus().getUser().getScreenName()
            );
            intent.putExtra(
                    context.getString(R.string.detail_intent_protect),
                    mention.getRetweetedStatus().getUser().isProtected()
            );
            Place place = mention.getRetweetedStatus().getPlace();
            if (place != null) {
                intent.putExtra(
                        context.getString(R.string.detail_intent_check_in),
                        place.getFullName()
                );
            } else {
                intent.putExtra(
                        context.getString(R.string.detail_intent_check_in),
                        (String) null
                );
            }
            /* Do something with photoURL */
            intent.putExtra(
                    context.getString(R.string.detail_intent_photo_url),
                    (String) null
            );
            intent.putExtra(
                    context.getString(R.string.detail_intent_text),
                    mention.getRetweetedStatus().getText()
            );
            intent.putExtra(
                    context.getString(R.string.detail_intent_retweet),
                    true
            );
            intent.putExtra(
                    context.getString(R.string.detail_intent_retweeted_by_user_name),
                    mention.getUser().getName()
            );
            intent.putExtra(
                    context.getString(R.string.detail_intent_favorite),
                    mention.getRetweetedStatus().isFavorited()
            );
        } else {
            intent.putExtra(
                    context.getString(R.string.detail_intent_status_id),
                    mention.getId()
            );
            intent.putExtra(
                    context.getString(R.string.detail_intent_reply_to_status_id),
                    mention.getInReplyToStatusId()
            );
            intent.putExtra(
                    context.getString(R.string.detail_intent_user_id),
                    mention.getUser().getId()
            );
            intent.putExtra(
                    context.getString(R.string.detail_intent_retweeted_by_user_id),
                    -1l
            );
            intent.putExtra(
                    context.getString(R.string.detail_intent_avatar_url),
                    mention.getUser().getBiggerProfileImageURL()
            );
            intent.putExtra(
                    context.getString(R.string.detail_intent_created_at),
                    format.format(mention.getCreatedAt())
            );
            intent.putExtra(
                    context.getString(R.string.detail_intent_name),
                    mention.getUser().getName()
            );
            intent.putExtra(
                    context.getString(R.string.detail_intent_screen_name),
                    "@" + mention.getUser().getScreenName()
            );
            intent.putExtra(
                    context.getString(R.string.detail_intent_protect),
                    mention.getUser().isProtected()
            );
            Place place = mention.getPlace();
            if (place != null) {
                intent.putExtra(
                        context.getString(R.string.detail_intent_check_in),
                        place.getFullName()
                );
            } else {
                intent.putExtra(
                        context.getString(R.string.detail_intent_check_in),
                        (String) null
                );
            }
            /* Do something with photoURL */
            intent.putExtra(
                    context.getString(R.string.detail_intent_photo_url),
                    (String) null
            );
            intent.putExtra(
                    context.getString(R.string.detail_intent_text),
                    mention.getText()
            );
            intent.putExtra(
                    context.getString(R.string.detail_intent_retweet),
                    false
            );
            intent.putExtra(
                    context.getString(R.string.detail_intent_retweeted_by_user_name),
                    (String) null
            );
            intent.putExtra(
                    context.getString(R.string.detail_intent_favorite),
                    mention.isFavorited()
            );
        }
        if (mention.isRetweetedByMe() || mention.isRetweeted()) {
            intent.putExtra(
                    context.getString(R.string.detail_intent_retweeted_by_user_id),
                    timelineFragment.getUseId()
            );
            intent.putExtra(
                    context.getString(R.string.detail_intent_retweet),
                    true
            );
            intent.putExtra(
                    context.getString(R.string.detail_intent_retweeted_by_user_name),
                    context.getString(R.string.tweet_info_retweeted_by_me)
            );
        }
        intent.putExtra(
                context.getString(R.string.detail_intent_from_notification),
                true
        );

        return intent;
    }
    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            tweetList.clear();
            for (TimelineRecord record : timelineRecordList) {
                Tweet tweet = TweetUnit.getTweetFromRecord(record);
                tweetList.add(tweet);
            }

            if (firstSignIn) {
                editor.putBoolean(
                        context.getString(R.string.sp_is_timeline_first),
                        false
                ).commit();
                timelineFragment.setContentEmpty(false);
                tweetAdapter.notifyDataSetChanged();
                timelineFragment.setContentShown(true);
            } else {
                tweetAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

            long latestMentionId = sharedPreferences.getLong(
                    context.getString(R.string.sp_latest_mention_id),
                    -1l
            );
            if (mention.getId() > latestMentionId) {
                NotificationManager manager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                builder.setSmallIcon(R.drawable.ic_tweet_notification);
                builder.setTicker(
                        context.getString(R.string.mention_notification_new_mention)
                );
                builder.setContentTitle(
                        context.getString(R.string.mention_notification_new_mention)
                );
                builder.setContentText(mention.getText());

                Intent resultIntent = getMentionToDetailIntent();
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(DetailActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent pendingIntent = stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_ONE_SHOT
                );
                builder.setContentIntent(pendingIntent);

                Notification notification = builder.build();
                notification.flags = Notification.FLAG_AUTO_CANCEL;
                manager.notify(Flag.NOTIFICATION_MENTION_ID, notification);
            }
        } else {
            if (firstSignIn) {
                editor.putBoolean(
                        context.getString(R.string.sp_is_timeline_first),
                        true
                ).commit();
                timelineFragment.setContentEmpty(true);
                timelineFragment.setEmptyText(
                        R.string.timeline_error_get_timeline_failed
                );
                timelineFragment.setContentShown(true);
            } else {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
        timelineFragment.setRefreshFlag(Flag.TIMELINE_TASK_IDLE);
    }
}
