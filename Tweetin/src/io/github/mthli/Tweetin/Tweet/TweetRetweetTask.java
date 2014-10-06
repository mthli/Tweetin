package io.github.mthli.Tweetin.Tweet;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import io.github.mthli.Tweetin.Database.Tweet.TweetAction;
import io.github.mthli.Tweetin.Main.MainActivity;
import io.github.mthli.Tweetin.Main.MainFragment;
import io.github.mthli.Tweetin.R;
import twitter4j.Twitter;

import java.util.List;

public class TweetRetweetTask extends AsyncTask<Void, Integer, Boolean> {
    private MainFragment mainFragment;
    private Context context;

    private Twitter twitter;
    private twitter4j.Status status;
    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private List<twitter4j.Status> statusList;
    private int position = 0;

    public TweetRetweetTask(
            MainFragment mainFragment,
            int position
    ) {
        this.mainFragment = mainFragment;
        this.position = position;
    }

    @Override
    protected void onPreExecute() {
        context = mainFragment.getContentView().getContext();
        twitter = ((MainActivity) mainFragment.getActivity()).getTwitter();
        tweetAdapter = mainFragment.getTweetAdapter();
        tweetList = mainFragment.getTweetList();
        statusList = mainFragment.getStatusList();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Tweet tweet = tweetList.get(position);
        try {
            status = twitter.retweetStatus(tweet.getTweetId());
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
            Tweet oldTweet = tweetList.get(position);
            Tweet newTweet = new Tweet();
            newTweet.setTweetId(status.getId());
            newTweet.setUserId(oldTweet.getUserId());
            newTweet.setAvatarUrl(oldTweet.getAvatarUrl());
            newTweet.setCreatedAt(oldTweet.getCreatedAt());
            newTweet.setName(oldTweet.getName());
            newTweet.setScreenName(oldTweet.getScreenName());
            newTweet.setText(oldTweet.getText());
            newTweet.setRetweet(true);
            newTweet.setRetweetedBy(
                    context.getString(R.string.tweet_retweeted_by_me)
            );
            tweetList.remove(position);
            tweetList.add(position, newTweet);
            statusList.remove(position);
            statusList.add(position, status);
            tweetAdapter.notifyDataSetChanged();

            TweetAction action = new TweetAction(context);
            action.opewDatabase(true);
            action.updateByMe(oldTweet.getTweetId(), newTweet);
            action.closeDatabase();

            Toast.makeText(
                    context,
                    R.string.tweet_retweet_successful,
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Toast.makeText(
                    context,
                    R.string.tweet_retweet_failed,
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
