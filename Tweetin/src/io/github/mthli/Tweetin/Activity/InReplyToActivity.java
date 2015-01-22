package io.github.mthli.Tweetin.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import io.github.mthli.Tweetin.Fragment.TweetList.InReplyToFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.View.ViewUnit;

public class InReplyToActivity extends FragmentActivity {
    private InReplyToFragment inReplyToFragment;
    public InReplyToFragment getInReplyToFragment() {
        return inReplyToFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewUnit.setCustomTheme(this);
        setContentView(R.layout.in_reply_to);

        Toolbar toolbar = (Toolbar) findViewById(R.id.in_reply_to_toolbar);
        ViewCompat.setElevation(toolbar, ViewUnit.getElevation(this, 2));

        setActionBar(toolbar);
        getActionBar().setTitle(getString(R.string.in_reply_to_label));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        inReplyToFragment = (InReplyToFragment) getSupportFragmentManager().findFragmentById(R.id.in_reply_to_fragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.in_reply_to_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                inReplyToFragment.cancelAllTasks();
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            SlidingUpPanelLayout slidingUpPanelLayout = inReplyToFragment.getSlidingUpPanelLayout();
            if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            } else {
                inReplyToFragment.cancelAllTasks();
                finish();
            }
        }
        return true;
    }

    @Override
    public void onDestroy() {
        inReplyToFragment.cancelAllTasks();
        super.onDestroy();
    }
}
