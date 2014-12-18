package io.github.mthli.Tweetin.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Task.Initialize.GetAuthorizationURLTask;

public class SplashActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.sp_name),
                MODE_PRIVATE
        );

        long useId = sharedPreferences.getLong(
                getString(R.string.sp_use_id),
                -1l
        );
        if (useId > 0l) {
            /* Do something maybe */
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
