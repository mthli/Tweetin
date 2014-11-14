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
import io.github.mthli.Tweetin.Unit.Tweet.TweetUnit;
import twitter4j.Paging;
import twitter4j.Twitter;

import java.util.ArrayList;
import java.util.List;

public class MentionInitTask extends AsyncTask<Void, Integer, Boolean> {
    private MentionFragment mentionFragment;
    private Context context;
    private Twitter twitter;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private List<MentionRecord> mentionRecordList = new ArrayList<MentionRecord>();
    private boolean tweetWithDetail;

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

        tweetAdapter = mentionFragment.getTweetAdapter();
        tweetList = mentionFragment.getTweetList();

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.sp_name),
                Context.MODE_PRIVATE
        );
        editor = sharedPreferences.edit();
        swipeRefreshLayout = mentionFragment.getSwipeRefreshLayout();

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
        tweetWithDetail = mentionFragment.isTweetWithDetail();

        if (!pullToRefresh) {
            MentionAction action = new MentionAction(context);
            action.openDatabase(false);
            mentionRecordList = action.getMentionRecordList();
            action.closeDatabase();
            tweetList.clear();
            for (MentionRecord record : mentionRecordList) {
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
        TweetUnit tweetUnit = new TweetUnit(context);
        for (twitter4j.Status status : statusList) {
            MentionRecord record = tweetUnit
                    .getMentionRecordFromStatus(status, tweetWithDetail);
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
                Tweet tweet = TweetUnit.getTweetFromRecord(record);
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
