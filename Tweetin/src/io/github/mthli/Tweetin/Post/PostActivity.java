package io.github.mthli.Tweetin.Post;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.*;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.ActivityAnim;
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
    public int getPostFlag() {
        return postFlag;
    }

    private long quoteStatusId = 0;
    private String quoteScreenName = null;
    private long replyStatusId = 0;
    private String replyScreenName = null;
    public long getQuoteStatusId() {
        return quoteStatusId;
    }
    public String getQuoteScreenName() {
        return quoteScreenName;
    }
    public long getReplyStatusId() {
        return replyStatusId;
    }
    public String getReplyScreenName() {
        return replyScreenName;
    }

    private ImageView postPic;
    private EditText postEdit;
    private ToggleButton checkIn;
    private ToggleButton selectPic;
    private TextView countWords;
    private String picPath = null;
    public EditText getPostEdit() {
        return postEdit;
    }
    public ToggleButton getCheckIn() {
        return checkIn;
    }
    public ToggleButton getSelectPic() {
        return selectPic;
    }
    public String getPicPath() {
        return picPath;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post);

        SharedPreferences preferences = getSharedPreferences(
                getString(R.string.sp_name),
                Context.MODE_PRIVATE
        );
        final String conKey = preferences.getString(getString(R.string.sp_consumer_key), null);
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

        postPic = (ImageView) findViewById(R.id.post_picture);
        postEdit = (EditText) findViewById(R.id.post_text);
        checkIn = (ToggleButton) findViewById(R.id.post_check_in);
        selectPic = (ToggleButton) findViewById(R.id.post_choose_image);
        countWords = (TextView) findViewById(R.id.post_count_words);

        Intent intent = getIntent();
        postFlag = intent.getIntExtra(getString(R.string.post_flag), 0);
        switch (postFlag) {
            case Flag.POST_ORIGINAL:
                countWords.setTextColor(getResources().getColor(R.color.hint));
                countWords.setText("0");
                break;
            case Flag.POST_REPLY:
                replyStatusId = intent.getLongExtra(
                        getString(R.string.post_reply_status_id),
                        0
                );
                String reply = intent.getStringExtra(
                        getString(R.string.post_reply_screen_name)
                );
                if (!reply.startsWith("@")) {
                    reply = "@" + reply;
                }
                replyScreenName = reply;
                reply = reply + " ";
                postEdit.setText(reply);
                postEdit.setSelection(reply.length());

                if (reply.length() > 140) {
                    countWords.setTextColor(getResources().getColor(R.color.red_alert));
                    countWords.setText(String.valueOf(reply.length()));
                } else {
                    countWords.setTextColor(getResources().getColor(R.color.hint));
                    countWords.setText(String.valueOf(reply.length()));
                }
                break;
            case Flag.POST_RETWEET_QUOTE:
                quoteStatusId = intent.getLongExtra(
                        getString(R.string.post_quote_status_id),
                        0
                );
                quoteScreenName = intent.getStringExtra(
                        getString(R.string.post_quote_screen_name)
                );
                String quote = "RT ";
                if (intent.getStringExtra(getString(R.string.post_quote_screen_name)).startsWith("@")) {
                    quote = quote
                            + intent.getStringExtra(getString(R.string.post_quote_screen_name))
                            + ": "
                            + intent.getStringExtra(getString(R.string.post_quote_text));
                } else {
                    quote = quote
                            + " @"
                            + intent.getStringExtra(getString(R.string.post_quote_screen_name))
                            + ": "
                            + intent.getStringExtra(getString(R.string.post_quote_text));
                }
                postEdit.setText(quote);

                if (quote.length() > 140) {
                    countWords.setTextColor(getResources().getColor(R.color.red_alert));
                    countWords.setText(String.valueOf(quote.length()));
                } else {
                    countWords.setTextColor(getResources().getColor(R.color.hint));
                    countWords.setText(String.valueOf(quote.length()));
                }
                break;
            default:
                break;
        }

        selectPic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(
                            intent,
                            Flag.POST_SELECT_PICTURE
                    );
                } else {
                    postPic.setVisibility(View.GONE);
                }
            }
        });

        postEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /* Do nothing */
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /* Do nothing */
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (text.length() <= 140) {
                    countWords.setTextColor(getResources().getColor(R.color.hint));
                    countWords.setText(String.valueOf(text.length()));
                } else {
                    countWords.setTextColor(getResources().getColor(R.color.red_alert));
                    countWords.setText(String.valueOf(text.length()));
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            postPic.setVisibility(View.GONE);
            selectPic.setChecked(false);
            return;
        }
        if (requestCode == Flag.POST_SELECT_PICTURE) {
            Uri uri = data.getData();
            String[] proj = { MediaStore.Images.Media.DATA };
            CursorLoader loader = new CursorLoader(this, uri, proj, null, null, null);
            Cursor cursor = loader.loadInBackground();
            int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            picPath = cursor.getString(index);
            cursor.close();

            Bitmap bitmap = BitmapFactory.decodeFile(picPath);
            WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics metrics = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(metrics);
            int screenWidth = metrics.widthPixels;
            int screenHeight = metrics.heightPixels;
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();

            /* Maybe do something with Bitmap OOM 2048 * 2048 */
            if (bitmapWidth > screenWidth) {
                float percent = ((float) screenWidth) / ((float) bitmapWidth);
                Matrix matrix = new Matrix();
                matrix.postScale(percent, percent);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
                postPic.setImageBitmap(bitmap);
                postPic.setVisibility(View.VISIBLE);
                return;
            }
            if (bitmapHeight > screenHeight) {
                float percent = ((float) screenHeight) / ((float) bitmapHeight);
                Matrix matrix = new Matrix();
                matrix.postScale(percent, percent);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
                postPic.setImageBitmap(bitmap);
                postPic.setVisibility(View.VISIBLE);
                return;
            }
            postPic.setImageBitmap(bitmap);
            postPic.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        ActivityAnim anim = new ActivityAnim();
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                anim.fade(this);
                break;
            case R.id.post_send:
                PostTask postTask = new PostTask(this);
                postTask.execute();
                finish();
                anim.fade(this);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ActivityAnim anim = new ActivityAnim();
            finish();
            anim.fade(this);
        }

        return true;
    }

    @Override
    public void onDestroy() {
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
