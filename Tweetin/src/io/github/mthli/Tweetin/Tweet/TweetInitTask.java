package io.github.mthli.Tweetin.Tweet;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import io.github.mthli.Tweetin.Database.Tweet.TweetAction;
import io.github.mthli.Tweetin.Database.Tweet.TweetData;
import io.github.mthli.Tweetin.Unit.TaskFlag;
import io.github.mthli.Tweetin.Main.MainActivity;
import io.github.mthli.Tweetin.Main.MainFragment;
import io.github.mthli.Tweetin.R;
import twitter4j.Paging;
import twitter4j.Twitter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TweetInitTask extends AsyncTask<Void, Integer, Boolean> {
    private MainFragment mainFragment;
    private Context context;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private SwipeRefreshLayout srl;
    private boolean isFirstSignIn = false;
    private boolean isPullToRefresh = false;

    private Twitter twitter;
    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private List<TweetData> tweetDataList = new ArrayList<TweetData>();

    public TweetInitTask(
            MainFragment mainFragment,
            boolean isPullToRefresh
    ) {
        this.mainFragment = mainFragment;
        this.isPullToRefresh = isPullToRefresh;
    }

    @Override
    protected void onPreExecute() {
        context = mainFragment.getContentView().getContext();
        srl = mainFragment.getSrl();
        twitter = ((MainActivity) mainFragment.getActivity()).getTwitter();
        tweetAdapter = mainFragment.getTweetAdapter();
        tweetList = mainFragment.getTweetList();

        /* Do something */
        if (mainFragment.getTaskFlag() == TaskFlag.TWEET_TASK_ALIVE) {
            onCancelled();
        } else {
            mainFragment.setTaskFlag(TaskFlag.TWEET_TASK_ALIVE);
        }

        if (!isPullToRefresh) {
            TweetAction action = new TweetAction(context);
            action.opewDatabase(false);
            tweetDataList = action.getTweetDataList();
            tweetList.clear();
            for (TweetData data : tweetDataList) {
                Tweet tweet = new Tweet();
                tweet.setTweetId(data.getTweetId());
                tweet.setUserId(data.getUserId());
                tweet.setAvatarUrl(data.getAvatarUrl());
                tweet.setCreatedAt(data.getCreatedAt());
                tweet.setName(data.getName());
                tweet.setScreenName(data.getScreenName());
                tweet.setText(data.getText());
                tweet.setRetweet(data.isRetweet());
                tweet.setRetweetedBy(data.getRetweetedBy());
                tweetList.add(tweet);
            }
            tweetAdapter.notifyDataSetChanged();
        }

        preferences = mainFragment.getActivity().getPreferences(Context.MODE_PRIVATE);
        editor = preferences.edit();
        if (
                preferences.getString(context.getString(R.string.sp_is_first_sign_in), "false").equals("true")
        ) {
            isFirstSignIn = true;
            mainFragment.setContentShown(false);
        } else {
            isFirstSignIn = false;
            if (!srl.isRefreshing()) {
                srl.setRefreshing(true);
            }
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        TweetAction action = new TweetAction(context);
        action.opewDatabase(true);

        List<twitter4j.Status> statusList;
        try {
            Paging paging = new Paging(1, 50);
            statusList = twitter.getHomeTimeline(paging);
        } catch (Exception e) {
            return false;
        }

        if (isCancelled()) {
            return false;
        }

        action.deleteAll();
        tweetDataList.clear();
        SimpleDateFormat format = new SimpleDateFormat(
                context.getString(R.string.tweet_date_format)
        );
        for (twitter4j.Status status : statusList) {
            TweetData data = new TweetData();
            if (status.isRetweet()) {
                data.setTweetId(status.getId());
                data.setUserId(status.getRetweetedStatus().getUser().getId());
                data.setAvatarUrl(
                        status.getRetweetedStatus().getUser().getBiggerProfileImageURL()
                );
                data.setCreatedAt(
                        format.format(status.getRetweetedStatus().getCreatedAt())
                );
                data.setName(status.getRetweetedStatus().getUser().getName());
                data.setScreenName(
                        "@" + status.getRetweetedStatus().getUser().getScreenName()
                );
                data.setText(status.getRetweetedStatus().getText());
                data.setRetweet(status.isRetweet());
                data.setRetweetedBy(status.getUser().getName());
            } else {
                data.setTweetId(status.getId());
                data.setUserId(status.getUser().getId());
                data.setAvatarUrl(status.getUser().getBiggerProfileImageURL());
                data.setCreatedAt(format.format(status.getCreatedAt()));
                data.setName(status.getUser().getName());
                data.setScreenName("@" + status.getUser().getScreenName());
                data.setText(status.getText());
                data.setRetweet(status.isRetweet());
                data.setRetweetedBy(null);
            }
            if (status.isRetweetedByMe()) {
                data.setRetweet(status.isRetweetedByMe());
                data.setRetweetedBy(
                        context.getString(R.string.tweet_retweeted_by_me)
                );
            }
            action.addTweet(data);
            tweetDataList.add(data);
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
            for (TweetData data : tweetDataList) {
                Tweet tweet = new Tweet();
                tweet.setTweetId(data.getTweetId());
                tweet.setUserId(data.getUserId());
                tweet.setAvatarUrl(data.getAvatarUrl());
                tweet.setCreatedAt(data.getCreatedAt());
                tweet.setName(data.getName());
                tweet.setScreenName(data.getScreenName());
                tweet.setText(data.getText());
                tweet.setRetweet(data.isRetweet());
                tweet.setRetweetedBy(data.getRetweetedBy());
                tweetList.add(tweet);
            }

            if (isFirstSignIn) {
                mainFragment.setContentEmpty(false);
                tweetAdapter.notifyDataSetChanged();
                mainFragment.setContentShown(true);
            } else {
                srl.setRefreshing(false);
                tweetAdapter.notifyDataSetChanged();
            }
        } else {
            if (isFirstSignIn) {
                editor.putString(
                        context.getString(R.string.sp_is_first_sign_in),
                        "false"
                ).commit();
                mainFragment.setContentEmpty(true);
                mainFragment.setEmptyText(R.string.tweet_get_timeline_failed);
                mainFragment.setContentShown(true);
            } else {
                srl.setRefreshing(false);
            }
        }
        mainFragment.setTaskFlag(TaskFlag.TWEET_TASK_DIED);
    }
}
