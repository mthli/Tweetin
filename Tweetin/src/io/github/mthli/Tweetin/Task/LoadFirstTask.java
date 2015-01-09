package io.github.mthli.Tweetin.Task;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import io.github.mthli.Tweetin.Activity.MainActivity;
import io.github.mthli.Tweetin.Data.DataAction;
import io.github.mthli.Tweetin.Data.DataRecord;
import io.github.mthli.Tweetin.Data.DataUnit;
import io.github.mthli.Tweetin.Flag.FlagUnit;
import io.github.mthli.Tweetin.Fragment.BaseFragment;
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

public class LoadFirstTask extends AsyncTask<Void, Integer, Boolean> {

    private BaseFragment baseFragment;
    private int fragmentFlag;
    private Context context;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private TweetUnit tweetUnit;
    private List<DataRecord> recordList = new ArrayList<DataRecord>();

    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean swipeRefresh;

    public LoadFirstTask(BaseFragment baseFragment, boolean swipeRefresh) {
        this.baseFragment = baseFragment;

        this.swipeRefresh = swipeRefresh;
    }

    private boolean isFirstLoad() {
        switch (fragmentFlag) {
            case FlagUnit.IN_TIMELINE_FRAGMENT:
                return sharedPreferences.getBoolean(
                        context.getString(R.string.sp_is_timeline_first),
                        false
                );
            case FlagUnit.IN_MENTION_FRAGMENT:
                return sharedPreferences.getBoolean(
                        context.getString(R.string.sp_is_mention_first),
                        false
                );
            case FlagUnit.IN_FAVORITE_FRAGMENT:
                return sharedPreferences.getBoolean(
                        context.getString(R.string.sp_is_favorite_first),
                        false
                );
            default:
                return false;
        }
    }

    private String getCurrentDatabaseTable() {
        switch (fragmentFlag) {
            case FlagUnit.IN_TIMELINE_FRAGMENT:
                return DataUnit.TIMELINE_TABLE;
            case FlagUnit.IN_MENTION_FRAGMENT:
                return DataUnit.MENTION_TABLE;
            case FlagUnit.IN_FAVORITE_FRAGMENT:
                return DataUnit.FAVORITE_TABLE;
            default:
                return DataUnit.TIMELINE_TABLE;
        }
    }

