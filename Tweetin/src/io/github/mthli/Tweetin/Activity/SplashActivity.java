package io.github.mthli.Tweetin.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Task.OAuth.GetAuthorizationURLTask;

public class SplashActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String useScreenName = getSharedPreferences(getString(R.string.sp_tweetin), MODE_PRIVATE)
                .getString(getString(R.string.sp_use_screen_name), null);
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
                (new GetAuthorizationURLTask(SplashActivity.this)).execute();
            }
        });
    }
}
