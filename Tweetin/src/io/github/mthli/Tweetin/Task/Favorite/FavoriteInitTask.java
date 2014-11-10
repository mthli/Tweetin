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
import twitter4j.Paging;
import twitter4j.Place;
import twitter4j.Twitter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FavoriteInitTask extends AsyncTask<Void, Integer, Boolean> {
    private FavoriteFragment favoriteFragment;
    private Context context;
    private Twitter twitter;
    private long useId;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private List<FavoriteRecord> favoriteRecordList = new ArrayList<FavoriteRecord>();

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
        useId = favoriteFragment.getUseId();

        tweetAdapter = favoriteFragment.getTweetAdapter();
        tweetList = favoriteFragment.getTweetList();

        SharedPreferences sharedPreferences = favoriteFragment.getSharedPreferences();
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
        SimpleDateFormat format = new SimpleDateFormat(
                context.getString(R.string.tweet_date_format)
        );
        for (twitter4j.Status status : statusList) {
            FavoriteRecord record = new FavoriteRecord();
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
                record.setRetweetedByUserId(-1);
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
