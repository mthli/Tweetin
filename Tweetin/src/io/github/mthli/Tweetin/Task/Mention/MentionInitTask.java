package io.github.mthli.Tweetin.Task.Mention;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import io.github.mthli.Tweetin.Database.Mention.MentionAction;
import io.github.mthli.Tweetin.Database.Mention.MentionRecord;
import io.github.mthli.Tweetin.Fragment.MentionFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import twitter4j.Paging;
import twitter4j.Place;
import twitter4j.Twitter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MentionInitTask extends AsyncTask<Void, Integer, Boolean> {
    private MentionFragment mentionFragment;
    private Context context;
    private Twitter twitter;
    private long useId;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private List<MentionRecord> mentionRecordList = new ArrayList<MentionRecord>();

    private SharedPreferences.Editor editor;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean firstSignIn;
    private boolean pullToRefresh;

    public MentionInitTask(
            MentionFragment mentionFragment,
            boolean pullToRefresh
    ) {
        this.mentionFragment = mentionFragment;
        this.pullToRefresh = pullToRefresh;
    }

    @Override
    protected void onPreExecute() {
        if (mentionFragment.getRefreshFlag() == Flag.MENTION_TASK_RUNNING) {
            onCancelled();
        } else {
            mentionFragment.setRefreshFlag(Flag.MENTION_TASK_RUNNING);
        }

        context = mentionFragment.getContentView().getContext();
        twitter = mentionFragment.getTwitter();
        useId = mentionFragment.getUseId();

        tweetAdapter = mentionFragment.getTweetAdapter();
        tweetList = mentionFragment.getTweetList();

        SharedPreferences sharedPreferences = mentionFragment.getSharedPreferences();
        editor = sharedPreferences.edit();
        swipeRefreshLayout = mentionFragment.getSwipeRefreshLayout();

        /*
        if (
                sharedPreferences.getLong(
                        context.getString(R.string.sp_latest_mention_id),
                        -1l
                ) == -1l
                ) {
        */
        if (
                sharedPreferences.getBoolean(
                        context.getString(R.string.sp_is_mention_first),
                        true
                )
        ) {
            firstSignIn = true;
            mentionFragment.setContentShown(false);
        } else {
            firstSignIn = false;
            if (!swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
        }

        if (!pullToRefresh) {
            MentionAction action = new MentionAction(context);
            action.openDatabase(false);
            mentionRecordList = action.getMentionRecordList();
            action.closeDatabase();
            tweetList.clear();
            for (MentionRecord record : mentionRecordList) {
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
                tweet.setText(record.getText());
                tweet.setRetweet(record.isRetweet());
                tweet.setRetweetedByUserName(record.getRetweetedByUserName());
                tweet.setFavorite(record.isFavorite());
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
            statusList = twitter.getMentionsTimeline(paging);
            mention = statusList.get(0);
        } catch (Exception e) {
            return false;
        }
        if (isCancelled()) {
            return false;
        }

        MentionAction action = new MentionAction(context);
        action.openDatabase(true);
        action.deleteAll();
        mentionRecordList.clear();
        SimpleDateFormat format = new SimpleDateFormat(
                context.getString(R.string.tweet_date_format)
        );
        for (twitter4j.Status status : statusList) {
            MentionRecord record = new MentionRecord();
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
            mentionRecordList.add(record);
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

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            tweetList.clear();
            for (MentionRecord record : mentionRecordList) {
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
                tweet.setText(record.getText());
                tweet.setRetweet(record.isRetweet());
                tweet.setRetweetedByUserName(record.getRetweetedByUserName());
                tweet.setFavorite(record.isFavorite());
                tweetList.add(tweet);
            }

            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(Flag.NOTIFICATION_MENTION_ID);
            editor.putLong(
                    context.getString(R.string.sp_latest_mention_id),
                    mention.getId()
            ).commit();

            if (firstSignIn) {
                editor.putBoolean(
                        context.getString(R.string.sp_is_mention_first),
                        false
                ).commit();
                mentionFragment.setContentEmpty(false);
                tweetAdapter.notifyDataSetChanged();
                mentionFragment.setContentShown(true);
            } else {
                tweetAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        } else {
            if (firstSignIn) {
                mentionFragment.setContentEmpty(true);
                mentionFragment.setEmptyText(
                        R.string.mention_error_get_mention_failed
                );
                mentionFragment.setContentShown(true);
            } else {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
        mentionFragment.setRefreshFlag(Flag.MENTION_TASK_IDLE);
    }
}
