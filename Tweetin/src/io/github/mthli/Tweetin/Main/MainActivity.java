package io.github.mthli.Tweetin.Main;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import io.github.mthli.Tweetin.R;

public class MainActivity extends Activity {
    private ActionBar actionBar;

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

    private MenuItem notifiMenu;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        notifiMenu = menu.findItem(R.id.main_menu_notification);
        return true;
    }

    public void changeNotifiMenuState(boolean hasNotification) {
        if (hasNotification) {
            notifiMenu.setIcon(R.drawable.ic_action_notification_active);
        } else {
            notifiMenu.setIcon(R.drawable.ic_action_notification_default);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.main_menu_notification:
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
