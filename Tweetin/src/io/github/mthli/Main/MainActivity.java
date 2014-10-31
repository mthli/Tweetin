package io.github.mthli.Main;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Timeline.TimelineFragment;

public class MainActivity extends FragmentActivity {
    private TimelineFragment timelineFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        timelineFragment = (TimelineFragment) getSupportFragmentManager()
                .findFragmentById(R.id.timeline_fragment);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            /* Do something */
        }
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

    /* Do something */
}
