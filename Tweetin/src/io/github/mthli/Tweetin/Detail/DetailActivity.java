package io.github.mthli.Tweetin.Detail;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.mthli.Tweetin.Post.PostActivity;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.Base.Tweet;
import io.github.mthli.Tweetin.Unit.ActivityAnim;
import io.github.mthli.Tweetin.Unit.Flag;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailActivity extends FragmentActivity {
    private Twitter twitter;
    private long useId;
    public Twitter getTwitter() {
        return twitter;
    }
    public long getUseId() {
        return useId;
    }

    private DetailLoadTask detailLoadTask;
    private DetailRetweetTask detailRetweetTask;
    private void allTaskDown() {
        if (detailLoadTask != null && detailLoadTask.getStatus() == AsyncTask.Status.RUNNING) {
            detailLoadTask.cancel(true);
        }
        if (detailRetweetTask != null && detailRetweetTask.getStatus() == AsyncTask.Status.RUNNING) {
            detailRetweetTask.cancel(true);
        }
    }

    private int position = 0;
    private boolean isRetweetFromDetail = false;
    /* detail_this_status */
    private Tweet thisTweet;
    private CircleImageView thisStatusAvatar;
    private TextView thisStatusCreatedAt;
    private TextView thisStatusName;
    private TextView thisStatusScreenName;
    private TextView thisStatusProtect;
    private TextView thisStatusText;
    private ImageView thisStatusPicture;
    private TextView thisStatusCheckIn;
    private TextView thisStatusRetweetedByName;
    /* detail_option */
    private Button detailQuote;
    private Button detailRetweet;
    private Button detailReply;
    public Tweet getThisTweet() {
        return thisTweet;
    }
    public TextView getThisStatusText() {
        return thisStatusText;
    }
    public ImageView getThisStatusPicture() {
        return thisStatusPicture;
    }
    public TextView getThisStatusRetweetedByName() {
        return thisStatusRetweetedByName;
    }
    public Button getDetailRetweet() {
        return detailRetweet;
    }

    private void findView() {
        /* detail_this_status */
        thisStatusAvatar = (CircleImageView) findViewById(
                R.id.detail_this_status_avatar
        );
        thisStatusCreatedAt = (TextView) findViewById(
                R.id.detail_this_status_created_at
        );
        thisStatusName = (TextView) findViewById(
                R.id.detail_this_status_name
        );
        thisStatusScreenName = (TextView) findViewById(
                R.id.detail_this_status_screen_name
        );
        thisStatusProtect = (TextView) findViewById(
                R.id.detail_this_status_protect
        );
        thisStatusText = (TextView) findViewById(
                R.id.detail_this_status_text
        );
        thisStatusPicture = (ImageView) findViewById(
                R.id.detail_this_status_picture
        );
        thisStatusCheckIn = (TextView) findViewById(
                R.id.detail_this_status_check_in
        );
        thisStatusRetweetedByName = (TextView) findViewById(
                R.id.detail_this_status_retweeted_by_name
        );
        detailQuote = (Button) findViewById(
                R.id.detail_quote
        );
        detailRetweet = (Button) findViewById(
                R.id.detail_retweet
        );
        detailReply = (Button) findViewById(
                R.id.detail_reply
        );
    }
    private void setThisTweet() {
        Intent intent = getIntent();
        position = intent.getIntExtra(
                getString(R.string.detail_from_position),
                0
        );
        thisTweet = new Tweet();
        thisTweet.setTweetId(
                intent.getLongExtra(
                        getString(R.string.detail_intent_tweet_id),
                        0
                )
        );
        thisTweet.setUserId(
                intent.getLongExtra(
                        getString(R.string.detail_intent_user_id),
                        0
                )
        );
        thisTweet.setAvatarUrl(
                intent.getStringExtra(
                        getString(R.string.detail_intent_avatar_url)
                )
        );
        thisTweet.setCreatedAt(
                intent.getStringExtra(
                        getString(R.string.detail_intent_created_at)
                )
        );
        thisTweet.setName(
                intent.getStringExtra(
                        getString(R.string.detail_intent_name)
                )
        );
        thisTweet.setScreenName(
                intent.getStringExtra(
                        getString(R.string.detail_intent_screen_name)
                )
        );
        thisTweet.setProtect(
                intent.getBooleanExtra(
                        getString(R.string.detail_intent_protect),
                        false
                )
        );
        thisTweet.setText(
                intent.getStringExtra(
                        getString(R.string.detail_intent_text)
                )
        );
        thisTweet.setCheckIn(
                intent.getStringExtra(
                        getString(R.string.detail_intent_check_in)
                )
        );
        thisTweet.setRetweet(
                intent.getBooleanExtra(
                        getString(R.string.detail_intent_retweet),
                        false
                )
        );
        thisTweet.setRetweetedByName(
                intent.getStringExtra(
                        getString(R.string.detail_intent_retweeted_by_name)
                )
        );
        thisTweet.setRetweetedById(
                intent.getLongExtra(
                        getString(R.string.detail_intent_retweeted_by_id),
                        0
                )
        );
        thisTweet.setReplyTo(
                intent.getLongExtra(
                        getString(R.string.detail_intent_reply_to),
                        -1
                )
        );
    }
    private String getShortCreatedAt(String createdAt) {
        SimpleDateFormat format = new SimpleDateFormat(
                getString(R.string.tweet_date_format)
        );
        Date date = new Date();
        String str = format.format(date);
        String[] arrD = str.split(" ");
        String[] arrC = createdAt.split(" ");
        if (arrD[1].equals(arrC[1])) {
            return arrC[0];
        } else {
            return createdAt;
        }
    }
    private void thisStatusAdapter() {
        Glide.with(this).load(thisTweet.getAvatarUrl())
                .crossFade().into(thisStatusAvatar);
        thisStatusCreatedAt.setText(
                getShortCreatedAt(thisTweet.getCreatedAt())
        );
        thisStatusName.setText(thisTweet.getName());
        if (thisTweet.getScreenName().startsWith("@")) {
            thisStatusScreenName.setText(thisTweet.getScreenName());
        } else {
            thisStatusScreenName.setText("@" + thisTweet.getScreenName());
        }
        if (thisTweet.isProtected()) {
            thisStatusProtect.setVisibility(View.VISIBLE);
            detailRetweet.setVisibility(View.GONE);
        }
        thisStatusText.setText(thisTweet.getText());
        if (thisTweet.getCheckIn() != null) {
            thisStatusCheckIn.setVisibility(View.VISIBLE);
            thisStatusCheckIn.setText(thisTweet.getCheckIn());
        }
        if (thisTweet.isRetweet()) {
            thisStatusRetweetedByName.setVisibility(View.VISIBLE);
            thisStatusRetweetedByName.setText(
                    thisTweet.getRetweetedByName()
            );
            if (thisTweet.getRetweetedById() == useId) {
                detailRetweet.setVisibility(View.GONE);
            }
        }
        if (thisTweet.getUserId() == useId) {
            detailRetweet.setVisibility(View.GONE);
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager manager = new SystemBarTintManager(this);
            manager.setStatusBarTintEnabled(true);
            int color = getResources().getColor(R.color.tumblr_dark_blue);
            manager.setTintColor(color);
        }
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(null);
        actionBar.setSubtitle(null);
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
        final TwitterFactory factory = new TwitterFactory();
        twitter = factory.getInstance();
        twitter.setOAuthConsumer(conKey, conSecret);
        AccessToken token = new AccessToken(accToken, accTokenSecret);
        twitter.setOAuthAccessToken(token);

        findView();
        setThisTweet();
        thisStatusAdapter();

        detailQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, PostActivity.class);
                ActivityAnim anim = new ActivityAnim();
                intent.putExtra(
                        getString(R.string.post_flag),
                        Flag.POST_RETWEET_QUOTE
                );
                intent.putExtra(
                        getString(R.string.post_quote_status_id),
                        thisTweet.getTweetId()
                );
                intent.putExtra(
                        getString(R.string.post_quote_screen_name),
                        thisTweet.getScreenName()
                );
                intent.putExtra(
                        getString(R.string.post_quote_text),
                        thisTweet.getText()
                );
                startActivity(intent);
                anim.fade(DetailActivity.this);
            }
        });

        detailRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRetweetFromDetail = true;
                thisStatusRetweetedByName.setText(
                        getString(R.string.detail_retweeted_by_me)
                );
                thisStatusRetweetedByName.setVisibility(View.VISIBLE);
                detailRetweet.setVisibility(View.GONE);
                detailRetweetTask = new DetailRetweetTask(DetailActivity.this);
                detailRetweetTask.execute();
            }
        });

        detailReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, PostActivity.class);
                ActivityAnim anim = new ActivityAnim();
                intent.putExtra(
                        getString(R.string.post_flag),
                        Flag.POST_REPLY
                );
                intent.putExtra(
                        getString(R.string.post_reply_status_id),
                        thisTweet.getTweetId()
                );
                intent.putExtra(
                        getString(R.string.post_reply_screen_name),
                        thisTweet.getScreenName()
                );
                startActivity(intent);
                anim.fade(DetailActivity.this);
            }
        });

        detailLoadTask = new DetailLoadTask(this);
        detailLoadTask.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                allTaskDown();
                ActivityAnim anim = new ActivityAnim();
                detailFinish();
                anim.rightOut(this);
                break;
            case R.id.detail_menu_copy:
                /* Do something */
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            allTaskDown();
            ActivityAnim anim = new ActivityAnim();
            detailFinish();
            anim.rightOut(this);
        }

        return true;
    }

    @Override
    public void onDestroy() {
        detailFinish();
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

    private void detailFinish() {
        Intent intent = new Intent();
        intent.putExtra(
                getString(R.string.detail_from_position),
                position
        );
        intent.putExtra(
                getString(R.string.detail_intent_is_retweet_from_detail),
                isRetweetFromDetail
        );
        allTaskDown();
        setResult(Activity.RESULT_OK, intent);
        ActivityAnim anim = new ActivityAnim();
        finish();
        anim.rightOut(DetailActivity.this);
    }
}
