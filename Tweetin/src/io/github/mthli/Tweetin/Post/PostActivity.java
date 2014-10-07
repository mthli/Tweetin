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
import android.util.DisplayMetrics;
import android.view.*;
import android.view.animation.Animation;
import android.widget.*;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import de.keyboardsurfer.android.widget.crouton.Crouton;
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
    private PostTask postTask;

    private ImageView postPic;
    private AutoCompleteTextView postText;
    private ToggleButton checkIn;
    private ToggleButton selectPic;
    private String picPath = null;
    public AutoCompleteTextView getPostText() {
        return postText;
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

        postPic = (ImageView) findViewById(R.id.post_picture);
        postText = (AutoCompleteTextView) findViewById(R.id.post_text);
        checkIn = (ToggleButton) findViewById(R.id.check_in);
        selectPic = (ToggleButton) findViewById(R.id.choose_image);

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

        /* Do something */
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
            if (bitmapWidth > screenWidth || bitmapHeight > screenHeight) {
                float percent = ((float) screenWidth) / ((float) bitmapWidth);
                Matrix matrix = new Matrix();
                matrix.postScale(percent, percent);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
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
                postTask = new PostTask(this);
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
        Crouton.cancelAllCroutons();
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
