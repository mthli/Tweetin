package io.github.mthli.Tweetin.Timeline;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import twitter4j.Twitter;

import java.text.SimpleDateFormat;
import java.util.List;

public class TimelineMoreTask extends AsyncTask<Void, Integer, Boolean> {
    private TimelineFragment timelineFragment;
    private Context context;
    private Twitter twitter;
    private long useId;

    private SwipeRefreshLayout swipeRefreshLayout;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;

    public TimelineMoreTask(TimelineFragment timelineFragment) {
        this.timelineFragment = timelineFragment;
    }

    @Override
    protected void onPreExecute() {
        /* Do something */
    }

    private List<twitter4j.Status> statusList;
    private static int count = 2;
    @Override
    protected Boolean doInBackground(Void... params) {
        /* Do something */

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
            /* Do something */
        } else {
            /* Do something */
        }
        /* Do something */
    }
}
