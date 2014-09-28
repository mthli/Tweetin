package io.github.mthli.Tweetin.Splash;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import io.github.mthli.Tweetin.R;

public class SplashActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager manager = new SystemBarTintManager(this);
            manager.setStatusBarTintEnabled(true);
            int color = getResources().getColor(R.color.teal_default);
            manager.setTintColor(color);
        }

        Button signIn = (Button) findViewById(R.id.splash_sign_in_button);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignInDialog();
            }
        });

        Button signUp = (Button) findViewById(R.id.splash_sign_up_button);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignUpDialog();
            }
        });
    }

    private void showSignInDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);

        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(
                R.layout.splash_dialog,
                null
        );
        EditText userText = (EditText) layout.findViewById(R.id.splash_sign_in_dialog_username);
        EditText passText = (EditText) layout.findViewById(R.id.splash_sign_in_dialog_password);
        passText.setTypeface(Typeface.DEFAULT);
        passText.setTransformationMethod(new PasswordTransformationMethod());
        builder.setView(layout);

        builder.setPositiveButton(
                getString(R.string.splash_sign_in_dialog_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /* Do something */
                    }
                }
        );

        builder.setNegativeButton(
                getString(R.string.splash_sign_in_dialog_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /* Do nothing */
                    }
                }
        );

        builder.show();
    }

    private void showSignUpDialog() {
        Uri uri = Uri.parse(getString(R.string.splash_sign_up_url));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    /* Do something */
}
