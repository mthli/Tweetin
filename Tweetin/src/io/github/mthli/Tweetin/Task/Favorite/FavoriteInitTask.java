package io.github.mthli.Tweetin.Task.Favorite;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import io.github.mthli.Tweetin.Database.Favorite.FavoriteAction;
import io.github.mthli.Tweetin.Database.Favorite.FavoriteRecord;
import io.github.mthli.Tweetin.Fragment.FavoriteFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import io.github.mthli.Tweetin.Unit.Tweet.TweetUnit;
import twitter4j.Paging;
import twitter4j.Twitter;

import java.util.ArrayList;
import java.util.List;

public class FavoriteInitTask extends AsyncTask<Void, Integer, Boolean> {
    private FavoriteFragment favoriteFragment;
    private Context context;
    private Twitter twitter;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private List<FavoriteRecord> favoriteRecordList = new ArrayList<FavoriteRecord>();
    private boolean tweetWithDetail;

    private SharedPreferences.Editor editor;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean firstSignIn;
    private boolean pullToRefresh;

    public FavoriteInitTask(
            FavoriteFragment favoriteFragment,
            boolean pullToRefresh
    ) {
        this.favoriteFragment = favoriteFragment;
        this.pullToRefresh = pullToRefresh;
    }

    @Override
    protected void onPreExecute() {
        if (favoriteFragment.getRefreshFlag() == Flag.FAVORITE_TASK_RUNNING) {
            onCancelled();
        } else {
            favoriteFragment.setRefreshFlag(Flag.FAVORITE_TASK_RUNNING);
        }

        context = favoriteFragment.getContentView().getContext();
        twitter = favoriteFragment.getTwitter();

        tweetAdapter = favoriteFragment.getTweetAdapter();
        tweetList = favoriteFragment.getTweetList();
        tweetWithDetail = favoriteFragment.isTweetWithDetail();

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.sp_name),
                Context.MODE_PRIVATE
        );
        editor = sharedPreferences.edit();
        swipeRefreshLayout = favoriteFragment.getSwipeRefreshLayout();

        if (
                sharedPreferences.getBoolean(
                        context.getString(R.string.sp_is_favorite_first),
                        true
                )
        ) {
            firstSignIn = true;
            favoriteFragment.setContentShown(false);
        } else {
            firstSignIn = false;
            if (!swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
        }

        if (!pullToRefresh) {
            FavoriteAction action = new FavoriteAction(context);
            action.openDatabase(false);
            favoriteRecordList = action.getFavoriteRecordList();
            action.closeDatabase();
            tweetList.clear();
            for (FavoriteRecord record : favoriteRecordList) {
                Tweet tweet = TweetUnit.getTweetFromRecord(record);
                tweetList.add(tweet);
            }
            tweetAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        List<twitter4j.Status> statusList;
        try {
            Paging paging = new Paging(1, 40);
            statusList = twitter.getFavorites(paging);
        } catch (Exception e) {
            return false;
        }
        if (isCancelled()) {
            return false;
        }

        FavoriteAction action = new FavoriteAction(context);
        action.openDatabase(true);
        action.deleteAll();
        favoriteRecordList.clear();
        TweetUnit tweetUnit = new TweetUnit(context);
        for (twitter4j.Status status : statusList) {
            FavoriteRecord record = tweetUnit.getFavoriteRecordFromStatus(status, tweetWithDetail);
            action.addRecord(record);
            favoriteRecordList.add(record);
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
            for (FavoriteRecord record : favoriteRecordList) {
                Tweet tweet = TweetUnit.getTweetFromRecord(record);
                tweetList.add(tweet);
            }

            if (firstSignIn) {
                editor.putBoolean(
                        context.getString(R.string.sp_is_favorite_first),
                        false
                ).commit();
                favoriteFragment.setContentEmpty(false);
                tweetAdapter.notifyDataSetChanged();
                favoriteFragment.setContentShown(true);
            } else {
                tweetAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        } else {
            if (firstSignIn) {
                editor.putBoolean(
                        context.getString(R.string.sp_is_favorite_first),
                        true
                ).commit();
                favoriteFragment.setContentEmpty(true);
                favoriteFragment.setEmptyText(
                        R.string.favorite_error_get_favorite_failed
                );
                favoriteFragment.setContentShown(true);
            } else {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
        favoriteFragment.setRefreshFlag(Flag.FAVORITE_TASK_IDLE);
    }
}
