package io.github.mthli.Tweetin.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import io.github.mthli.Tweetin.Flag.FlagUnit;
import io.github.mthli.Tweetin.Picture.PictureUnit;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Task.PostTask;
import io.github.mthli.Tweetin.Twitter.TwitterUnit;
import io.github.mthli.Tweetin.View.ViewUnit;

public class PostActivity extends Activity {
    private SharedPreferences sharedPreferences;

    private ImageView postPicture;
    private EditText postText;
    private ToggleButton checkInButton;
    private ToggleButton pictureButton;
    private TextView countWords;

    private long inReplyToStatusId = -1l;
    public long getInReplyToStatusId() {
        return inReplyToStatusId;
    }

    private String inReplyToScreenName = null;
    public String getInReplyToScreenName() {
        return inReplyToScreenName;
    }

    private String picturePath = null;
    public String getPicturePath() {
        return picturePath;
    }

    private String text = "";
    public String getText() {
        if (text == null) {
            return "";
        }

        return text;
    }

    private boolean checkIn = false;
    public boolean isCheckIn() {
        return checkIn;
    }

    private void setPostOptionTheme() {
        int spColorValue = sharedPreferences.getInt(
                getString(R.string.sp_color),
                0
        );

        switch (spColorValue) {
            case FlagUnit.COLOR_BLUE:
                checkInButton.setBackgroundDrawable(getDrawable(R.drawable.post_option_check_in_blue_selector));
                pictureButton.setBackgroundDrawable(getDrawable(R.drawable.post_option_picture_blue_selector));
                break;
            case FlagUnit.COLOR_ORANGE:
                checkInButton.setBackgroundDrawable(getDrawable(R.drawable.post_option_check_in_orange_selector));
                pictureButton.setBackgroundDrawable(getDrawable(R.drawable.post_option_picture_orange_selector));
                break;
            case FlagUnit.COLOR_PINK:
                checkInButton.setBackgroundDrawable(getDrawable(R.drawable.post_option_check_in_pink_selector));
                pictureButton.setBackgroundDrawable(getDrawable(R.drawable.post_option_picture_pink_selector));
                break;
            case FlagUnit.COLOR_PURPLE:
                checkInButton.setBackgroundDrawable(getDrawable(R.drawable.post_option_check_in_purple_selector));
                pictureButton.setBackgroundDrawable(getDrawable(R.drawable.post_option_picture_purple_selector));
                break;
            case FlagUnit.COLOR_TEAL:
                checkInButton.setBackgroundDrawable(getDrawable(R.drawable.post_option_check_in_teal_selector));
                pictureButton.setBackgroundDrawable(getDrawable(R.drawable.post_option_picture_teal_selector));
                break;
            default:
                checkInButton.setBackgroundDrawable(getDrawable(R.drawable.post_option_check_in_blue_selector));
                pictureButton.setBackgroundDrawable(getDrawable(R.drawable.post_option_picture_blue_selector));
                break;
        }
    }

    private void setCountWordsStatus() {
        if (text.length() <= 140) {
            countWords.setTextColor(getResources().getColor(R.color.secondary_text));
        } else {
            countWords.setTextColor(getResources().getColor(R.color.red_500));
        }
        countWords.setText(String.valueOf(text.length()));
    }

    private void initPostWithReply() {
        inReplyToStatusId = getIntent().getLongExtra(getString(R.string.post_intent_in_reply_to_status_id), -1);
        inReplyToScreenName = getIntent().getStringExtra(getString(R.string.post_intent_in_reply_to_screen_name));

        text = "@" + inReplyToScreenName + " ";

        postText.setText(text);
        postText.setSelection(text.length());

        setCountWordsStatus();
    }

    private void initPostWithQuote() {
        inReplyToStatusId = getIntent().getLongExtra(getString(R.string.post_intent_in_reply_to_status_id), -1);
        inReplyToScreenName = getIntent().getStringExtra(getString(R.string.post_intent_in_reply_to_screen_name));
        text = getIntent().getStringExtra(getString(R.string.post_intent_text));

        text = " RT @" + inReplyToScreenName + ": " + text;

        postText.setText(text);
        postText.setSelection(0);

        setCountWordsStatus();
    }

