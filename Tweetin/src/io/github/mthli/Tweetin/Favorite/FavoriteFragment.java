package io.github.mthli.Tweetin.Favorite;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import com.devspark.progressfragment.ProgressFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends ProgressFragment {
    private View view;

    private int refreshFlag = Flag.FAVORITE_TASK_IDLE;
    private boolean moveToBottom = false;
    private SwipeRefreshLayout swipeRefreshLayout;
    public int getRefreshFlag() {
        return refreshFlag;
    }
    public void setRefreshFlag(int refreshFlag) {
        this.refreshFlag = refreshFlag;
    }
    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList = new ArrayList<Tweet>();
    public TweetAdapter getTweetAdapter() {
        return tweetAdapter;
    }
    public List<Tweet> getTweetList() {
        return tweetList;
    }

    private SharedPreferences sharedPreferences;
    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    private Twitter twitter;
    private long useId;
    public Twitter getTwitter() {
        return twitter;
    }
    public long getUseId() {
        return useId;
    }

    private FavoriteInitTask favoriteInitTask;
    private FavoriteMoreTask favoriteMoreTask;
    public boolean isSomeTaskRunning() {
        if (
                (favoriteInitTask != null && favoriteInitTask.getStatus() == AsyncTask.Status.RUNNING)
                || (favoriteMoreTask != null && favoriteMoreTask.getStatus() == AsyncTask.Status.RUNNING)

        ) {
            return true;
        }
        return false;
    }
    public void cancelAllTask() {
        if (favoriteInitTask != null && favoriteInitTask.getStatus() == AsyncTask.Status.RUNNING) {
            favoriteInitTask.cancel(true);
        }
        if (favoriteMoreTask != null && favoriteMoreTask.getStatus() == AsyncTask.Status.RUNNING) {
            favoriteMoreTask.cancel(true);
        }
        /* Do something */
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.favorite_fragment);
        view = getContentView();
        setContentEmpty(false);
        setContentShown(true);

        sharedPreferences = getActivity().getSharedPreferences(
                getString(R.string.sp_name),
                Context.MODE_PRIVATE
        );
        useId = sharedPreferences.getLong(
                getString(R.string.sp_use_id),
                -1
        );
        String consumerKey = sharedPreferences.getString(
                getString(R.string.sp_consumer_key),
                null
        );
        String consumerSecret = sharedPreferences.getString(
                getString(R.string.sp_consumer_secret),
                null
        );
        String accessToken = sharedPreferences.getString(
                getString(R.string.sp_access_token),
                null
        );
        String accessTokenSecret = sharedPreferences.getString(
                getString(R.string.sp_access_token_secret),
                null
        );
        TwitterFactory factory = new TwitterFactory();
        twitter = factory.getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        AccessToken token = new AccessToken(accessToken, accessTokenSecret);
        twitter.setOAuthAccessToken(token);

        ListView listView = (ListView) view
                .findViewById(R.id.favorite_fragment_listview);
        tweetAdapter = new TweetAdapter(
                view.getContext(),
                R.layout.tweet,
                tweetList
        );
        listView.setAdapter(tweetAdapter);
        tweetAdapter.notifyDataSetChanged();

        swipeRefreshLayout = (SwipeRefreshLayout) view
                .findViewById(R.id.favorite_swipe_container);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.text,
                R.color.secondary_text,
                R.color.text,
                R.color.secondary_text
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                /* Do something */
            }
        });

        /* Do something */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /* Do something */
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                /* Do something */
                return true;
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int previous = 0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                /* Do nothing */
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (previous < firstVisibleItem) {
                    moveToBottom = true;
                }
                if (previous > firstVisibleItem) {
                    moveToBottom = false;
                }
                previous = firstVisibleItem;

                if (totalItemCount == firstVisibleItem + visibleItemCount) {
                    if (!isSomeTaskRunning() && moveToBottom) {
                        favoriteMoreTask = new FavoriteMoreTask(FavoriteFragment.this);
                        favoriteMoreTask.execute();
                    }
                }
            }
        });

        favoriteInitTask = new FavoriteInitTask(
                FavoriteFragment.this,
                false
        );
        favoriteInitTask.execute();
    }

    /* Do something */
}
