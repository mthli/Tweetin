package io.github.mthli.Tweetin.Tweet.Main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import io.github.mthli.Tweetin.Database.Main.MainAction;
import io.github.mthli.Tweetin.Database.Main.MainData;
import io.github.mthli.Tweetin.Tweet.Base.Tweet;
import io.github.mthli.Tweetin.Tweet.Base.TweetAdapter;
import io.github.mthli.Tweetin.Unit.Flag;
import io.github.mthli.Tweetin.Main.MainActivity;
import io.github.mthli.Tweetin.Main.MainFragment;
import io.github.mthli.Tweetin.R;
import twitter4j.Paging;
import twitter4j.Place;
import twitter4j.Twitter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainInitTask extends AsyncTask<Void, Integer, Boolean> {
    private MainFragment mainFragment;
    private Context context;
    private long useId = 0;

    private SharedPreferences.Editor editor;
    private SwipeRefreshLayout srl;
    private boolean isFirstSignIn = false;
    private boolean isPullToRefresh = false;

    private Twitter twitter;
    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private List<MainData> mainDataList = new ArrayList<MainData>();

    public MainInitTask(
            MainFragment mainFragment,
            boolean isPullToRefresh
    ) {
        this.mainFragment = mainFragment;
        this.isPullToRefresh = isPullToRefresh;
    }

    @Override
    protected void onPreExecute() {
        context = mainFragment.getContentView().getContext();
        useId = mainFragment.getUseId();

        srl = mainFragment.getSrl();
        twitter = ((MainActivity) mainFragment.getActivity()).getTwitter();
        tweetAdapter = mainFragment.getTweetAdapter();
        tweetList = mainFragment.getTweetList();

        if (mainFragment.getRefreshFlag() == Flag.TWEET_TASK_ALIVE) {
            onCancelled();
        } else {
            mainFragment.setRefreshFlag(Flag.TWEET_TASK_ALIVE);
        }

        SharedPreferences preferences = mainFragment.getActivity()
                .getSharedPreferences(
                        context.getString(R.string.sp_name),
                        Context.MODE_PRIVATE
                );
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

        if (!isPullToRefresh) {
            MainAction action = new MainAction(context);
            action.opewDatabase(false);
            mainDataList = action.getTweetDataList();
            action.closeDatabase();
            tweetList.clear();
            for (MainData data : mainDataList) {
                Tweet tweet = new Tweet();
                tweet.setTweetId(data.getTweetId());
                tweet.setUserId(data.getUserId());
                tweet.setAvatarUrl(data.getAvatarUrl());
                tweet.setCreatedAt(data.getCreatedAt());
                tweet.setName(data.getName());
                tweet.setScreenName(data.getScreenName());
                tweet.setProtect(data.isProtected());
                tweet.setText(data.getText());
                tweet.setCheckIn(data.getCheckIn());
                tweet.setRetweet(data.isRetweet());
                tweet.setRetweetedByName(data.getRetweetedByName());
                tweet.setRetweetedById(data.getRetweetedById());
                tweet.setReplyTo(data.getReplyTo());
                tweetList.add(tweet);
            }
            tweetAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        MainAction action = new MainAction(context);
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
        mainDataList.clear();
        SimpleDateFormat format = new SimpleDateFormat(
                context.getString(R.string.tweet_date_format)
        );
        for (twitter4j.Status status : statusList) {
            MainData data = new MainData();
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
                data.setProtect(status.getRetweetedStatus().getUser().isProtected());
                data.setText(status.getRetweetedStatus().getText());
                Place place = status.getRetweetedStatus().getPlace();
                if (place != null) {
                    data.setCheckIn(place.getFullName());
                } else {
                    data.setCheckIn(null);
                }
                data.setRetweet(true);
                data.setRetweetedByName(status.getUser().getName());
                data.setRetweetedById(status.getUser().getId());
                data.setReplyTo(status.getRetweetedStatus().getInReplyToStatusId());
            } else {
                data.setTweetId(status.getId());
                data.setUserId(status.getUser().getId());
                data.setAvatarUrl(status.getUser().getBiggerProfileImageURL());
                data.setCreatedAt(format.format(status.getCreatedAt()));
                data.setName(status.getUser().getName());
                data.setScreenName("@" + status.getUser().getScreenName());
                data.setProtect(status.getUser().isProtected());
                data.setText(status.getText());
                Place place = status.getPlace();
                if (place != null) {
                    data.setCheckIn(place.getFullName());
                } else {
                    data.setCheckIn(null);
                }
                data.setRetweet(false);
                data.setRetweetedByName(null);
                data.setRetweetedById(0);
                data.setReplyTo(status.getInReplyToStatusId());
            }
            if (status.isRetweetedByMe() || status.isRetweeted()) { //
                data.setRetweet(true);
                data.setRetweetedByName(context.getString(R.string.tweet_retweeted_by_me));
                data.setRetweetedById(useId);
            }

            action.addTweet(data);
            mainDataList.add(data);
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
            for (MainData data : mainDataList) {
                Tweet tweet = new Tweet();
                tweet.setTweetId(data.getTweetId());
                tweet.setUserId(data.getUserId());
                tweet.setAvatarUrl(data.getAvatarUrl());
                tweet.setCreatedAt(data.getCreatedAt());
                tweet.setName(data.getName());
                tweet.setScreenName(data.getScreenName());
                tweet.setProtect(data.isProtected());
                tweet.setText(data.getText());
                tweet.setCheckIn(data.getCheckIn());
                tweet.setRetweet(data.isRetweet());
                tweet.setRetweetedByName(data.getRetweetedByName());
                tweet.setRetweetedById(data.getRetweetedById());
                tweet.setReplyTo(data.getReplyTo());
                tweetList.add(tweet);
            }

            if (isFirstSignIn) {
                editor.putString(context.getString(R.string.sp_is_first_sign_in), "false").commit();
                mainFragment.setContentEmpty(false);
                tweetAdapter.notifyDataSetChanged();
                mainFragment.setContentShown(true);
            } else {
                srl.setRefreshing(false);
                tweetAdapter.notifyDataSetChanged();
            }
        } else {
            if (isFirstSignIn) {
                editor.putString(context.getString(R.string.sp_is_first_sign_in), "true").commit();
                mainFragment.setContentEmpty(true);
                mainFragment.setEmptyText(R.string.tweet_get_timeline_failed);
                mainFragment.setContentShown(true);
            } else {
                srl.setRefreshing(false);
            }
        }
        mainFragment.setRefreshFlag(Flag.TWEET_TASK_DIED);
    }
}
