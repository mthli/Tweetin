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
import com.melnykov.fab.FloatingActionButton;
import io.github.mthli.Tweetin.Activity.PostActivity;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Task.Timeline.*;
import io.github.mthli.Tweetin.Task.Unit.CancelTask;
import io.github.mthli.Tweetin.Task.Unit.DeleteTask;
import io.github.mthli.Tweetin.Task.Unit.FavoriteTask;
import io.github.mthli.Tweetin.Task.Unit.RetweetTask;
import io.github.mthli.Tweetin.Unit.Anim.ActivityAnim;
import io.github.mthli.Tweetin.Unit.ContextMenu.ContextMenuUnit;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import io.github.mthli.Tweetin.Unit.Tweet.TweetUnit;
import twitter4j.Twitter;

import java.util.ArrayList;
import java.util.List;

public class TimelineFragment extends ProgressFragment {

    private int refreshFlag = Flag.TIMELINE_TASK_IDLE;
    private boolean moveToBottom = false;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton floatingActionButton;
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

    private TimelineInitTask timelineInitTask;
    private TimelineMoreTask timelineMoreTask;
    private DeleteTask deleteTask;
    private RetweetTask retweetTask;
    private FavoriteTask favoriteTask;
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
    public void setFavoriteTask(FavoriteTask favoriteTask) {
        if (this.favoriteTask != null && this.favoriteTask.getStatus() == AsyncTask.Status.RUNNING) {
            this.favoriteTask.cancel(true);
        }
        this.favoriteTask = favoriteTask;
    }
    public void setCancelTask(CancelTask cancelTask) {
        if (this.cancelTask != null && this.cancelTask.getStatus() == AsyncTask.Status.RUNNING) {
            this.cancelTask.cancel(true);
        }
        this.cancelTask = cancelTask;
    }
    public boolean isSomeTaskRunning() {
        if (
                (timelineInitTask != null && timelineInitTask.getStatus() == AsyncTask.Status.RUNNING)
                || (timelineMoreTask != null && timelineMoreTask.getStatus() == AsyncTask.Status.RUNNING)
        ) {
            return true;
        }
        return false;
    }
    public void cancelAllTask() {
        if (timelineInitTask != null && timelineInitTask.getStatus() == AsyncTask.Status.RUNNING) {
            timelineInitTask.cancel(true);
        }
        if (timelineMoreTask != null && timelineMoreTask.getStatus() == AsyncTask.Status.RUNNING) {
            timelineMoreTask.cancel(true);
        }
        if (deleteTask != null && deleteTask.getStatus() == AsyncTask.Status.RUNNING) {
            deleteTask.cancel(true);
        }
        if (retweetTask != null && retweetTask.getStatus() == AsyncTask.Status.RUNNING) {
            retweetTask.cancel(true);
        }
        if (favoriteTask != null && favoriteTask.getStatus() == AsyncTask.Status.RUNNING) {
            favoriteTask.cancel(true);
        }
        if (cancelTask != null && cancelTask.getStatus() == AsyncTask.Status.RUNNING) {
            cancelTask.cancel(true);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.timeline_fragment);
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
                .findViewById(R.id.timeline_fragment_listview);
        tweetAdapter = new TweetAdapter(
                getActivity(),
                R.layout.tweet,
                tweetList,
                tweetWithDetail
        );
        listView.setAdapter(tweetAdapter);
        tweetAdapter.notifyDataSetChanged();

        swipeRefreshLayout = (SwipeRefreshLayout) view
                .findViewById(R.id.timeline_swipe_container);
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
                timelineInitTask = new TimelineInitTask(
                        TimelineFragment.this,
                        true
                );
                timelineInitTask.execute();
            }
        });

        floatingActionButton = (FloatingActionButton) view
                .findViewById(R.id.timeline_floating_action_button);
        floatingActionButton.attachToListView(listView);
        floatingActionButton.show();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PostActivity.class);
                intent.putExtra(
                        getString(R.string.post_intent_flag),
                        Flag.POST_ORIGINAL
                );
                ActivityAnim anim = new ActivityAnim();
                startActivity(intent);
                anim.fade(getActivity());
            }
        });
        floatingActionButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(
                        getActivity(),
                        R.string.timeline_toast_fab_add,
                        Toast.LENGTH_SHORT
                ).show();

                return true;
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
                if (scrollState == SCROLL_STATE_IDLE) {
                    /* Do nothing */
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (previous < firstVisibleItem) {
                    moveToBottom = true;
                    floatingActionButton.hide();
                }
                if (previous > firstVisibleItem) {
                    moveToBottom = false;
                    floatingActionButton.show();
                }
                previous = firstVisibleItem;

                if (totalItemCount == firstVisibleItem + visibleItemCount) {
                    if (!isSomeTaskRunning() && moveToBottom) {
                        timelineMoreTask = new TimelineMoreTask(TimelineFragment.this);
                        timelineMoreTask.execute();
                    }
                }
            }
        });

        timelineInitTask = new TimelineInitTask(
                TimelineFragment.this,
                false
        );
        timelineInitTask.execute();
    }
}
