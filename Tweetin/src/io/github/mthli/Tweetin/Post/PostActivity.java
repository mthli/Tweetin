package io.github.mthli.Tweetin.Post;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Flag;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class PostActivity extends Activity {
    private Twitter twitter;
    public Twitter getTwitter() {
        return twitter;
    }

    private int postFlag = 0;

    private ImageView postImage;
    private AutoCompleteTextView postText;
    private ToggleButton checkIn;
    private ToggleButton chooseImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post);

        /* Do something */
        Intent intent = getIntent();
        postFlag = intent.getIntExtra(getString(R.string.post_flag), 0);
        switch (postFlag) {
            case Flag.POST_ORIGINAL:
                break;
            case Flag.POST_REPLY:
                /* Do something */
                break;
            case Flag.POST_RETWEET_WITH_COMMENT:
                /* Do something */
                break;
            default:
                break;
        }

        SharedPreferences preferences = getSharedPreferences(
                getString(R.string.sp_name),
                Context.MODE_PRIVATE
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager manager = new SystemBarTintManager(this);
            manager.setStatusBarTintEnabled(true);
            int color = getResources().getColor(R.color.tumblr_dark_blue);
            manager.setTintColor(color);
        }

        ActionBar actionBar = getActionBar();
        actionBar.setTitle(null);
        actionBar.setDisplayHomeAsUpEnabled(true);

        /* Do something */
        postImage = (ImageView) findViewById(R.id.post_image);
        postText = (AutoCompleteTextView) findViewById(R.id.post_text);
        checkIn = (ToggleButton) findViewById(R.id.check_in);
        chooseImage = (ToggleButton) findViewById(R.id.choose_image);

        /* Do something */
        chooseImage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    /* Do something */
                } else {
                    postImage.setVisibility(View.GONE);
                }
            }
        });

        /* Do something */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                /* DO something */
                break;
            case R.id.post_send:
                /* Do something */
                break;
            default:
                break;
        }
        return true;
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
