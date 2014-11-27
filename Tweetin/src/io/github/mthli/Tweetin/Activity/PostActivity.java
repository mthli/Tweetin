package io.github.mthli.Tweetin.Activity;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Task.Post.PostTask;
import io.github.mthli.Tweetin.Unit.Anim.ActivityAnim;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import io.github.mthli.Tweetin.Unit.Picture.PictureUnit;
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

    private ImageView postPicture;
    private EditText postEdit;
    private ToggleButton postCheckInButton;
    private ToggleButton postPictureButton;
    private TextView countWords;
    private String picturePath = null;
    public EditText getPostEdit() {
        return postEdit;
    }
    public ToggleButton getPostCheckInButton() {
        return postCheckInButton;
    }
    public ToggleButton getPostPictureButton() {
        return postPictureButton;
    }
    public String getPicturePath() {
        return picturePath;
    }

    private void initPostOriginal() {
        countWords.setTextColor(
                getResources().getColor(R.color.hint)
        );
        countWords.setText("0");
    }
    private void initPostReply() {
        statusId = getIntent().getLongExtra(
                getString(R.string.post_intent_status_id),
                -1l
        );
        String reply = getIntent().getStringExtra(
                getString(R.string.post_intent_status_screen_name)
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
    }
    private void initPostQuote() {
        statusId = getIntent().getLongExtra(
                getString(R.string.post_intent_status_id),
                -1l
        );
        screenName = getIntent().getStringExtra(
                getString(R.string.post_intent_status_screen_name)
        );
        String quote = " RT ";
        if (getIntent().getStringExtra(getString(R.string.post_intent_status_screen_name)).startsWith("@")) {
            quote = quote
                    + getIntent().getStringExtra(getString(R.string.post_intent_status_screen_name))
                    + ": "
                    + getIntent().getStringExtra(getString(R.string.post_intent_status_text));
        } else {
            quote = quote
                    + " @"
                    + getIntent().getStringExtra(getString(R.string.post_intent_status_screen_name))
                    + ": "
                    + getIntent().getStringExtra(getString(R.string.post_intent_status_text));
        }
        postEdit.setText(quote);
        postEdit.setSelection(0);
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
    }
    private void initPostResend() {
        statusId = getIntent().getLongExtra(
                getString(R.string.post_intent_status_id),
                -1l
        );
        screenName = getIntent().getStringExtra(
                getString(R.string.post_intent_status_screen_name)
        );

        postCheckInButton.setChecked(
                getIntent().getBooleanExtra(
                        getString(R.string.post_intent_check_in),
                        false
                )
        );

        String resendText = getIntent().getStringExtra(
                getString(R.string.post_intent_status_text)
        );
        postEdit.setText(resendText);
        postEdit.setSelection(resendText.length());
        if (resendText.length() > 140) {
            countWords.setTextColor(
                    getResources().getColor(R.color.red)
            );
        } else {
            countWords.setTextColor(
                    getResources().getColor(R.color.hint)
            );
        }
        countWords.setText(String.valueOf(resendText.length()));

        if (getIntent().getBooleanExtra(
                getString(R.string.post_intent_picture), false
        )) {
            picturePath = getIntent().getStringExtra(
                    getString(R.string.post_intent_picture_path)
            );
            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
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
                postPicture.setImageBitmap(bitmap);
                postPicture.setVisibility(View.VISIBLE);
                postPictureButton.setChecked(true);
                postFlag = getIntent().getIntExtra(
                        getString(R.string.post_intent_resend_flag),
                        Flag.POST_SHARE
                );
                return;
            }
            if (bitmapHeight > screenHeight) {
                float percent = ((float) screenHeight) / ((float) bitmapHeight);
                Matrix matrix = new Matrix();
                matrix.postScale(percent, percent);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
                postPicture.setImageBitmap(bitmap);
                postPicture.setVisibility(View.VISIBLE);
                postPictureButton.setChecked(true);
                postFlag = getIntent().getIntExtra(
                        getString(R.string.post_intent_resend_flag),
                        Flag.POST_SHARE
                );
            }
        } else {
            postPicture.setVisibility(View.GONE);
            postPictureButton.setChecked(false);
            postFlag = getIntent().getIntExtra(
                    getString(R.string.post_intent_resend_flag),
                    Flag.POST_SHARE
            );
        }
    }
    private void initPostFeedback() {
        String feedbackStr = getString(R.string.setting_feedback_str) + " ";
        postEdit.setText(feedbackStr);
        postEdit.setSelection(feedbackStr.length());
        if (feedbackStr.length() > 140) {
            countWords.setTextColor(
                    getResources().getColor(R.color.red)
            );
        } else {
            countWords.setTextColor(
                    getResources().getColor(R.color.hint)
            );
        }
        countWords.setText(String.valueOf(feedbackStr.length()));
    }
    private void initPostShare() {
        String action = getIntent().getAction();
        String type = getIntent().getType();
        if (action.equals(Intent.ACTION_SEND) && type != null) {
            if (type.equals("text/plain")) {
                String shareText = getIntent().getStringExtra(Intent.EXTRA_TEXT);
                postEdit.setText(shareText);
                postEdit.setSelection(shareText.length());
                if (shareText.length() > 140) {
                    countWords.setTextColor(
                            getResources().getColor(R.color.red)
                    );
                } else {
                    countWords.setTextColor(
                            getResources().getColor(R.color.hint)
                    );
                }
                countWords.setText(String.valueOf(shareText.length()));
            }

            /* maybe do something with image/ */
        }
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

        postPicture = (ImageView) findViewById(R.id.post_photo);
        postEdit = (EditText) findViewById(R.id.post_text);
        postCheckInButton = (ToggleButton) findViewById(R.id.post_check_in_button);
        postPictureButton = (ToggleButton) findViewById(R.id.post_picture_button);
        countWords = (TextView) findViewById(R.id.post_count_words);
        Button postSendButton = (Button) findViewById(R.id.post_send_button);

        postCheckInButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(
                        PostActivity.this,
                        R.string.post_toast_check_in,
                        Toast.LENGTH_SHORT
                ).show();

                return true;
            }
        });

        postPictureButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (postFlag != Flag.POST_RESEND) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_PICK);
                        startActivityForResult(
                                intent,
                                Flag.POST_PHOTO //
                        );
                    }
                } else {
                    postPicture.setVisibility(View.GONE);
                }
            }
        });
        postPictureButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(
                        PostActivity.this,
                        R.string.post_toast_picture,
                        Toast.LENGTH_SHORT
                ).show();

                return true;
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
                Vibrator vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(64);

                String text = postEdit.getText().toString();
                if (text.length() <= 0) {
                    Toast.makeText(
                            PostActivity.this,
                            R.string.post_toast_have_not_input_anything,
                            Toast.LENGTH_SHORT
                    ).show();
                } else if (text.length() > 140) {
                    Toast.makeText(
                            PostActivity.this,
                            R.string.post_toast_tweet_too_long,
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    ActivityAnim anim = new ActivityAnim();
                    PostTask postTask = new PostTask(PostActivity.this);
                    postTask.execute();
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(postEdit.getWindowToken(), 0);
                    finish();
                    anim.fade(PostActivity.this);
                }
            }
        });
        postSendButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(
                        PostActivity.this,
                        R.string.post_toast_send,
                        Toast.LENGTH_SHORT
                ).show();

                return true;
            }
        });

        postFlag = getIntent().getIntExtra(
                getString(R.string.post_intent_flag),
                Flag.POST_SHARE
        );
        switch (postFlag) {
            case Flag.POST_ORIGINAL:
                initPostOriginal();
                break;
            case Flag.POST_REPLY:
                initPostReply();
                break;
            case Flag.POST_QUOTE:
                initPostQuote();
                break;
            case Flag.POST_RESEND:
                initPostResend();
                break;
            case Flag.POST_FEEDBACK:
                initPostFeedback();
                break;
            case Flag.POST_SHARE:
                initPostShare();
                break;
            default:
                initPostOriginal();
                break;
        }
    }

    private void intentWithPicture(Intent data) {
        picturePath = PictureUnit.getPicturePathFromIntent(
                this,
                data
        );
        postPicture.setImageBitmap(
                PictureUnit.fixBitmap(
                        this,
                        BitmapFactory.decodeFile(picturePath)
                )
        );
        postPicture.setVisibility(View.VISIBLE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            postPicture.setVisibility(View.GONE);
            postPictureButton.setChecked(false);
            return;
        }
        if (requestCode == Flag.POST_PHOTO) {
            intentWithPicture(data);
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
        } else{
            /* Do nothing */
        }
    }
}