    private void initPostWithShare() {
        if (getIntent().getType() == null) {
            return;
        }

        if (getIntent().getType().equals("text/plain")) {
            text = getIntent().getStringExtra(Intent.EXTRA_TEXT);

            if (text != null) {
                postText.setText(text);
                postText.setSelection(text.length());
            } else {
                text = "";
            }

            setCountWordsStatus();
        } else if (getIntent().getType().startsWith("image/")) {
            picturePath = PictureUnit.getPicturePath(this, (Uri) getIntent().getParcelableExtra(Intent.EXTRA_STREAM));

            if (picturePath != null) {
                postPicture.setImageBitmap(PictureUnit.fixBitmap(this, BitmapFactory.decodeFile(picturePath)));
                postPicture.setVisibility(View.VISIBLE);

                pictureButton.setChecked(true);
            } else {
                picturePath = null; //

                postPicture.setVisibility(View.GONE);

                pictureButton.setChecked(false);
            }
        }
    }

    private void initPostWithResend() {
        /* Do something */
    }

    private void initPostWithAdvice() {
        inReplyToScreenName = TwitterUnit.getUseScreenNameFromSharedPreferences(this);

        text = "@" + inReplyToScreenName + " ";

        postText.setText(text);
        postText.setSelection(text.length());

        setCountWordsStatus();
    }

    private void initPostStatus() {
        int postFlag = getIntent().getIntExtra(getString(R.string.post_intent_post_flag), FlagUnit.POST_NEW);

        if (getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_SEND)) {
            postFlag = FlagUnit.POST_SHARE;
        }

        switch (postFlag) {
            case FlagUnit.POST_NEW:
                setCountWordsStatus();
                break;
            case FlagUnit.POST_REPLY:
                initPostWithReply();
                break;
            case FlagUnit.POST_QUOTE:
                initPostWithQuote();
                break;
            case FlagUnit.POST_SHARE:
                initPostWithShare();
                break;
            case FlagUnit.POST_RESEND:
                initPostWithResend();
                break;
            case FlagUnit.POST_ADVICE:
                initPostWithAdvice();
                break;
            default:
                break;
        }
    }

    private void initUI() {
        SystemBarTintManager systemBarTintManager = new SystemBarTintManager(this);
        systemBarTintManager.setNavigationBarTintEnabled(true);
        systemBarTintManager.setNavigationBarTintColor(getResources().getColor(R.color.black));

        Toolbar toolbar = (Toolbar) findViewById(R.id.post_toolbar);
        ViewCompat.setElevation(toolbar, ViewUnit.getElevation(this, 2));

        setActionBar(toolbar);
        getActionBar().setTitle(getString(R.string.post_label));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        postPicture = (ImageView) findViewById(R.id.post_picture);
        postText = (EditText) findViewById(R.id.post_text);
        checkInButton = (ToggleButton) findViewById(R.id.post_option_check_in);
        pictureButton = (ToggleButton) findViewById(R.id.post_option_picture);
        countWords = (TextView) findViewById(R.id.post_count_words);
        setPostOptionTheme();

        postText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                /* Do something */
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                /* Do nothing */
            }

            @Override
            public void afterTextChanged(Editable editable) {
                text = editable.toString();

                setCountWordsStatus();
            }
        });

        checkInButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (check && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    checkInButton.setChecked(false);

                    Toast.makeText(PostActivity.this, R.string.post_toast_check_in_failed, Toast.LENGTH_SHORT).show();

                    return;
                }

                checkIn = check;
            }
        });
        checkInButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(PostActivity.this, R.string.post_toast_check_in, Toast.LENGTH_SHORT).show();

                return true;
            }
        });

        pictureButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
                if (check) {
                    if (picturePath != null) {
                        return;
                    }

                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(intent, FlagUnit.REQUEST_PICTURE);
                } else {
                    picturePath = null;

                    postPicture.setVisibility(View.GONE);
                }
            }
        });
        pictureButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(PostActivity.this, R.string.post_toast_picture, Toast.LENGTH_SHORT).show();

                return true;
            }
        });

        initPostStatus();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(
                getString(R.string.sp_tweetin),
                MODE_PRIVATE
        );

        ViewUnit.setCustomTheme(this);
        setContentView(R.layout.post);

        initUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.post_menu_send:
                if (text.length() <= 0 && picturePath == null) {
                    Toast.makeText(this, R.string.post_toast_empty, Toast.LENGTH_SHORT).show();

                    break;
                }

                (new PostTask(this)).execute();

                finish();

                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            postPicture.setVisibility(View.GONE);
            pictureButton.setChecked(false);

            return;
        }

        if (requestCode == FlagUnit.REQUEST_PICTURE) {
            picturePath = PictureUnit.getPicturePath(this, data.getData());

            if (picturePath != null) {
                postPicture.setImageBitmap(PictureUnit.fixBitmap(this, BitmapFactory.decodeFile(picturePath)));
                postPicture.setVisibility(View.VISIBLE);
            } else {
                picturePath = null; //

                postPicture.setVisibility(View.GONE);

                pictureButton.setChecked(false);
            }
        }
    }
}
