package io.github.mthli.Tweetin.Activity;

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
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Task.Post.PostTask;
import io.github.mthli.Tweetin.Unit.Anim.ActivityAnim;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
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

    private long statusId = 0;
    private String screenName = null;
    public long getStatusId() {
        return statusId;
    }
    public String getScreenName() {
        return screenName;
    }

    private ImageView postPhoto;
    private EditText postEdit;
    private ToggleButton postCheckInButton;
    private ToggleButton postPhotoButton;
    private Button postSendButton;
    private TextView countWords;
    private String photoPath = null;
    public EditText getPostEdit() {
        return postEdit;
    }
    public ToggleButton getPostCheckInButton() {
        return postCheckInButton;
    }
    public ToggleButton getPostPhotoButton() {
        return postPhotoButton;
    }
    public String getPhotoPath() {
        return photoPath;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post);

        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.sp_name),
                Context.MODE_PRIVATE
        );
        final String consumerKey = sharedPreferences.getString(
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

        postPhoto = (ImageView) findViewById(R.id.post_photo);
        postEdit = (EditText) findViewById(R.id.post_text);
        postCheckInButton = (ToggleButton) findViewById(R.id.post_check_in_button);
        postPhotoButton = (ToggleButton) findViewById(R.id.post_photo_button);
        countWords = (TextView) findViewById(R.id.post_count_words);
        postSendButton = (Button) findViewById(R.id.post_send_button);

        postFlag = getIntent().getIntExtra(
                getString(R.string.post_flag),
                0
        );
        switch (postFlag) {
            case Flag.POST_ORIGINAL:
                countWords.setTextColor(
                        getResources().getColor(R.color.hint)
                );
                countWords.setText("0");
                break;
            case Flag.POST_REPLY:
                statusId = getIntent().getLongExtra(
                        getString(R.string.post_status_id),
                        -1
                );
                String reply = getIntent().getStringExtra(
                        getString(R.string.post_status_screen_name)
                );
                if (!reply.startsWith("@")) {
                    reply = "@" + reply;
                }
                screenName = reply;
                reply = reply + " ";
                postEdit.setText(reply);
                postEdit.setSelection(reply.length());

                if (reply.length() > 140) {
                    countWords.setTextColor(
                            getResources().getColor(R.color.red)
                    );
                } else {
                    countWords.setTextColor(
                            getResources().getColor(R.color.hint)
                    );
                }
                countWords.setText(String.valueOf(reply.length()));
                break;
            case Flag.POST_QUOTE:
                statusId = getIntent().getLongExtra(
                        getString(R.string.post_status_id),
                        -1
                );
                screenName = getIntent().getStringExtra(
                        getString(R.string.post_status_screen_name)
                );
                String quote = "RT ";
                if (getIntent().getStringExtra(getString(R.string.post_status_screen_name)).startsWith("@")) {
                    quote = quote
                            + getIntent().getStringExtra(getString(R.string.post_status_screen_name))
                            + ": "
                            + getIntent().getStringExtra(getString(R.string.post_status_text));
                } else {
                    quote = quote
                            + " @"
                            + getIntent().getStringExtra(getString(R.string.post_status_screen_name))
                            + ": "
                            + getIntent().getStringExtra(getString(R.string.post_status_text));
                }
                postEdit.setText(quote);
                postEdit.setSelection(quote.length());

                if (quote.length() > 140) {
                    countWords.setTextColor(
                            getResources().getColor(R.color.red)
                    );
                } else {
                    countWords.setTextColor(
                            getResources().getColor(R.color.hint)
                    );
                }
                countWords.setText(String.valueOf(quote.length()));
                break;
            default:
                break;
        }

        postPhotoButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(
                            intent,
                            Flag.POST_PHOTO //
                    );
                } else {
                    postPhoto.setVisibility(View.GONE);
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
                    countWords.setTextColor(
                            getResources().getColor(R.color.hint)
                    );
                    countWords.setText(String.valueOf(text.length()));
                } else {
                    countWords.setTextColor(
                            getResources().getColor(R.color.red)
                    );
                    countWords.setText(String.valueOf(text.length()));
                }
            }
        });

        postSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityAnim anim = new ActivityAnim();
                PostTask postTask = new PostTask(PostActivity.this);
                postTask.execute();
                finish();
                anim.fade(PostActivity.this);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            postPhoto.setVisibility(View.GONE);
            postPhotoButton.setChecked(false);
            return;
        }
        if (requestCode == Flag.POST_PHOTO) { //
            Uri uri = data.getData();
            String[] proj = {MediaStore.Images.Media.DATA};
            CursorLoader loader = new CursorLoader(this, uri, proj, null, null, null);
            Cursor cursor = loader.loadInBackground();
            int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            photoPath = cursor.getString(index);
            cursor.close();

            Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
            WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics metrics = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(metrics);
            int screenWidth = metrics.widthPixels;
            int screenHeight = metrics.heightPixels;
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            if (bitmapWidth > screenWidth) {
                float percent = ((float) screenWidth) / ((float) bitmapWidth);
                Matrix matrix = new Matrix();
                matrix.postScale(percent, percent);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
                postPhoto.setImageBitmap(bitmap);
                postPhoto.setVisibility(View.VISIBLE);
                return;
            }
            if (bitmapHeight > screenHeight) {
                float percent = ((float) screenHeight) / ((float) bitmapHeight);
                Matrix matrix = new Matrix();
                matrix.postScale(percent, percent);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
                postPhoto.setImageBitmap(bitmap);
                postPhoto.setVisibility(View.VISIBLE);
                return;
            }
            postPhoto.setImageBitmap(bitmap);
            postPhoto.setVisibility(View.VISIBLE);
        }
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
