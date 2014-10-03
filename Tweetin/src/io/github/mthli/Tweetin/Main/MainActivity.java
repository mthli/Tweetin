package io.github.mthli.Tweetin.Main;

import android.app.ActionBar;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.*;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import io.github.mthli.Tweetin.R;

public class MainActivity extends FragmentActivity {

    private MainFragment fragment;

    private MenuItem notification;
    private Drawable notificationDefault;
    private Drawable notificationActive;

    private void getNotificationActive() {
        notificationDefault = getResources().getDrawable(R.drawable.ic_action_notification);
        Bitmap bitmap = ((BitmapDrawable) notificationDefault).getBitmap();
        bitmap = bitmap.copy(bitmap.getConfig(), true);
        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.red_default));
        paint.setAntiAlias(true);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawCircle(
                bitmap.getWidth() - 11,
                bitmap.getHeight() / 5,
                7,
                paint
        );
        notificationActive = new BitmapDrawable(getResources(), bitmap);
    }

    public void setNotificationStatus(boolean hasNotification) {
        if (hasNotification) {
            notification.setIcon(notificationDefault);
        } else {
            notification.setIcon(notificationActive);
        }
    }

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

        ActionBar actionBar = getActionBar();
        actionBar.setTitle(null);
        actionBar.setSubtitle(null);
        getNotificationActive();

        /* Do something */
        fragment = (MainFragment) getSupportFragmentManager().findFragmentById(
                R.id.main_fragment
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        notification = menu.getItem(0);
        return true;
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
