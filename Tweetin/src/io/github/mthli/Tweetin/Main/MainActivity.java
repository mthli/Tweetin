package io.github.mthli.Tweetin.Main;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.*;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import io.github.mthli.Tweetin.Mention.MentionActivity;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.Base.Tweet;
import io.github.mthli.Tweetin.Tweet.Base.TweetAdapter;
import io.github.mthli.Tweetin.Unit.ActivityAnim;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import java.util.List;

public class MainActivity extends FragmentActivity {
    private Twitter twitter;
    private long useId;
    public Twitter getTwitter() {
        return twitter;
    }
    public long getUseId() {
        return useId;
    }

    private MainFragment mainFragment;

    private SharedPreferences preferences;

    private long latestMentionId = 0;
    private long tempLatestMentionId = 0;
    public void setTempLatestMentionId(long tempLatestMentionId) {
        this.tempLatestMentionId = tempLatestMentionId;
    }

    private MenuItem mention;
    private Drawable mentionDefault;
    private Drawable mentionActive;
    private boolean pressMention = false;
    public boolean isPressMention() {
        return pressMention;
    }
    public void setPressMention(Boolean pressMention) {
        this.pressMention = pressMention;
    }

    private void getNotificationActive() {
        mentionDefault = getResources().getDrawable(R.drawable.ic_action_mention);
        Bitmap bitmap = ((BitmapDrawable) mentionDefault).getBitmap();
        bitmap = bitmap.copy(bitmap.getConfig(), true);
        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.red_alert));
        paint.setAntiAlias(true);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawCircle(
                bitmap.getWidth() - 11,
                bitmap.getHeight() / 5,
                7,
                paint
        );
        mentionActive = new BitmapDrawable(getResources(), bitmap);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager manager = new SystemBarTintManager(this);
            manager.setStatusBarTintEnabled(true);
            int color = getResources().getColor(R.color.tumblr_dark_blue);
            manager.setTintColor(color);
        }
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(null);
        actionBar.setSubtitle(null);

        getNotificationActive();

        preferences = getSharedPreferences(
                getString(R.string.sp_name),
                Context.MODE_PRIVATE
        );
        useId = preferences.getLong(
                getString(R.string.sp_use_id),
                0
        );
        String conKey = preferences.getString(getString(R.string.sp_consumer_key), null);
        String conSecret = preferences.getString(getString(R.string.sp_consumer_secret), null);
        String accToken = preferences.getString(getString(R.string.sp_access_token), null);
        String accTokenSecret = preferences.getString(getString(R.string.sp_access_token_secret), null);
        TwitterFactory factory = new TwitterFactory();
        twitter = factory.getInstance();
        twitter.setOAuthConsumer(conKey, conSecret);
        AccessToken token = new AccessToken(accToken, accTokenSecret);
        twitter.setOAuthAccessToken(token);

        mainFragment = (MainFragment) getSupportFragmentManager()
                .findFragmentById(R.id.main_fragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        mention = menu.getItem(0);
        return true;
    }

    public void setMentionStatus(boolean hasMention) {
        if (hasMention) {
            mention.setIcon(mentionActive);
        } else {
            mention.setIcon(mentionDefault);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.main_menu_mention:
                pressMention = true;
                ActivityAnim anim = new ActivityAnim();
                Intent intent_mention = new Intent(this, MentionActivity.class);
                startActivityForResult(intent_mention, 0);
                anim.rightIn(this);
                break;
            case R.id.main_menu_about:
                /* Do something */
                break;
            case R.id.main_menu_sign_out:
                /* Do something */
            default:
                break;
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            boolean mentionFinish = data.getBooleanExtra(
                    getString(R.string.mention_finish),
                    false
            );
            if (mentionFinish) {
                setMentionStatus(false);
            } else {
                latestMentionId = preferences.getLong(
                        getString(R.string.sp_latest_mention_id),
                        0
                );
                if (tempLatestMentionId > latestMentionId) {
                    setMentionStatus(true);
                }
            }

            int position = data.getIntExtra(
                    getString(R.string.detail_intent_from_position),
                    0
            );
            boolean isRetweetFromDetail = data.getBooleanExtra(
                    getString(R.string.detail_intent_is_retweet_from_detail),
                    false
            );
            if (isRetweetFromDetail) {
                TweetAdapter tweetAdapter = mainFragment.getTweetAdapter();
                List<Tweet> tweetList = mainFragment.getTweetList();

                Tweet tweet = tweetList.get(position);
                Tweet newTweet = new Tweet();
                newTweet.setTweetId(tweet.getTweetId());
                newTweet.setUserId(tweet.getUserId());
                newTweet.setAvatarUrl(tweet.getAvatarUrl());
                newTweet.setCreatedAt(tweet.getCreatedAt());
                newTweet.setName(tweet.getName());
                newTweet.setScreenName(tweet.getScreenName());
                newTweet.setProtect(tweet.isProtected());
                newTweet.setText(tweet.getText());
                newTweet.setCheckIn(tweet.getCheckIn());
                newTweet.setRetweet(true);
                newTweet.setRetweetedByName(getString(R.string.tweet_retweeted_by_me));
                newTweet.setRetweetedById(useId);
                newTweet.setReplyTo(tweet.getReplyTo());

                tweetList.remove(position);
                tweetList.add(position, newTweet);
                tweetAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ActivityAnim anim = new ActivityAnim();
            mainFragment.allTaskDown();
            finish();
            anim.rightOut(this);
        }

        return true;
    }

    @Override
    public void onDestroy() {
        mainFragment.allTaskDown();
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
