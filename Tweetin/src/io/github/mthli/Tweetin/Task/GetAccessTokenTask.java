package io.github.mthli.Tweetin.Task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;
import io.github.mthli.Tweetin.Activity.MainActivity;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Twitter.TwitterUnit;
import twitter4j.Twitter;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class GetAccessTokenTask extends AsyncTask<Void, Integer, Boolean> {

    private MainActivity mainActivity;
    private String oAuthVerifier;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private AccessToken accessToken;
    private String useScreenName;

    private ProgressDialog progressDialog;

    public GetAccessTokenTask(
            MainActivity mainActivity,
            String oAuthVerifier
    ) {
        this.mainActivity = mainActivity;
        this.oAuthVerifier = oAuthVerifier;

        this.useScreenName = null;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(mainActivity);
        progressDialog.setMessage(
                mainActivity.getString(R.string.main_pd_get_access_token)
        );
        progressDialog.setCancelable(false);
        progressDialog.show();

        sharedPreferences = mainActivity.getSharedPreferences(
                mainActivity.getString(R.string.sp_tweetin),
                Context.MODE_PRIVATE
        );
        editor = sharedPreferences.edit();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        String consumerKey = mainActivity.getString(R.string.app_consumer_key);
        String consumerSecret = mainActivity.getString(R.string.app_consumer_secret);

        Twitter twitter = TwitterUnit.getTwitterFromInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);

        try {
            String token = sharedPreferences.getString(
                    mainActivity.getString(R.string.sp_request_token),
                    null
            );
            String tokenSecret = sharedPreferences.getString(
                    mainActivity.getString(R.string.sp_request_token_secret),
                    null
            );
            RequestToken requestToken = new RequestToken(token, tokenSecret);

            accessToken = twitter.getOAuthAccessToken(
                    requestToken,
                    oAuthVerifier
            );

            useScreenName = twitter.verifyCredentials().getScreenName();
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
            editor.putString(
                    mainActivity.getString(R.string.sp_use_screen_name),
                    useScreenName
            );
            editor.putString(
                    mainActivity.getString(R.string.sp_access_token),
                    accessToken.getToken()
            );
            editor.putString(
                    mainActivity.getString(R.string.sp_access_token_secret),
                    accessToken.getTokenSecret()
            );
            editor.commit();

            mainActivity.initUI();

            progressDialog.hide();
            progressDialog.dismiss();

            Toast.makeText(
                    mainActivity,
                    mainActivity.getString(R.string.main_toast_initializing),
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            editor.putString(
                    mainActivity.getString(R.string.sp_request_token),
                    null
            );
            editor.putString(
                    mainActivity.getString(R.string.sp_request_token_secret),
                    null
            );
            editor.commit();

            progressDialog.hide();
            progressDialog.dismiss();

            Toast.makeText(
                    mainActivity,
                    mainActivity.getString(R.string.main_toast_get_access_token_failed),
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}
