package io.github.mthli.Tweetin.Activity;

import android.app.Activity;
import android.app.AlertDialog;
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
import io.github.mthli.Tweetin.Unit.Anim.ActivityAnim;
import io.github.mthli.Tweetin.Unit.ContextMenu.ContextMenuAdapter;
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
        if (detailDeleteTask != null && detailDeleteTask.getStatus() == AsyncTask.Status.RUNNING) {
            detailDeleteTask.cancel(true);
        }
        if (detailRetweetTask != null && detailRetweetTask.getStatus() == AsyncTask.Status.RUNNING) {
            detailRetweetTask.cancel(true);
        }
        if (detailFavoriteTask != null && detailFavoriteTask.getStatus() == AsyncTask.Status.RUNNING) {
            detailFavoriteTask.cancel(true);
        }
    }

    public Tweet getTweetFromIntent() {
        Intent intent = getIntent();
        long statusId = intent.getLongExtra(
                getString(R.string.detail_intent_status_id),
                -1l
        );
        long replyToStatusId = intent.getLongExtra(
                getString(R.string.detail_intent_reply_to_status_id),
                -1l
        );
        long userId = intent.getLongExtra(
                getString(R.string.detail_intent_user_id),
                -1l
        );
        long retweetedByUserId = intent.getLongExtra(
                getString(R.string.detail_intent_retweeted_by_user_id),
                -1l
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
                -1l
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
                        getTweetFromIntent().getStatusId()
                );
                intent.putExtra(
                        getString(R.string.post_intent_status_screen_name),
                        getTweetFromIntent().getScreenName()
                );
                startActivity(intent);
                anim.fade(DetailActivity.this);
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
                showItemLongClickDialog(position);
                return true;
            }
        });

        detailInitTask = new DetailInitTask(this);
        detailInitTask.execute();
    }

    private AlertDialog alertDialog;
    private boolean deleteFromDetail = false;
    private boolean retweetFromDetail = false;
    private boolean favoriteFromDetail = false;
    public void setDeleteFromDetail(boolean deleteFromDetail) {
        this.deleteFromDetail = deleteFromDetail;
    }
    public void setRetweetFromDetail(boolean retweetFromDetail) {
        this.retweetFromDetail = retweetFromDetail;
    }
    public void setFavoriteFromDetail(boolean favoriteFromDetail) {
        this.favoriteFromDetail = favoriteFromDetail;
    }
    private void quote(int location) {
        Intent intent = new Intent(this, PostActivity.class);
        ActivityAnim anim = new ActivityAnim();
        intent.putExtra(
                getString(R.string.post_intent_flag),
                Flag.POST_QUOTE
        );
        intent.putExtra(
                getString(R.string.post_intent_status_id),
                tweetList.get(location).getStatusId()
        );
        intent.putExtra(
                getString(R.string.post_intent_status_screen_name),
                tweetList.get(location).getScreenName()
        );
        intent.putExtra(
                getString(R.string.post_intent_status_text),
                tweetList.get(location).getText()
        );
        startActivity(intent);
        anim.fade(this);
    }
    private void clip(int location) {
        ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        String text = tweetList.get(location).getText();
        ClipData data = ClipData.newPlainText(
                getString(R.string.tweet_copy_label),
                text
        );
        manager.setPrimaryClip(data);
        Toast.makeText(
                this,
                R.string.tweet_notification_copy_successful,
                Toast.LENGTH_SHORT
        ).show();
    }
    private void multipleAtOne(int flag, int location) {
        switch (flag) {
            case Flag.STATUS_NONE:
                detailRetweetTask = new DetailRetweetTask(this, location);
                detailRetweetTask.execute();
                break;
            case Flag.STATUS_RETWEETED_BY_ME:
                Toast.makeText(
                        this,
                        R.string.context_toast_already_retweet,
                        Toast.LENGTH_SHORT
                ).show();
                break;
            case Flag.STATUS_SENT_BY_ME:
                detailDeleteTask = new DetailDeleteTask(this, location);
                detailDeleteTask.execute();
                break;
            default:
                break;
        }
    }
    private void showItemLongClickDialog(final int location) {
        LinearLayout linearLayout = (LinearLayout) getLayoutInflater()
                .inflate(
                        R.layout.context_menu,
                        null
                );
        ListView menu = (ListView) linearLayout.findViewById(R.id.context_menu_listview);
        List<String> menuItemList = new ArrayList<String>();

        final int flag;
        final Tweet tweet = tweetList.get(location);
        menuItemList.add(getString(R.string.context_menu_item_quote));
        if (tweet.getRetweetedByUserId() != -1l && tweet.getRetweetedByUserId() == useId) {
            flag = Flag.STATUS_RETWEETED_BY_ME;
            menuItemList.add(getString(R.string.context_menu_item_retweet));
        } else {
            if (tweet.getUserId() != useId) {
                flag = Flag.STATUS_NONE;
                menuItemList.add(getString(R.string.context_menu_item_retweet));
            } else {
                flag = Flag.STATUS_SENT_BY_ME;
                menuItemList.add(getString(R.string.context_menu_item_delete));
            }
        }
        menuItemList.add(getString(R.string.context_menu_item_favorite));
        menuItemList.add(getString(R.string.context_menu_item_copy));

        ContextMenuAdapter contextMenuAdapter = new ContextMenuAdapter(
                this,
                R.layout.context_menu_item,
                menuItemList
        );
        menu.setAdapter(contextMenuAdapter);
        contextMenuAdapter.notifyDataSetChanged();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(linearLayout);
        builder.setCancelable(true);
        alertDialog = builder.create();
        alertDialog.show();

        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        quote(location);
                        alertDialog.hide();
                        alertDialog.dismiss();
                        break;
                    case 1:
                        multipleAtOne(flag, location);
                        alertDialog.hide();
                        alertDialog.dismiss();
                        break;
                    case 2:
                        if (!tweet.isFavorite()) {
                            detailFavoriteTask = new DetailFavoriteTask(
                                    DetailActivity.this,
                                    location
                            );
                            detailFavoriteTask.execute();
                        } else {
                            Toast.makeText(
                                    DetailActivity.this,
                                    R.string.context_toast_already_favorite,
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                        alertDialog.hide();
                        alertDialog.dismiss();
                        break;
                    case 3:
                        clip(location);
                        alertDialog.hide();
                        alertDialog.dismiss();
                        break;
                    default:
                        alertDialog.hide();
                        alertDialog.dismiss();
                        break;
                }
            }
        });
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
                deleteFromDetail
        );
        intent.putExtra(
                getString(R.string.detail_intent_is_retweet_at_detail),
                retweetFromDetail
        );
        intent.putExtra(
                getString(R.string.detail_intent_is_favorite_at_detail),
                favoriteFromDetail
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
