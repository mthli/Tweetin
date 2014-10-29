package io.github.mthli.Tweetin.Tweet.Timeline;

import android.content.Context;
import android.os.AsyncTask;
import io.github.mthli.Tweetin.Main.MainFragment;
import io.github.mthli.Tweetin.Tweet.Base.Tweet;
import io.github.mthli.Tweetin.Tweet.Base.TweetAdapter;
import twitter4j.Paging;
import twitter4j.Twitter;

import java.util.List;

public class TimelineInitTask extends AsyncTask<Void, Integer, Boolean> {

    private MainFragment mainFragment;
    private Context context;
    private Twitter twitter;
    private long useId;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;

    public TimelineInitTask(
            MainFragment mainFragment
    ) {
        this.mainFragment = mainFragment;
    }

    @Override
    protected void onPreExecute() {
        context = mainFragment.getView().getContext();
        twitter = mainFragment.getTwitter();
        useId = mainFragment.getUseId();

        tweetAdapter = mainFragment.getTweetAdapter();
        tweetList = mainFragment.getTweetList();

        /* Do something */
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        List<twitter4j.Status> statusList;
        try {
            Paging paging = new Paging(1, 40);
            statusList = twitter.getHomeTimeline(paging);
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
            /* Do something */
        } else {
            /* Do something */
        }
    }
}
