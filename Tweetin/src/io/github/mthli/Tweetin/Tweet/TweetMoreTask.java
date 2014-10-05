package io.github.mthli.Tweetin.Tweet;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.TaskFlag;
import io.github.mthli.Tweetin.Main.MainActivity;
import io.github.mthli.Tweetin.Main.MainFragment;
import twitter4j.Paging;
import twitter4j.Twitter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TweetMoreTask extends AsyncTask<Void, Integer, Boolean> {
    private MainFragment mainFragment;
    private Context context;
    private SwipeRefreshLayout srl;

    private Twitter twitter;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private List<twitter4j.Status> statusList = new ArrayList<twitter4j.Status>();

    public TweetMoreTask(MainFragment mainFragment) {
        this.mainFragment = mainFragment;
    }

    @Override
    protected void onPreExecute() {
        context = mainFragment.getContentView().getContext();
        srl = mainFragment.getSrl();
        twitter = ((MainActivity) mainFragment.getActivity()).getTwitter();

        tweetAdapter = mainFragment.getTweetAdapter();
        tweetList = mainFragment.getTweetList();

        if (mainFragment.getTaskFlag() == TaskFlag.TWEET_TASK_ALIVE) {
            onCancelled();
        } else {
            mainFragment.setTaskFlag(TaskFlag.TWEET_TASK_ALIVE);
        }

        srl.setRefreshing(true);
    }

    private static int count = 2;
    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            Paging paging = new Paging(count, 50);
            statusList = twitter.getHomeTimeline(paging);
            count++;
        } catch (Exception e) {
            return false;
        }

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
            SimpleDateFormat format = new SimpleDateFormat(
                    context.getString(R.string.tweet_date_format)
            );
            for (twitter4j.Status status : statusList) {
                Tweet tweet = new Tweet();
                if (status.isRetweet()) {
                    tweet.setTweetId(status.getId());
                    tweet.setUserId(status.getRetweetedStatus().getUser().getId());
                    tweet.setAvatarUrl(
                            status.getRetweetedStatus().getUser().getBiggerProfileImageURL()
                    );
                    tweet.setCreatedAt(
                            format.format(status.getRetweetedStatus().getCreatedAt())
                    );
                    tweet.setName(status.getRetweetedStatus().getUser().getName());
                    tweet.setScreenName(
                            "@" + status.getRetweetedStatus().getUser().getScreenName()
                    );
                    tweet.setText(status.getRetweetedStatus().getText());
                    tweet.setRetweet(status.isRetweet());
                    tweet.setRetweetedBy(status.getUser().getName());
                } else {
                    tweet.setTweetId(status.getId());
                    tweet.setUserId(status.getUser().getId());
                    tweet.setAvatarUrl(status.getUser().getBiggerProfileImageURL());
                    tweet.setCreatedAt(format.format(status.getCreatedAt()));
                    tweet.setName(status.getUser().getName());
                    tweet.setScreenName(status.getUser().getScreenName());
                    tweet.setText(status.getText());
                    tweet.setRetweet(status.isRetweet());
                    tweet.setRetweetedBy(null);
                }
                if (status.isRetweetedByMe()) {
                    tweet.setRetweet(status.isRetweetedByMe());
                    tweet.setRetweetedBy(context.getString(R.string.tweet_retweeted_by_me));
                }
                tweetList.add(tweet);
            }
            tweetAdapter.notifyDataSetChanged();
        }
        srl.setRefreshing(false);
        mainFragment.setTaskFlag(TaskFlag.TWEET_TASK_DIED);
    }
}
