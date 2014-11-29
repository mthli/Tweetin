package io.github.mthli.Tweetin.Fragment;

import android.content.*;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Display;
import android.view.View;
import android.widget.*;
import com.devspark.progressfragment.ProgressFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Task.Favorite.*;
import io.github.mthli.Tweetin.Task.Unit.CancelTask;
import io.github.mthli.Tweetin.Task.Unit.DeleteTask;
import io.github.mthli.Tweetin.Task.Unit.RetweetTask;
import io.github.mthli.Tweetin.Unit.ContextMenu.ContextMenuUnit;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import io.github.mthli.Tweetin.Unit.Tweet.TweetUnit;
import twitter4j.Twitter;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends ProgressFragment {

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

    private boolean tweetWithDetail;
    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList = new ArrayList<Tweet>();
    public boolean isTweetWithDetail() {
        return tweetWithDetail;
    }
    public TweetAdapter getTweetAdapter() {
        return tweetAdapter;
    }
    public List<Tweet> getTweetList() {
        return tweetList;
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
    private DeleteTask deleteTask;
    private RetweetTask retweetTask;
    private CancelTask cancelTask;
    public void setDeleteTask(DeleteTask deleteTask) {
        if (this.deleteTask != null && this.deleteTask.getStatus() == AsyncTask.Status.RUNNING) {
            this.deleteTask.cancel(true);
        }
        this.deleteTask = deleteTask;
    }
    public void setRetweetTask(RetweetTask retweetTask) {
        if (this.retweetTask != null && this.retweetTask.getStatus() == AsyncTask.Status.RUNNING) {
            this.retweetTask.cancel(true);
        }
        this.retweetTask = retweetTask;
    }
    public void setCancelTask(CancelTask cancelTask) {
        if (this.cancelTask != null && this.cancelTask.getStatus() == AsyncTask.Status.RUNNING) {
            this.cancelTask.cancel(true);
        }
        this.cancelTask = cancelTask;
    }
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
        if (deleteTask != null && deleteTask.getStatus() == AsyncTask.Status.RUNNING) {
            deleteTask.cancel(true);
        }
        if (retweetTask != null && retweetTask.getStatus() == AsyncTask.Status.RUNNING) {
            retweetTask.cancel(true);
        }
        if (cancelTask != null && cancelTask.getStatus() == AsyncTask.Status.RUNNING) {
            cancelTask.cancel(true);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.favorite_fragment);
        View view = getContentView();
        setContentEmpty(false);
        setContentShown(true);

        twitter = TweetUnit.getTwitterFromSharedPreferences(getActivity());
        useId = TweetUnit.getUseIdFromeSharedPreferences(getActivity());

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                getString(R.string.sp_name),
                Context.MODE_PRIVATE
        );
        tweetWithDetail = sharedPreferences.getBoolean(
                getString(R.string.sp_is_tweet_with_detail),
                false
        );
        ListView listView = (ListView) view
                .findViewById(R.id.favorite_fragment_listview);
        tweetAdapter = new TweetAdapter(
                getActivity(),
                R.layout.tweet,
                tweetList,
                tweetWithDetail
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
        Display display = getActivity()
                .getWindowManager()
                .getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        swipeRefreshLayout.setProgressViewOffset(
                false,
                0,
                height / 10
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                favoriteInitTask = new FavoriteInitTask(
                        FavoriteFragment.this,
                        true
                );
                favoriteInitTask.execute();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContextMenuUnit.show(
                        getActivity(),
                        position
                );
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
}
