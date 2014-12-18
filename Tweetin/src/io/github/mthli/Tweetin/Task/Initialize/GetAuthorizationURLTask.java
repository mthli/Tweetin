package io.github.mthli.Tweetin.Task.Initialize;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;
import io.github.mthli.Tweetin.Activity.SplashActivity;
import io.github.mthli.Tweetin.R;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;

public class GetAuthorizationURLTask extends AsyncTask<Void, Integer, Boolean> {

    private SplashActivity splashActivity;

    private String token;
    private String tokenSecret;
    private String authorizationURL;

    private ProgressDialog progressDialog;

    public GetAuthorizationURLTask(
            SplashActivity splashActivity
    ) {
        this.splashActivity = splashActivity;

        this.token = null;
        this.token = null;
        this.authorizationURL = null;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(splashActivity);
        progressDialog.setMessage(
                splashActivity.getString(R.string.splash_pd_start_authorization)
        );
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        String consumerKey = splashActivity.getString(R.string.app_consumer_key);
        String consumerSecret = splashActivity.getString(R.string.app_consumer_secret);

        TwitterFactory twitterFactory = new TwitterFactory();
        Twitter twitter = twitterFactory.getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);

        try {
            RequestToken requestToken = twitter.getOAuthRequestToken(
                    splashActivity.getString(R.string.app_callback_url)
            );

            token = requestToken.getToken();
            tokenSecret = requestToken.getTokenSecret();
            authorizationURL = requestToken.getAuthorizationURL();
        } catch (Exception e) {
            return false;
        }

        if (isCancelled()) {
            return false;
        }
        return true;
    }

    @Override
    protected void onCancelled() {
        /* Do nothing */
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        /* Do nothing */
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(authorizationURL)
            );
            splashActivity.startActivity(intent);

            SharedPreferences sharedPreferences = splashActivity.getSharedPreferences(
                    splashActivity.getString(R.string.sp_name),
                    Context.MODE_PRIVATE
            );
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putLong(
                    splashActivity.getString(R.string.sp_use_id),
                    -1l
            );
            editor.putString(
                    splashActivity.getString(R.string.sp_use_screen_name),
                    null
            );
            editor.putString(
                    splashActivity.getString(R.string.sp_request_token),
                    token
            );
            editor.putString(
                    splashActivity.getString(R.string.sp_request_token_secret),
                    tokenSecret
            );
            editor.putString(
                    splashActivity.getString(R.string.sp_access_token),
                    null
            );
            editor.putString(
                    splashActivity.getString(R.string.sp_access_token_secret),
                    null
            );
            editor.commit();

            editor.putBoolean(
                    splashActivity.getString(R.string.sp_is_timeline_first),
                    true
            );
            editor.putBoolean(
                    splashActivity.getString(R.string.sp_is_mention_first),
                    true
            );
            editor.putBoolean(
                    splashActivity.getString(R.string.sp_is_favorite_first),
                    true
            );
            editor.commit();

            progressDialog.hide();
            progressDialog.dismiss();

            splashActivity.finish();
        } else {
            progressDialog.hide();
            progressDialog.dismiss();

            Toast.makeText(
                    splashActivity,
                    R.string.splash_toast_authorization_failed,
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