    @Override
    protected void onPreExecute() {
        if (baseFragment.getTaskStatus() == FlagUnit.TASK_RUNNING) {
            onCancelled();
        } else {
            baseFragment.setTaskStatus(FlagUnit.TASK_RUNNING);
        }

        fragmentFlag = baseFragment.getFragmentFlag();
        context = baseFragment.getActivity();

        sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.sp_tweetin),
                Context.MODE_PRIVATE
        );
        editor = sharedPreferences.edit();

        String useScreenName = TwitterUnit.getUseScreenNameFromSharedPreferences(context);
        if (useScreenName == null) {
            baseFragment.setContentEmpty(true);
            baseFragment.setEmptyText(R.string.fragmet_empty_get_authorization_failed);
            baseFragment.setContentShown(false);

            onCancelled();
        }

        tweetAdapter = baseFragment.getTweetAdapter();
        tweetList = baseFragment.getTweetList();
        tweetUnit = new TweetUnit(context);

        swipeRefreshLayout = baseFragment.getSwipeRefreshLayout();

        if (isFirstLoad()) {
            baseFragment.setContentShown(false);
        } else {
            if (!swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
        }

        if (!swipeRefresh) {
            DataAction action = new DataAction(context);
            action.openDatabase(false);
            recordList = action.getDataRecordList(getCurrentDatabaseTable());
            action.closeDatabase();

            tweetList.clear();

            for (DataRecord record : recordList) {
                Tweet tweet = tweetUnit.getTweetFromDataRecord(record);
                tweetList.add(tweet);
            }

            tweetAdapter.notifyDataSetChanged();
        }
    }

    private List<twitter4j.Status> getStatusList() throws TwitterException {
        Twitter twitter = TwitterUnit.getTwitterFromSharedPreferences(context);

        Paging paging = new Paging(1, 40);

        switch (fragmentFlag) {
            case FlagUnit.IN_TIMELINE_FRAGMENT:
                return twitter.getHomeTimeline(paging);
            case FlagUnit.IN_MENTION_FRAGMENT:
                return twitter.getMentionsTimeline(paging);
            case FlagUnit.IN_FAVORITE_FRAGMENT:
                return twitter.getFavorites(paging);
            default:
                return new ArrayList<twitter4j.Status>();
        }
    }

    private twitter4j.Status latestMention = null;

    private twitter4j.Status getLatestMention() throws TwitterException {
        Twitter twitter = TwitterUnit.getTwitterFromSharedPreferences(context);

        Paging paging = new Paging(1, 1);

        return twitter.getMentionsTimeline(paging).get(0);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        List<twitter4j.Status> statusList;

        try {
            statusList = getStatusList();

            if (fragmentFlag != FlagUnit.IN_FAVORITE_FRAGMENT) {
                latestMention = getLatestMention();
            }
        } catch (Exception e) {
            return false;
        }

        if (isCancelled()) {
            return false;
        }

        DataAction action = new DataAction(context);
        action.openDatabase(true);
        action.deleteAll(getCurrentDatabaseTable());

        recordList.clear();

        for (twitter4j.Status status : statusList) {
            DataRecord record = tweetUnit.getDataRecordFromStatus(status);

            action.addDataRecord(record, getCurrentDatabaseTable());

            recordList.add(record);
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

    private String getEmptyText() {
        switch (fragmentFlag) {
            case FlagUnit.IN_TIMELINE_FRAGMENT:
                return context.getString(R.string.fragment_empty_get_timeline_data_failed);
            case FlagUnit.IN_MENTION_FRAGMENT:
                return context.getString(R.string.fragment_empty_get_mention_data_failed);
            case FlagUnit.IN_FAVORITE_FRAGMENT:
                return context.getString(R.string.fragment_empty_get_favorite_data_failed);
            default:
                return context.getString(R.string.fragment_empty_get_data_failed);
        }
    }

    private void updateInitializationResultToSharedPreferences(boolean result) {
        switch (fragmentFlag) {
            case FlagUnit.IN_TIMELINE_FRAGMENT:
                editor.putBoolean(
                        context.getString(R.string.sp_is_timeline_first),
                        !result
                ).commit();
                break;
            case FlagUnit.IN_MENTION_FRAGMENT:
                editor.putBoolean(
                        context.getString(R.string.sp_is_mention_first),
                        !result
                ).commit();
                break;
            case FlagUnit.IN_FAVORITE_FRAGMENT:
                editor.putBoolean(
                        context.getString(R.string.sp_is_favorite_first),
                        !result
                ).commit();
                break;
            default:
                editor.putBoolean(
                        context.getString(R.string.sp_is_timeline_first),
                        true
                ).commit();
                editor.putBoolean(
                        context.getString(R.string.sp_is_mention_first),
                        true
                ).commit();
                editor.putBoolean(
                        context.getString(R.string.sp_is_favorite_first),
                        true
                ).commit();
                break;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            tweetList.clear();
            for (DataRecord record : recordList) {
                Tweet tweet = tweetUnit.getTweetFromDataRecord(record);
                tweetList.add(tweet);
            }

            if (isFirstLoad()) {
                updateInitializationResultToSharedPreferences(result);

                baseFragment.setContentEmpty(false);
                tweetAdapter.notifyDataSetChanged();
                baseFragment.setContentShown(true);
            } else {
                tweetAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

            long spLatestMentionId = sharedPreferences.getLong(
                    context.getString(R.string.sp_latest_mention_id),
                    -1
            );
            if (latestMention != null && latestMention.getId() > spLatestMentionId) {
                if (fragmentFlag == FlagUnit.IN_TIMELINE_FRAGMENT) {
                    ((MainActivity) baseFragment.getActivity()).showBadge(true);
                }

                editor.putLong(
                        context.getString(R.string.sp_latest_mention_id),
                        latestMention.getId()
                ).commit();
            }
        } else {
            if (isFirstLoad()) {
                updateInitializationResultToSharedPreferences(result);

                baseFragment.setContentEmpty(true);
                baseFragment.setEmptyText(getEmptyText());
                baseFragment.setContentShown(true);
            } else {
                swipeRefreshLayout.setRefreshing(false);
            }
        }

        baseFragment.setTaskStatus(FlagUnit.TASK_IDLE);
    }
}
