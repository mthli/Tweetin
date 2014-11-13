package io.github.mthli.Tweetin.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import io.github.mthli.Tweetin.R;
import org.apache.commons.io.IOUtils;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.io.InputStream;

public class SplashActivity extends Activity {
    private static final int GET_AUTH_URL_SUCCESSFUL = 0x100;
    private static final int GET_AUTH_URL_FAILED = 0x101;
    private static final int GET_ACCESS_TOKEN_SUCCESSFUL = 0x200;
    private static final int GET_ACCESS_TOKEN_FAILED = 0x201;
    private Handler handler;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private AlertDialog signInDialog;
    private ProgressDialog progressDialog;
    private String conKey;
    private String conSecret;

    private Twitter twitter;
    private RequestToken requestToken;
    private String authUrl;
    private void showAuthorizationDialog() {
        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(
                R.layout.splash_webview_dialog,
                null
        );
        WebView webView = (WebView) layout.findViewById(
                R.id.splash_webview_dialog_webview
        );
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(authUrl);

        Toast.makeText(
                SplashActivity.this,
                R.string.splash_toast_wait,
                Toast.LENGTH_SHORT
        ).show();

        AlertDialog.Builder builder = new AlertDialog.Builder(
                SplashActivity.this
        );
        builder.setCancelable(false);
        builder.setView(layout);
        builder.setPositiveButton(
                getString(R.string.splash_webview_dialog_next),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        signInDialog.hide();
                        signInDialog.dismiss();
                        showPinDialog();
                    }
                }
        );

        signInDialog = builder.create();
        signInDialog.show();
        signInDialog.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
        );
    }
    private void showPinDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                SplashActivity.this
        );
        builder.setCancelable(false);

        final LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(
                R.layout.splash_pin_dialog,
                null
        );
        builder.setView(linearLayout);
        builder.setPositiveButton(
                getString(R.string.splash_pin_dialog_accept),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        signInDialog.hide();
                        signInDialog.dismiss();
                        getAccessToken(linearLayout);
                    }
                }
        );

        signInDialog = builder.create();
        signInDialog.show();
    }
    private String pin;
    private void getAccessToken(LinearLayout linearLayout) {
        EditText pinEdit = (EditText) linearLayout.findViewById(
                R.id.splash_pin_dialog_pin
        );
        pin = pinEdit.getText().toString();
        if (pin.length() == 0) {
            Toast.makeText(
                    SplashActivity.this,
                    R.string.splash_toast_miss_pin,
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            progressDialog = new ProgressDialog(SplashActivity.this);
            progressDialog.setMessage(
                    getString(R.string.splash_pd_get_access_token)
            );
            progressDialog.setCancelable(false);
            progressDialog.show();
            new Thread(getAccessTokenThread).start();
        }
    }
    private void saveSignInData(long useId, AccessToken accessToken) {
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
    Runnable getAccessTokenThread = new Runnable() {
        @Override
        public void run() {
            try {
                AccessToken accessToken = twitter.getOAuthAccessToken(
                        requestToken,
                        pin
                );
                saveSignInData(twitter.verifyCredentials().getId(), accessToken);
                Message message = new Message();
                message.what = GET_ACCESS_TOKEN_SUCCESSFUL;
                handler.sendMessage(message);
            } catch (Exception e) {
                Message message = new Message();
                message.what = GET_ACCESS_TOKEN_FAILED;
                handler.sendMessage(message);
            }
        }
    };

    Runnable getAuthURLThread = new Runnable() {
        @Override
        public void run() {
            try {
                twitter = TwitterFactory.getSingleton();
                twitter.setOAuthConsumer(conKey, conSecret);
                requestToken = twitter.getOAuthRequestToken();
                authUrl = requestToken.getAuthorizationURL();
                Message message = new Message();
                message.what = GET_AUTH_URL_SUCCESSFUL;
                handler.sendMessage(message);
            } catch (Exception e) {
                Message message = new Message();
                message.what = GET_AUTH_URL_FAILED;
                handler.sendMessage(message);
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        preferences = getSharedPreferences(
                getString(R.string.sp_name),
                MODE_PRIVATE
        );
        editor = preferences.edit();
        long useId = preferences.getLong(getString(R.string.sp_use_id), 0l);
        if (useId > 0l) {
            editor.putBoolean(
                    getString(R.string.sp_is_timeline_first),
                    false
            ).commit();
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        final EditText conKeyEdit = (EditText) findViewById(R.id.splash_consumer_key);
        final EditText conSecretEdit = (EditText) findViewById(R.id.splash_consumer_secret);

        Button signIn = (Button) findViewById(R.id.splash_sign_in);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conKey = conKeyEdit.getText().toString();
                conSecret = conSecretEdit.getText().toString();
                if (conKey.length() == 0 || conSecret.length() == 0) {
                    Toast.makeText(
                            SplashActivity.this,
                            R.string.splash_toast_miss_api_info,
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    progressDialog = new ProgressDialog(SplashActivity.this);
                    progressDialog.setMessage(
                            getString(R.string.splash_pd_start_authorization)
                    );
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    new Thread(getAuthURLThread).start();
                }
            }
        });

        TextView howTo = (TextView) findViewById(R.id.splash_how_to);
        howTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHowToDialog();
            }
        });

        handler = new Handler() {
            public void handleMessage(Message message) {
                switch (message.what) {
                    case GET_AUTH_URL_SUCCESSFUL:
                        progressDialog.hide();
                        progressDialog.dismiss();
                        showAuthorizationDialog();
                        break;
                    case GET_AUTH_URL_FAILED:
                        progressDialog.hide();
                        progressDialog.dismiss();
                        Toast.makeText(
                                SplashActivity.this,
                                R.string.splash_toast_authorization_failed,
                                Toast.LENGTH_SHORT
                        ).show();
                        break;
                    case GET_ACCESS_TOKEN_SUCCESSFUL:
                        progressDialog.hide();
                        progressDialog.dismiss();
                        editor.putBoolean(
                                getString(R.string.sp_is_timeline_first),
                                true
                        ).commit();
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case GET_ACCESS_TOKEN_FAILED:
                        progressDialog.hide();
                        progressDialog.dismiss();
                        Toast.makeText(
                                SplashActivity.this,
                                R.string.splash_toast_get_access_token_failed,
                                Toast.LENGTH_SHORT
                        ).show();
                        break;
                    default:
                        break;
                }
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

    private void showHowToDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setCancelable(false);

        String lang;
        if (getResources().getConfiguration().locale.getLanguage().equals("zh")) {
            lang = getString(R.string.splash_how_to_zh);
        } else {
            lang = getString(R.string.splash_how_to_en);
        }
        String str = null;
        try {
            InputStream inputStream = getResources().getAssets().open(lang);
            str = IOUtils.toString(inputStream);
        } catch (Exception e) {
            /* Do nothing */
        }

        WebView webView = new WebView(SplashActivity.this);
        webView.loadDataWithBaseURL(
                getString(R.string.splash_how_to_base_url),
                str,
                null,
                "UTF-8",
                null
        );
        builder.setView(webView);

        builder.setPositiveButton(
                getString(R.string.splash_how_to_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /* Do nothing */
                    }
                }
        );
        builder.create().show();
    }
}
