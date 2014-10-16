package io.github.mthli.Tweetin.Splash;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.*;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import io.github.mthli.Tweetin.Main.MainActivity;
import io.github.mthli.Tweetin.R;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class SplashActivity extends Activity {
    private static final int SIGN_IN_FIRST_SUCCESSFUL = 0x100;
    private static final int SIGN_IN_FIRST_FAILED = 0x101;
    private static final int SIGN_IN_SECOND_SUCCESSFUL = 0x200;
    private static final int SIGN_IN_SECOND_FAILED = 0x201;
    private Handler handler;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private AlertDialog signInDialog;
    private ProgressDialog progressDialog;
    private String conKey;
    private String conSecret;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager manager = new SystemBarTintManager(this);
            manager.setStatusBarTintEnabled(true);
            manager.setNavigationBarTintEnabled(true);
            int color = getResources().getColor(R.color.tumblr_dark_blue);
            manager.setTintColor(color);
        }

        preferences = getSharedPreferences(
                getString(R.string.sp_name),
                MODE_PRIVATE
        );
        editor = preferences.edit();
        long useId = preferences.getLong(getString(R.string.sp_use_id), 0);
        if (useId != 0) {
            editor.putString(getString(R.string.sp_is_first_sign_in), "false").commit();
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        final EditText conKeyEidt = (EditText) findViewById(R.id.splash_sign_in_consumer_key);
        final EditText conSecretEidt = (EditText) findViewById(R.id.splash_sign_in_consumer_secret);

        Button signIn = (Button) findViewById(R.id.splash_sign_in_button);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conKey = conKeyEidt.getText().toString();
                conSecret = conSecretEidt.getText().toString();
                if (conKey.length() == 0 || conSecret.length() == 0) {
                    Toast.makeText(
                            SplashActivity.this,
                            R.string.splash_sign_in_miss_oauth,
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    progressDialog = new ProgressDialog(SplashActivity.this);
                    progressDialog.setMessage(getString(R.string.splash_sign_in_start_authorization));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    new Thread(getAccessTokenThreadFirst).start();
                }
            }
        });

        Button help = (Button) findViewById(R.id.splash_help_button);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Do something */
            }
        });

        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SIGN_IN_FIRST_SUCCESSFUL:
                        progressDialog.hide();
                        progressDialog.dismiss();
                        showSignInDialogNext();
                        break;
                    case SIGN_IN_FIRST_FAILED:
                        progressDialog.hide();
                        progressDialog.dismiss();
                        Toast.makeText(
                                SplashActivity.this,
                                R.string.splash_sign_in_authorization_failed,
                                Toast.LENGTH_SHORT
                        ).show();
                        break;
                    case SIGN_IN_SECOND_SUCCESSFUL:
                        progressDialog.hide();
                        progressDialog.dismiss();
                        editor.putString(getString(R.string.sp_is_first_sign_in), "true").commit();
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case SIGN_IN_SECOND_FAILED:
                        progressDialog.hide();
                        progressDialog.dismiss();
                        Toast.makeText(
                                SplashActivity.this,
                                R.string.splash_sign_in_get_access_token_failed,
                                Toast.LENGTH_SHORT
                        ).show();
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };
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

    private Twitter twitter;
    private RequestToken requestToken;
    private String authUrl;
    Runnable getAccessTokenThreadFirst = new Runnable() {
        @Override
        public void run() {
            try {
                twitter = TwitterFactory.getSingleton();
                twitter.setOAuthConsumer(conKey, conSecret);
                requestToken = twitter.getOAuthRequestToken();
                authUrl = requestToken.getAuthorizationURL();
                Message message = new Message();
                message.what = SIGN_IN_FIRST_SUCCESSFUL;
                handler.sendMessage(message);
            } catch (Exception e) {
                Message message = new Message();
                message.what = SIGN_IN_FIRST_FAILED;
                handler.sendMessage(message);
            }
        }
    };

    private void showSignInDialogNext() {
        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(
                R.layout.splash_dialog_webview,
                null
        );
        WebView webView = (WebView) layout.findViewById(R.id.splash_dialog_webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(authUrl);

        Toast.makeText(
                SplashActivity.this,
                R.string.splash_sign_in_wait,
                Toast.LENGTH_SHORT
        ).show();

        AlertDialog.Builder builder = new AlertDialog.Builder(
                SplashActivity.this
        );
        builder.setCancelable(false);
        builder.setView(layout);

        builder.setPositiveButton(
                getString(R.string.splash_sign_in_dialog_next),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        signInDialog.hide();
                        signInDialog.dismiss();
                        showSignInDialogSecond();
                    }
                }
        );

        signInDialog = builder.create();
        signInDialog.show();
        signInDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    private void showSignInDialogSecond() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                SplashActivity.this
        );
        builder.setCancelable(false);

        final LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(
                R.layout.splash_dialog_second,
                null
        );
        builder.setView(layout);

        builder.setPositiveButton(
                getString(R.string.splash_sign_in_dialog_accept),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        signInDialog.hide();
                        signInDialog.dismiss();
                        getAccessTokenSecond(layout);
                    }
                }
        );

        signInDialog = builder.create();
        signInDialog.show();
    }

    private String pin;
    private void getAccessTokenSecond(LinearLayout layout) {
        EditText pinText = (EditText) layout.findViewById(R.id.splash_sign_in_dialog_pin);
        pin = pinText.getText().toString();
        if (pin.length() == 0) {
            Toast.makeText(
                    SplashActivity.this,
                    R.string.splash_sign_in_miss_pin,
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            progressDialog = new ProgressDialog(SplashActivity.this);
            progressDialog.setMessage(getString(R.string.splash_sign_in_get_access_token));
            progressDialog.setCancelable(false);
            progressDialog.show();
            new Thread(getAccessTokenThreadSecond).start();
        }
    }

    Runnable getAccessTokenThreadSecond = new Runnable() {
        @Override
        public void run() {
            try {
                AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                saveAccessToken(twitter.verifyCredentials().getId(), accessToken);
                Message message = new Message();
                message.what = SIGN_IN_SECOND_SUCCESSFUL;
                handler.sendMessage(message);
            } catch (Exception e) {
                Message message = new Message();
                message.what = SIGN_IN_SECOND_FAILED;
                handler.sendMessage(message);
            }
        }
    };

    private void saveAccessToken(long useId, AccessToken accessToken) {
        editor.putLong(
                getString(R.string.sp_use_id),
                useId
        ).commit();
        editor.putString(
                getString(R.string.sp_consumer_key),
                conKey
        ).commit();
        editor.putString(
                getString(R.string.sp_consumer_secret),
                conSecret
        ).commit();
        editor.putString(
                getString(R.string.sp_access_token),
                accessToken.getToken()
        ).commit();
        editor.putString(
                getString(R.string.sp_access_token_secret),
                accessToken.getTokenSecret()
        ).commit();
    }
}
