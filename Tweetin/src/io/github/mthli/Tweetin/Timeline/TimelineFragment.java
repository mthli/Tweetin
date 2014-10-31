package io.github.mthli.Tweetin.Timeline;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import com.devspark.progressfragment.ProgressFragment;
import com.melnykov.fab.FloatingActionButton;
import io.github.mthli.Tweetin.Post.PostActivity;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Anim.ActivityAnim;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import java.util.ArrayList;
import java.util.List;

public class TimelineFragment extends ProgressFragment {
    private View view;

    private int refreshFlag = Flag.TIMELINE_TASK_IDLE;
    private boolean isMoveToBottom = false;
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

    private TimelineInitTask timelineInitTask;
    private TimelineMoreTask timelineMoreTask;
    private TimelineRetweetTask timelineRetweetTask;
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
        if (timelineRetweetTask != null && timelineRetweetTask.getStatus() == AsyncTask.Status.RUNNING) {
            timelineRetweetTask.cancel(true);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.timeline_fragment);
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
                .findViewById(R.id.timeline_fragment_listview);
        tweetAdapter = new TweetAdapter(
                view.getContext(),
                R.layout.tweet,
                tweetList
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
                        getString(R.string.post_flag),
                        Flag.POST_ORIGINAL
                );
                ActivityAnim anim = new ActivityAnim();
                startActivity(intent);
                anim.fade(getActivity());
            }
        });
        floatingActionButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                /* Do something */
                return true;
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
                if (scrollState == SCROLL_STATE_IDLE) {
                    /* Do nothing */
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (previous < firstVisibleItem) {
                    isMoveToBottom = true;
                    floatingActionButton.hide();
                }
                if (previous > firstVisibleItem) {
                    isMoveToBottom = false;
                    floatingActionButton.show();
                }
                previous = firstVisibleItem;

                if (totalItemCount == firstVisibleItem + visibleItemCount) {
                    if (!isSomeTaskRunning() && isMoveToBottom) {
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

    /* Do something */
}
