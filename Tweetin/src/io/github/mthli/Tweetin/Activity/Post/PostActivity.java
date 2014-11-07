package io.github.mthli.Tweetin.Activity.Post;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import io.github.mthli.Tweetin.R;
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
                /* Do something */
                break;
            case Flag.POST_QUOTE:
                /* Do something */
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
                            Flag.POST_PHOTO
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
                /* Do something */
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /* Do something */
    }

    /* Do something */

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
