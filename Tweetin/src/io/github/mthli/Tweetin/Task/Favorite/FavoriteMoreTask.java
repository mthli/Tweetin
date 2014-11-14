package io.github.mthli.Tweetin.Task.Favorite;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import io.github.mthli.Tweetin.Fragment.FavoriteFragment;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import io.github.mthli.Tweetin.Unit.Tweet.TweetUnit;
import twitter4j.Paging;
import twitter4j.Twitter;

import java.util.List;

public class FavoriteMoreTask extends AsyncTask<Void, Integer, Boolean> {
    private FavoriteFragment favoriteFragment;
    private Context context;
    private Twitter twitter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private boolean tweetWithDetail;

    public FavoriteMoreTask(FavoriteFragment favoriteFragment) {
        this.favoriteFragment = favoriteFragment;
    }

    @Override
    protected void onPreExecute() {
        if (favoriteFragment.getRefreshFlag() == Flag.FAVORITE_TASK_RUNNING) {
            onCancelled();
        } else {
            favoriteFragment.setRefreshFlag(Flag.FAVORITE_TASK_RUNNING);
        }

        context = favoriteFragment.getContentView().getContext();
        twitter = favoriteFragment.getTwitter();

        tweetAdapter = favoriteFragment.getTweetAdapter();
        tweetList = favoriteFragment.getTweetList();
        tweetWithDetail = favoriteFragment.isTweetWithDetail();

        swipeRefreshLayout = favoriteFragment.getSwipeRefreshLayout();
        swipeRefreshLayout.setRefreshing(true);
    }

    private List<twitter4j.Status> statusList;
    private static int count = 2;
    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            Paging paging = new Paging(count, 40);
            statusList = twitter.getFavorites(paging);
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
        favoriteFragment.setRefreshFlag(Flag.FAVORITE_TASK_IDLE);
    }
}
