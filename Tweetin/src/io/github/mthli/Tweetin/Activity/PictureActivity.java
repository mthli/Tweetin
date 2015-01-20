package io.github.mthli.Tweetin.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;
import io.github.mthli.Tweetin.Fragment.Picture.PictureFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.View.ViewUnit;

public class PictureActivity extends FragmentActivity {
    private PictureFragment pictureFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewUnit.setCustomTheme(this);
        setContentView(R.layout.picture);

        Toolbar toolbar = (Toolbar) findViewById(R.id.picture_toolbar);
        ViewCompat.setElevation(toolbar, ViewUnit.getElevation(this, 2));

        setActionBar(toolbar);
        getActionBar().setTitle(getString(R.string.picture_label));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        pictureFragment = (PictureFragment) getSupportFragmentManager().findFragmentById(R.id.picture_fragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.picture_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                pictureFragment.cancelAllTasks();
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.picture_menu_description:
                final TextView decriptionView = pictureFragment.getDescriptionView();
                if (decriptionView.getVisibility() == View.VISIBLE) {
                    decriptionView.animate()
                            .alpha(0f)
                            .setDuration(getResources().getInteger(android.R.integer.config_longAnimTime))
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    decriptionView.setVisibility(View.GONE);
                                }
                            });
                } else {
                    decriptionView.setAlpha(0);
                    decriptionView.setVisibility(View.VISIBLE);
                    decriptionView.animate()
                            .alpha(1f)
                            .setDuration(getResources().getInteger(android.R.integer.config_longAnimTime))
                            .setListener(null);
                }
                break;
            case R.id.picture_menu_save:
                // TODO
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            pictureFragment.cancelAllTasks();
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        return true;
    }

    @Override
    public void onDestroy() {
        pictureFragment.cancelAllTasks();
        super.onDestroy();
    }

    // TODO: onActivityResult()
}
