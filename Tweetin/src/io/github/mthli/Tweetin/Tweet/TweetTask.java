package io.github.mthli.Tweetin.Tweet;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;
import io.github.mthli.Tweetin.Main.MainActivity;
import io.github.mthli.Tweetin.Main.MainFragment;
import io.github.mthli.Tweetin.R;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.User;

import java.text.SimpleDateFormat;
import java.util.List;

public class TweetTask extends AsyncTask<Void, Integer, Boolean> {
    private MainFragment mainFragment;
    private Context context;
    /* Do something */
    private int flag;

    private SwipeRefreshLayout srl;

    private Twitter twitter;
    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private List<twitter4j.Status> statusList;

    public TweetTask(MainFragment mainFragment) {
        this.mainFragment = mainFragment;
    }

    @Override
    protected void onPreExecute() {
        context = mainFragment.getContentView().getContext();
        srl = mainFragment.getSrl();
        twitter = ((MainActivity) mainFragment.getActivity()).getTwitter();
        tweetAdapter = mainFragment.getTweetAdapter();
        tweetList = mainFragment.getTweetList();

        srl.setRefreshing(true);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            statusList = twitter.getHomeTimeline();
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
            srl.setRefreshing(false);
            SimpleDateFormat format = new SimpleDateFormat(
                    context.getString(R.string.tweet_date_format)
            );
            for (twitter4j.Status status : statusList) {
                Tweet tweet = new Tweet();

                /* Do something */
                if (status.isRetweet()) {
                    User user = status.getRetweetedStatus().getUser();
                    tweet.setTweetId(status.getId());
                    tweet.setUserId(user.getId());
                    tweet.setAvatarUrl(user.getBiggerProfileImageURL());
                    tweet.setCreatedAt(format.format(status.getCreatedAt()));
                    tweet.setName(user.getName());
                    tweet.setScreenName("@" + user.getScreenName());
                    tweet.setText(status.getRetweetedStatus().getText());
                    tweet.setRetweeted(status.isRetweet());
                    tweet.setRetweetedBy(status.getUser().getScreenName());
                } else {
                    tweet.setTweetId(status.getId());
                    tweet.setUserId(status.getUser().getId());
                    tweet.setAvatarUrl(status.getUser().getBiggerProfileImageURL());
                    tweet.setCreatedAt(format.format(status.getCreatedAt()));
                    tweet.setName(status.getUser().getName());
                    tweet.setScreenName("@" + status.getUser().getScreenName());
                    tweet.setText(status.getText());
                    tweet.setRetweeted(status.isRetweet());
                    tweet.setRetweetedBy(null);
                }

                tweetList.add(tweet);
            }
            tweetAdapter.notifyDataSetChanged();

            Toast.makeText(
                    context,
                    "OOOOOO",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            srl.setRefreshing(false);
            Toast.makeText(
                    context,
                    "XXXXXXX",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
