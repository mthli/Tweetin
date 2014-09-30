package io.github.mthli.Tweetin.Main;

import android.app.ActionBar;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.*;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import io.github.mthli.Tweetin.R;

public class MainActivity extends FragmentActivity {
    private ActionBar actionBar;

    private MainFragment fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager manager = new SystemBarTintManager(this);
            manager.setStatusBarTintEnabled(true);
            int color = getResources().getColor(R.color.teal_default);
            manager.setTintColor(color);
        }

        actionBar = getActionBar();
        actionBar.setTitle(null);
        actionBar.setSubtitle(null);

        /* Do something */
        fragment = (MainFragment) getSupportFragmentManager().findFragmentById(
                R.id.main_fragment
        );
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

    private MenuItem notification;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        notification = menu.findItem(R.id.main_menu_notification);
        return true;
    }

    public void setNotificationStatus(boolean hasNotification) {
        if (hasNotification) {
            notification.setIcon(R.drawable.ic_action_notification_active);
        } else {
            notification.setIcon(R.drawable.ic_action_notification_default);
        }
    }

    int count = 0;
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.main_menu_notification:
                if (count % 2 == 0) {
                    setNotificationStatus(true);
                } else {
                    setNotificationStatus(false);
                }
                count++;
                /* Do something */
                break;
            case R.id.main_menu_collection:
                /* Do something */
                break;
            case R.id.main_menu_about:
                /* Do something */
                break;
            case R.id.main_menu_sign_out:
                /* Do something */
            default:
                break;
        }
        return true;
    }
}
