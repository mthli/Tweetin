package io.github.mthli.Tweetin.Task.Mention;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import io.github.mthli.Tweetin.Fragment.MentionFragment;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import io.github.mthli.Tweetin.Unit.Tweet.TweetUnit;
import twitter4j.Paging;
import twitter4j.Twitter;

import java.util.List;

public class MentionMoreTask extends AsyncTask<Void, Integer, Boolean> {
    private MentionFragment mentionFragment;
    private Context context;
    private Twitter twitter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private boolean tweetWithDetail;

    public MentionMoreTask(MentionFragment mentionFragment) {
        this.mentionFragment = mentionFragment;
    }

    @Override
    protected void onPreExecute() {
        if (mentionFragment.getRefreshFlag() == Flag.MENTION_TASK_RUNNING) {
            onCancelled();
        } else {
            mentionFragment.setRefreshFlag(Flag.MENTION_TASK_RUNNING);
        }

        context = mentionFragment.getContentView().getContext();
        twitter = mentionFragment.getTwitter();

        tweetAdapter = mentionFragment.getTweetAdapter();
        tweetList = mentionFragment.getTweetList();
        tweetWithDetail = mentionFragment.isTweetWithDetail();

        swipeRefreshLayout = mentionFragment.getSwipeRefreshLayout();
        swipeRefreshLayout.setRefreshing(true);
    }

    private List<twitter4j.Status> statusList;
    private static int count = 2;
    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            Paging paging = new Paging(count, 40);
            statusList = twitter.getMentionsTimeline(paging);
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
            TweetUnit tweetUnit = new TweetUnit(context);
            for (twitter4j.Status status : statusList) {
                tweetList.add(tweetUnit.getTweetFromStatus(status, tweetWithDetail));
            }
            tweetAdapter.notifyDataSetChanged();
        }
        swipeRefreshLayout.setRefreshing(false);
        mentionFragment.setRefreshFlag(Flag.MENTION_TASK_IDLE);
    }
}
