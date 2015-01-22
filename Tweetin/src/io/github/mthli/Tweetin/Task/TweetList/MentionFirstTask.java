package io.github.mthli.Tweetin.Task.TweetList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;
import io.github.mthli.Tweetin.Data.DataAction;
import io.github.mthli.Tweetin.Data.DataRecord;
import io.github.mthli.Tweetin.Data.DataUnit;
import io.github.mthli.Tweetin.Flag.FlagUnit;
import io.github.mthli.Tweetin.Fragment.TweetList.MentionFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.Tweet;
import io.github.mthli.Tweetin.Tweet.TweetAdapter;
import io.github.mthli.Tweetin.Tweet.TweetUnit;
import io.github.mthli.Tweetin.Twitter.TwitterUnit;
import twitter4j.Paging;
import twitter4j.TwitterException;

import java.util.ArrayList;
import java.util.List;

public class MentionFirstTask extends AsyncTask<Void, Void, Boolean> {
    private MentionFragment mentionFragment;
    private Context context;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean swipeRefresh;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private List<DataRecord> recordList;
    private TweetUnit tweetUnit;

    private String error;
    
    public MentionFirstTask(MentionFragment mentionFragment, boolean swipeRefresh) {
        this.mentionFragment = mentionFragment;
        this.context = mentionFragment.getContext();

        this.sharedPreferences = context.getSharedPreferences(context.getString(R.string.sp_tweetin), Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();

        this.swipeRefreshLayout = mentionFragment.getSwipeRefreshLayout();
        this.swipeRefresh = swipeRefresh;

        this.tweetAdapter = mentionFragment.getTweetAdapter();
        this.tweetList = mentionFragment.getTweetList();
        this.recordList = new ArrayList<DataRecord>();
        this.tweetUnit = new TweetUnit(mentionFragment.getActivity());

        this.error = context.getString(R.string.fragment_error_get_mention_data_failed);
    }

    private boolean isFirstLoad() {
        return sharedPreferences.getBoolean(context.getString(R.string.sp_is_mention_first), false);
    }

    @Override
    protected void onPreExecute() {
        mentionFragment.setLoadTaskStatus(FlagUnit.TASK_RUNNING);

        if (TwitterUnit.getUseScreenNameFromSharedPreferences(context) == null) {
            mentionFragment.setContentEmpty(true);
            mentionFragment.setEmptyText(R.string.fragment_error_get_authorization_failed);
            mentionFragment.setContentShown(false);

            cancel(true);
            return;
        }

        mentionFragment.setPreviousPosition(0);
        mentionFragment.setNextPage(2);

        if (isFirstLoad()) {
            mentionFragment.setContentShown(false);
        } else if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }

        if (!swipeRefresh) {
            DataAction action = new DataAction(context);
            action.openDatabase(false);
            recordList = action.getDataRecordList(DataUnit.MENTION_TABLE);
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
            statusList = TwitterUnit.getTwitterFromSharedPreferences(context).getMentionsTimeline(new Paging(1, 40));
        } catch (TwitterException t) {
            error = t.getMessage();
            return false;
        }

        if (isCancelled()) {
            return false;
        }

        DataAction action = new DataAction(context);
        action.openDatabase(true);
        action.deleteAll(DataUnit.MENTION_TABLE);
        recordList.clear();
        for (twitter4j.Status status : statusList) {
            DataRecord record = tweetUnit.getDataRecordFromStatus(status);
            action.addDataRecord(record, DataUnit.MENTION_TABLE);
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
                mentionFragment.setContentEmpty(true);
                mentionFragment.setEmptyText(R.string.fragment_list_empty);
                mentionFragment.setContentShown(true);
                mentionFragment.setLoadTaskStatus(FlagUnit.TASK_IDLE);
                return;
            }

            if (isFirstLoad()) {
                editor.putBoolean(context.getString(R.string.sp_is_mention_first), false).commit();

                mentionFragment.setContentEmpty(false);
                tweetAdapter.notifyDataSetChanged();
                mentionFragment.setContentShown(true);
            } else {
                tweetAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        } else {
            if (isFirstLoad()) {
                editor.putBoolean(context.getString(R.string.sp_is_mention_first), true).commit();

                mentionFragment.setContentEmpty(true);
                mentionFragment.setEmptyText(error);
                mentionFragment.setContentShown(true);
            } else {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        }

        mentionFragment.setLoadTaskStatus(FlagUnit.TASK_IDLE);
    }
}