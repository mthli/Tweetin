package io.github.mthli.Tweetin.Mention;

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
import io.github.mthli.Tweetin.Database.Mention.MentionAction;
import io.github.mthli.Tweetin.Database.Mention.MentionRecord;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import java.util.ArrayList;
import java.util.List;

public class MentionFragment extends ProgressFragment {
    private View view;
    public static boolean reload = false;

    private int refreshFlag = Flag.MENTION_TASK_IDLE;
    private boolean isMoveToBottom = false;
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

    private MentionInitTask mentionInitTask;
    private MentionMoreTask mentionMoreTask;
    private MentionRetweetTask mentionRetweetTask;
    public boolean isSomeTaskRunning() {
        if (
                (mentionInitTask != null && mentionInitTask.getStatus() == AsyncTask.Status.RUNNING)
                || (mentionMoreTask != null && mentionMoreTask.getStatus() == AsyncTask.Status.RUNNING)
        ) {
            return true;
        }
        return false;
    }
    public void cancelAllTask() {
        if (mentionInitTask != null && mentionInitTask.getStatus() == AsyncTask.Status.RUNNING) {
            mentionInitTask.cancel(true);
        }
        if (mentionMoreTask != null && mentionMoreTask.getStatus() == AsyncTask.Status.RUNNING) {
            mentionMoreTask.cancel(true);
        }
        if (mentionRetweetTask != null && mentionRetweetTask.getStatus() == AsyncTask.Status.RUNNING) {
            mentionRetweetTask.cancel(true);
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.mention_fragment);
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
                .findViewById(R.id.mention_fragment_listview);
        tweetAdapter = new TweetAdapter(
                view.getContext(),
                R.layout.tweet,
                tweetList
        );
        listView.setAdapter(tweetAdapter);
        tweetAdapter.notifyDataSetChanged();

        swipeRefreshLayout = (SwipeRefreshLayout) view
                .findViewById(R.id.mention_swipe_container);
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
                    isMoveToBottom = true;
                }
                if (previous > firstVisibleItem) {
                    isMoveToBottom = false;
                }
                previous = firstVisibleItem;

                if (totalItemCount == firstVisibleItem + visibleItemCount) {
                    if (!isSomeTaskRunning() && isMoveToBottom) {
                        mentionMoreTask = new MentionMoreTask(MentionFragment.this);
                        mentionMoreTask.execute();
                    }
                }
            }
        });

        if (reload) {
            MentionAction action = new MentionAction(view.getContext());
            action.openDatabase(false);
            List<MentionRecord> mentionRecordList = action.getMentionRecordList();
            action.closeDatabase();
            tweetList.clear();
            for (MentionRecord record : mentionRecordList) {
                Tweet tweet = new Tweet();
                tweet.setStatusId(record.getStatusId());
                tweet.setReplyToStatusId(record.getReplyToStatusId());
                tweet.setUserId(record.getUserId());
                tweet.setRetweetedByUserId(record.getRetweetedByUserId());
                tweet.setAvatarURL(record.getAvatarURL());
                tweet.setCreatedAt(record.getCreatedAt());
                tweet.setName(record.getName());
                tweet.setScreenName(record.getScreenName());
                tweet.setProtect(record.isProtect());
                tweet.setCheckIn(record.getCheckIn());
                tweet.setText(record.getText());
                tweet.setRetweet(record.isRetweet());
                tweet.setRetweetedByUserName(record.getRetweetedByUserName());
                tweetList.add(tweet);
            }
        } else {
            mentionInitTask = new MentionInitTask(
                    MentionFragment.this,
                    false
            );
            mentionInitTask.execute();
        }
    }

    /* Do something */
}
