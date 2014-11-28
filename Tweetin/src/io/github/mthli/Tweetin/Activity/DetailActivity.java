package io.github.mthli.Tweetin.Activity;

import android.app.Activity;
import android.content.*;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import com.melnykov.fab.FloatingActionButton;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Task.Detail.*;
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

public class DetailActivity extends Activity {

    private int refreshFlag = Flag.DETAIL_TASK_IDLE;
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

    private Twitter twitter;
    private long useId;
    public Twitter getTwitter() {
        return twitter;
    }
    public long getUseId() {
        return useId;
    }

    private DetailInitTask detailInitTask;
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
        if (detailInitTask != null && detailInitTask.getStatus() == AsyncTask.Status.RUNNING) {
            return true;
        }
        return false;
    }
    public void cancelAllTask() {
        if (detailInitTask != null && detailInitTask.getStatus() == AsyncTask.Status.RUNNING) {
            detailInitTask.cancel(true);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        twitter = TweetUnit.getTwitterFromSharedPreferences(this);
        useId = TweetUnit.getUseIdFromeSharedPreferences(this);

        ListView listView = (ListView) findViewById(R.id.detail_listview);
        tweetAdapter = new TweetAdapter(
                this,
                R.layout.tweet,
                tweetList,
                true
        );
        listView.setAdapter(tweetAdapter);
        tweetList.add(TweetUnit.getTweetFromIntent(this));
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

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(
                R.id.detail_floating_action_button
        );
        floatingActionButton.show();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, PostActivity.class);
                ActivityAnim anim = new ActivityAnim();
                intent.putExtra(
                        getString(R.string.post_intent_flag),
                        Flag.POST_REPLY
                );
                intent.putExtra(
                        getString(R.string.post_intent_status_id),
                        TweetUnit.getTweetFromIntent(DetailActivity.this).getStatusId()
                );
                intent.putExtra(
                        getString(R.string.post_intent_status_screen_name),
                        TweetUnit.getTweetFromIntent(DetailActivity.this).getScreenName()
                );
                startActivity(intent);
                anim.fade(DetailActivity.this);
            }
        });
        floatingActionButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(
                        DetailActivity.this,
                        R.string.detail_toast_fab_reply,
                        Toast.LENGTH_SHORT
                ).show();

                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tweet tweet = tweetList.get(position);
                Intent intent = new Intent(DetailActivity.this, PostActivity.class);
                ActivityAnim anim = new ActivityAnim();
                intent.putExtra(
                        getString(R.string.post_intent_flag),
                        Flag.POST_REPLY
                );
                intent.putExtra(
                        getString(R.string.post_intent_status_id),
                        tweet.getStatusId()
                );
                intent.putExtra(
                        getString(R.string.post_intent_status_screen_name),
                        tweet.getScreenName()
                );
                startActivity(intent);
                anim.fade(DetailActivity.this);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ContextMenuUnit.showItemLongClickDialog(
                        DetailActivity.this,
                        twitter,
                        useId,
                        tweetAdapter,
                        tweetList,
                        position
                );
                return true;
            }
        });

        detailInitTask = new DetailInitTask(this);
        detailInitTask.execute();
    }

    private boolean deleteAtDetail = false;
    private boolean retweetAtDetail = false;
    private boolean favoriteAtDetail = false;
    public void setDeleteAtDetail(boolean deleteAtDetail) {
        this.deleteAtDetail = deleteAtDetail;
    }
    public void setRetweetAtDetail(boolean retweetAtDetail) {
        this.retweetAtDetail = retweetAtDetail;
    }
    public void setFavoriteAtDetail(boolean favoriteAtDetail) {
        this.favoriteAtDetail = favoriteAtDetail;
    }
    public void finishDetail() {
        Intent intent = new Intent();
        intent.putExtra(
                getString(R.string.detail_intent_from_position),
                getIntent().getIntExtra(
                        getString(R.string.detail_intent_from_position),
                        -1
                )
        );
        intent.putExtra(
                getString(R.string.detail_intent_is_delete_at_detail),
                deleteAtDetail
        );
        intent.putExtra(
                getString(R.string.detail_intent_is_retweet_at_detail),
                retweetAtDetail
        );
        intent.putExtra(
                getString(R.string.detail_intent_is_favorite_at_detail),
                favoriteAtDetail
        );
        cancelAllTask();
        setResult(RESULT_OK, intent);
        ActivityAnim anim = new ActivityAnim();
        finish();
        anim.rightOut(this);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishDetail();
        }

        return true;
    }
    @Override
    public void onDestroy() {
        finishDetail();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation== Configuration.ORIENTATION_LANDSCAPE) {
            /* Do nothing */
        }
        else{
            /* Do nothing */
        }
    }
}
