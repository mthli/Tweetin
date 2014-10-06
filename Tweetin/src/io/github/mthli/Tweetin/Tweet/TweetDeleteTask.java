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

public class TweetDeleteTask extends AsyncTask<Void, Integer, Boolean> {
    private MainFragment mainFragment;
    private Context context;

    private Twitter twitter;
    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private int position = 0;

    public TweetDeleteTask(
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
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Tweet tweet = tweetList.get(position);
        try {
            twitter.destroyStatus(tweet.getTweetId());

            TweetAction action = new TweetAction(context);
            action.opewDatabase(true);
            action.deleteTweet(tweet.getTweetId());
            action.closeDatabase();
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
            tweetList.remove(position);
            tweetAdapter.notifyDataSetChanged();

            Toast.makeText(
                    context,
                    R.string.tweet_delete_successful,
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Toast.makeText(
                    context,
                    R.string.tweet_delete_failed,
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
