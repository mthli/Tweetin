package io.github.mthli.Tweetin.Task.TweetList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;
import io.github.mthli.Tweetin.Activity.MainActivity;
import io.github.mthli.Tweetin.Data.DataAction;
import io.github.mthli.Tweetin.Data.DataRecord;
import io.github.mthli.Tweetin.Data.DataUnit;
import io.github.mthli.Tweetin.Flag.FlagUnit;
import io.github.mthli.Tweetin.Fragment.TweetList.TimelineFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.Tweet;
import io.github.mthli.Tweetin.Tweet.TweetAdapter;
import io.github.mthli.Tweetin.Tweet.TweetUnit;
import io.github.mthli.Tweetin.Twitter.TwitterUnit;
import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.ArrayList;
import java.util.List;

public class TimelineFirstTask extends AsyncTask<Void, Void, Boolean> {
    private TimelineFragment timelineFragment;
    private Context context;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean swipeRefresh;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private List<DataRecord> recordList;
    private TweetUnit tweetUnit;

    private twitter4j.Status latestMention;

    private String error;

    public TimelineFirstTask(TimelineFragment timelineFragment, boolean swipeRefresh) {
        this.timelineFragment = timelineFragment;
        this.context = timelineFragment.getContext();

        this.sharedPreferences = context.getSharedPreferences(context.getString(R.string.sp_tweetin), Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();

        this.swipeRefreshLayout = timelineFragment.getSwipeRefreshLayout();
        this.swipeRefresh = swipeRefresh;

        this.tweetAdapter = timelineFragment.getTweetAdapter();
        this.tweetList = timelineFragment.getTweetList();
        this.recordList = new ArrayList<DataRecord>();
        this.tweetUnit = new TweetUnit(timelineFragment.getActivity());

        this.error = context.getString(R.string.fragment_error_get_timeline_data_failed);
    }

    private boolean isFirstLoad() {
        return sharedPreferences.getBoolean(context.getString(R.string.sp_is_timeline_first), false);
    }

    @Override
    protected void onPreExecute() {
        timelineFragment.setLoadTaskStatus(FlagUnit.TASK_RUNNING);

        if (TwitterUnit.getUseScreenNameFromSharedPreferences(context) == null) {
            timelineFragment.setContentEmpty(true);
            timelineFragment.setEmptyText(R.string.fragment_error_get_authorization_failed);
            timelineFragment.setContentShown(false);

            cancel(true);
            return;
        }

        timelineFragment.setPreviousPosition(0);
        timelineFragment.setNextPage(2);

        if (isFirstLoad()) {
            timelineFragment.setContentShown(false);
        } else if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }

        if (!swipeRefresh) {
            DataAction action = new DataAction(context);
            action.openDatabase(false);
            recordList = action.getDataRecordList(DataUnit.TIMELINE_TABLE);
            action.closeDatabase();
            tweetList.clear();
            for (DataRecord record : recordList) {
                Tweet tweet = tweetUnit.getTweetFromDataRecord(record);
                tweetList.add(tweet);
            }
            tweetAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (isCancelled()) {
            return false;
        }

        List<twitter4j.Status> statusList;

        try {
            Twitter twitter = TwitterUnit.getTwitterFromSharedPreferences(context);

            Paging paging = new Paging(1, 40);
            statusList = twitter.getHomeTimeline(paging);

            paging = new Paging(1, 1);
            latestMention = twitter.getMentionsTimeline(paging).get(0);
        } catch (TwitterException t) {
            error = t.getMessage();
            return false;
        }

        if (isCancelled()) {
            return false;
        }

        DataAction action = new DataAction(context);
        action.openDatabase(true);
        action.deleteAll(DataUnit.TIMELINE_TABLE);
        recordList.clear();
        for (twitter4j.Status status : statusList) {
            DataRecord record = tweetUnit.getDataRecordFromStatus(status);
            action.addDataRecord(record, DataUnit.TIMELINE_TABLE);
            recordList.add(record);
        }
        action.closeDatabase();

        if (isCancelled()) {
            return false;
        }
        return true;
    }

    @Override
    protected void onCancelled() {}

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            tweetList.clear();
            for (DataRecord record : recordList) {
                tweetList.add(tweetUnit.getTweetFromDataRecord(record));
            }

            if (tweetList.size() <= 0) {
                timelineFragment.setContentEmpty(true);
                timelineFragment.setEmptyText(R.string.fragment_list_empty);
                timelineFragment.setContentShown(true);
                timelineFragment.setLoadTaskStatus(FlagUnit.TASK_IDLE);
                return;
            }

            if (isFirstLoad()) {
                editor.putBoolean(context.getString(R.string.sp_is_timeline_first), false).commit();

                timelineFragment.setContentEmpty(false);
                tweetAdapter.notifyDataSetChanged();
                timelineFragment.setContentShown(true);
            } else {
                tweetAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

            long spLatestMentionId = sharedPreferences.getLong(context.getString(R.string.sp_latest_mention_id), -1);
            if (latestMention != null && latestMention.getId() > spLatestMentionId) {
                ((MainActivity) timelineFragment.getActivity()).showBadge(true);

                editor.putLong(context.getString(R.string.sp_latest_mention_id), latestMention.getId()).commit();
            }
        } else {
            if (isFirstLoad()) {
                editor.putBoolean(context.getString(R.string.sp_is_timeline_first), true).commit();

                timelineFragment.setContentEmpty(true);
                timelineFragment.setEmptyText(error);
                timelineFragment.setContentShown(true);
            } else {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        }

        timelineFragment.setLoadTaskStatus(FlagUnit.TASK_IDLE);
    }
}