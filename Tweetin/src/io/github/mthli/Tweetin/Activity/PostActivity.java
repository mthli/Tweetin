package io.github.mthli.Tweetin.Activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.*;
import android.widget.*;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import io.github.mthli.Tweetin.Flag.Flag;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.View.ViewUnit;

public class PostActivity extends Activity {

    private SharedPreferences sharedPreferences;

    private ToggleButton checkInButton;
    private ToggleButton pictureButton;

    private void setCustomTheme() {
        int spColorValue = sharedPreferences.getInt(
                getString(R.string.sp_color),
                0
        );

        switch (spColorValue) {
            case Flag.COLOR_BLUE:
                setTheme(R.style.BaseAppTheme_Blue);
                break;
            case Flag.COLOR_ORANGE:
                setTheme(R.style.BaseAppTheme_Orange);
                break;
            case Flag.COLOR_PINK:
                setTheme(R.style.BaseAppTheme_Pink);
                break;
            case Flag.COLOR_PURPLE:
                setTheme(R.style.BaseAppTheme_Purple);
                break;
            case Flag.COLOR_TEAL:
                setTheme(R.style.BaseAppTheme_Teal);
                break;
            default:
                setTheme(R.style.BaseAppTheme_Blue);
                break;
        }
    }

    private void setPostOptionTheme() {
        int spColorValue = sharedPreferences.getInt(
                getString(R.string.sp_color),
                0
        );

        switch (spColorValue) {
            case Flag.COLOR_BLUE:
                checkInButton.setBackgroundDrawable(getDrawable(R.drawable.post_option_check_in_blue_selector));
                pictureButton.setBackgroundDrawable(getDrawable(R.drawable.post_option_picture_blue_selector));
                break;
            case Flag.COLOR_ORANGE:
                checkInButton.setBackgroundDrawable(getDrawable(R.drawable.post_option_check_in_orange_selector));
                pictureButton.setBackgroundDrawable(getDrawable(R.drawable.post_option_picture_orange_selector));
                break;
            case Flag.COLOR_PINK:
                checkInButton.setBackgroundDrawable(getDrawable(R.drawable.post_option_check_in_pink_selector));
                pictureButton.setBackgroundDrawable(getDrawable(R.drawable.post_option_picture_pink_selector));
                break;
            case Flag.COLOR_PURPLE:
                checkInButton.setBackgroundDrawable(getDrawable(R.drawable.post_option_check_in_purple_selector));
                pictureButton.setBackgroundDrawable(getDrawable(R.drawable.post_option_picture_purple_selector));
                break;
            case Flag.COLOR_TEAL:
                checkInButton.setBackgroundDrawable(getDrawable(R.drawable.post_option_check_in_teal_selector));
                pictureButton.setBackgroundDrawable(getDrawable(R.drawable.post_option_picture_teal_selector));
                break;
            default:
                checkInButton.setBackgroundDrawable(getDrawable(R.drawable.post_option_check_in_blue_selector));
                pictureButton.setBackgroundDrawable(getDrawable(R.drawable.post_option_picture_blue_selector));
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

        checkInButton = (ToggleButton) findViewById(R.id.post_option_check_in);
        pictureButton = (ToggleButton) findViewById(R.id.post_option_picture);
        setPostOptionTheme();

        checkInButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                /* Do something */
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
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                /* Do something */
            }
        });
        pictureButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(PostActivity.this, R.string.post_toast_picture, Toast.LENGTH_SHORT).show();

                return true;
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(
                getString(R.string.sp_tweetin),
                MODE_PRIVATE
        );

        setCustomTheme();
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
                /* Do something */
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(menuItem);
    }
}
