package io.github.mthli.Tweetin.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Display;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import com.melnykov.fab.FloatingActionButton;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Task.Detail.*;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends Activity {

    private int refreshFlag = Flag.DETAIL_TASK_IDLE;
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

    private DetailInitTask detailInitTask;
    private DetailDeleteTask detailDeleteTask;
    private DetailRetweetTask detailRetweetTask;
    private DetailFavoriteTask detailFavoriteTask;
    public boolean isSomeTaskRunning() {
        if (detailInitTask != null && detailInitTask.getStatus() == AsyncTask.Status.RUNNING) {
            return true;
        }
        return false;
    }
    public void cancelAllTask() {
        if (detailInitTask != null && detailInitTask.getStatus() == AsyncTask.Status.RUNNING) {
            detailInitTask.cancel(true);
        }
        /* Do something */
    }

    public Tweet getTweetFromIntent() {
        Intent intent = getIntent();
        long statusId = intent.getLongExtra(
                getString(R.string.detail_intent_status_id),
                -1
        );
        long replyToStatusId = intent.getLongExtra(
                getString(R.string.detail_intent_reply_to_status_id),
                -1
        );
        long userId = intent.getLongExtra(
                getString(R.string.detail_intent_user_id),
                -1
        );
        long retweetedByUserId = intent.getLongExtra(
                getString(R.string.detail_intent_retweeted_by_user_id),
                -1
        );
        String avatarURL = intent.getStringExtra(
                getString(R.string.detail_intent_avatar_url)
        );
        String createdAt = intent.getStringExtra(
                getString(R.string.detail_intent_created_at)
        );
        String name = intent.getStringExtra(
                getString(R.string.detail_intent_name)
        );
        String screenName = intent.getStringExtra(
                getString(R.string.detail_intent_screen_name)
        );
        boolean protect = intent.getBooleanExtra(
                getString(R.string.detail_intent_protect),
                false
        );
        String checkIn = intent.getStringExtra(
                getString(R.string.detail_intent_check_in)
        );
        String text = intent.getStringExtra(
                getString(R.string.detail_intent_text)
        );
        String photoURL = intent.getStringExtra(
                getString(R.string.detail_intent_photo_url)
        );
        boolean retweet = intent.getBooleanExtra(
                getString(R.string.detail_intent_retweet),
                false
        );
        String retweetedByUserName = intent.getStringExtra(
                getString(R.string.detail_intent_retweeted_by_user_name)
        );
        boolean favorite = intent.getBooleanExtra(
                getString(R.string.detail_intent_favorite),
                false
        );
        Tweet tweet = new Tweet();
        tweet.setStatusId(statusId);
        tweet.setReplyToStatusId(replyToStatusId);
        tweet.setUserId(userId);
        tweet.setRetweetedByUserId(retweetedByUserId);
        tweet.setAvatarURL(avatarURL);
        tweet.setCreatedAt(createdAt);
        tweet.setName(name);
        tweet.setScreenName(screenName);
        tweet.setProtect(protect);
        tweet.setCheckIn(checkIn);
        tweet.setText(text);
        tweet.setPhotoURL(photoURL);
        tweet.setRetweet(retweet);
        tweet.setRetweetedByUserName(retweetedByUserName);
        tweet.setFavorite(favorite);

        return tweet;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        sharedPreferences = getSharedPreferences(
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

        ListView listView = (ListView) findViewById(R.id.detail_listview);
        tweetAdapter = new TweetAdapter(
                this,
                this,
                R.layout.tweet,
                tweetList,
                true
        );
        listView.setAdapter(tweetAdapter);
        tweetList.add(getTweetFromIntent());
        tweetAdapter.notifyDataSetChanged();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.detail_swipe_container);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.text,
                R.color.secondary_text,
                R.color.text,
                R.color.secondary_text
        );
        Display display = getWindowManager().getDefaultDisplay();
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
                detailInitTask = new DetailInitTask(DetailActivity.this);
                detailInitTask.execute();
            }
        });

        floatingActionButton = (FloatingActionButton) findViewById(
                R.id.detail_floating_action_button
        );
        floatingActionButton.attachToListView(listView);
        floatingActionButton.show();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Do something */
            }
        });

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
                    floatingActionButton.hide();
                }
                if (previous > firstVisibleItem) {
                    floatingActionButton.show();
                }
                previous = firstVisibleItem;
            }
        });

        detailInitTask = new DetailInitTask(this);
        detailInitTask.execute();
    }

    /* Do something */
}
