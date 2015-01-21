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
import io.github.mthli.Tweetin.Fragment.TweetList.FavoriteFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.Tweet;
import io.github.mthli.Tweetin.Tweet.TweetAdapter;
import io.github.mthli.Tweetin.Tweet.TweetUnit;
import io.github.mthli.Tweetin.Twitter.TwitterUnit;
import twitter4j.Paging;
import twitter4j.TwitterException;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFirstTask extends AsyncTask<Void, Void, Boolean> {
    private FavoriteFragment favoriteFragment;
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

    public FavoriteFirstTask(FavoriteFragment favoriteFragment, boolean swipeRefresh) {
        this.favoriteFragment = favoriteFragment;
        this.context = favoriteFragment.getContext();

        this.sharedPreferences = context.getSharedPreferences(context.getString(R.string.sp_tweetin), Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();

        this.swipeRefreshLayout = favoriteFragment.getSwipeRefreshLayout();
        this.swipeRefresh = swipeRefresh;

        this.tweetAdapter = favoriteFragment.getTweetAdapter();
        this.tweetList = favoriteFragment.getTweetList();
        this.recordList = new ArrayList<DataRecord>();
        this.tweetUnit = new TweetUnit(favoriteFragment.getActivity());

        this.error = context.getString(R.string.fragment_error_get_favorite_data_failed);
    }

    private boolean isFirstLoad() {
        return sharedPreferences.getBoolean(context.getString(R.string.sp_is_favorite_first), false);
    }

    @Override
    protected void onPreExecute() {
        favoriteFragment.setLoadTaskStatus(FlagUnit.TASK_RUNNING);

        if (TwitterUnit.getUseScreenNameFromSharedPreferences(context) == null) {
            favoriteFragment.setContentEmpty(true);
            favoriteFragment.setEmptyText(R.string.fragment_error_get_authorization_failed);
            favoriteFragment.setContentShown(false);

            cancel(true);
            return;
        }

        favoriteFragment.setPreviousPosition(0);
        favoriteFragment.setNextPage(2);

        if (isFirstLoad()) {
            favoriteFragment.setContentShown(false);
        } else if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }

        if (!swipeRefresh) {
            DataAction action = new DataAction(context);
            action.openDatabase(false);
            recordList = action.getDataRecordList(DataUnit.FAVORITE_TABLE);
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
            statusList = TwitterUnit.getTwitterFromSharedPreferences(context).getFavorites(new Paging(1, 40));
        } catch (TwitterException t) {
            error = t.getMessage();
            return false;
        }

        if (isCancelled()) {
            return false;
        }

        DataAction action = new DataAction(context);
        action.openDatabase(true);
        action.deleteAll(DataUnit.FAVORITE_TABLE);
        recordList.clear();
        for (twitter4j.Status status : statusList) {
            DataRecord record = tweetUnit.getDataRecordFromStatus(status);
            action.addDataRecord(record, DataUnit.FAVORITE_TABLE);
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

            if (isFirstLoad()) {
                editor.putBoolean(context.getString(R.string.sp_is_favorite_first), false).commit();

                favoriteFragment.setContentEmpty(false);
                tweetAdapter.notifyDataSetChanged();
                favoriteFragment.setContentShown(true);
            } else {
                tweetAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        } else {
            if (isFirstLoad()) {
                editor.putBoolean(context.getString(R.string.sp_is_favorite_first), true).commit();

                favoriteFragment.setContentEmpty(true);
                favoriteFragment.setEmptyText(error);
                favoriteFragment.setContentShown(true);
            } else {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        }

        favoriteFragment.setLoadTaskStatus(FlagUnit.TASK_IDLE);
    }
}
