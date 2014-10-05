package io.github.mthli.Tweetin.Tweet;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import io.github.mthli.Tweetin.Database.Tweet.TweetData;
import io.github.mthli.Tweetin.Main.Flag;
import io.github.mthli.Tweetin.Main.MainActivity;
import io.github.mthli.Tweetin.Main.MainFragment;
import twitter4j.Twitter;

import java.util.ArrayList;
import java.util.List;

public class TweetUpToRefreshTask extends AsyncTask<Void, Integer, Boolean> {
    private MainFragment mainFragment;
    private Context context;
    private SwipeRefreshLayout srl;

    private Twitter twitter;
    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private Tweet old;
    private List<TweetData> tweetDataList = new ArrayList<TweetData>();

    public TweetUpToRefreshTask(MainFragment mainFragment) {
        this.mainFragment = mainFragment;
    }

    @Override
    protected void onPreExecute() {
        context = mainFragment.getContentView().getContext();
        srl = mainFragment.getSrl();
        twitter = ((MainActivity) mainFragment.getActivity()).getTwitter();
        tweetAdapter = mainFragment.getTweetAdapter();
        tweetList = mainFragment.getTweetList();

        /* Do something */
        if (mainFragment.getTaskFlag() == Flag.TWEET_TASK_ALIVE) {
            onCancelled();
        } else {
            mainFragment.setTaskFlag(Flag.TWEET_TASK_ALIVE);
        }

        old = tweetList.get(tweetList.size() - 1);
        srl.setRefreshing(true);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        /* Do something */

        return false;
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
            /* Do something */
        } else {
            /* Do something */
        }
        mainFragment.setTaskFlag(Flag.TWEET_TASK_DIED);
    }
}
