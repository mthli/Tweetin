package io.github.mthli.Tweetin.Mention;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.Base.Tweet;
import io.github.mthli.Tweetin.Tweet.Base.TweetAdapter;
import io.github.mthli.Tweetin.Unit.ActivityAnim;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import java.util.List;

public class MentionActivity extends FragmentActivity {
    private Twitter twitter;
    private long useId;
    public Twitter getTwitter() {
        return twitter;
    }
    public long getUseId() {
        return useId;
    }

    private MentionFragment mentionFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mention);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager manager = new SystemBarTintManager(this);
            manager.setStatusBarTintEnabled(true);
            manager.setNavigationBarTintEnabled(true);
            int color = getResources().getColor(R.color.tumblr_dark_blue);
            manager.setTintColor(color);
        }
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = getSharedPreferences(
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

        mentionFragment = (MentionFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mention_fragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mention_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                mentionFinish();
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            int position = data.getIntExtra(
                    getString(R.string.detail_intent_from_position),
                    0
            );
            boolean isRetweetFromDetail = data.getBooleanExtra(
                    getString(R.string.detail_intent_is_retweet_from_detail),
                    false
            );
            if (isRetweetFromDetail) {
                TweetAdapter tweetAdapter = mentionFragment.getTweetAdapter();
                List<Tweet> tweetList = mentionFragment.getTweetList();

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
            mentionFinish();
        }

        return true;
    }

    @Override
    public void onDestroy() {
        mentionFinish();
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

    private void mentionFinish() {
        Intent intent = new Intent();
        intent.putExtra(
                getString(R.string.mention_finish),
                mentionFragment.isMentionFinish()
        );
        mentionFragment.allTaskDown();
        setResult(RESULT_OK, intent);
        ActivityAnim anim = new ActivityAnim();
        finish();
        anim.rightOut(this);
    }
}
