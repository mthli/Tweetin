package io.github.mthli.Tweetin.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Task.GetAuthorizationURLTask;

public class SplashActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.sp_tweetin),
                MODE_PRIVATE
        );

        String useScreenName = sharedPreferences.getString(
                getString(R.string.sp_use_screen_name),
                null
        );
        if (useScreenName != null) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);

            finish();
        }

        setTheme(R.style.BaseAppTheme_Blue);
        setContentView(R.layout.splash);

        Button signInButton = (Button) findViewById(R.id.splash_sign_in);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetAuthorizationURLTask getAuthorizationURLTask = new GetAuthorizationURLTask(SplashActivity.this);
                getAuthorizationURLTask.execute();
            }
        });
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
