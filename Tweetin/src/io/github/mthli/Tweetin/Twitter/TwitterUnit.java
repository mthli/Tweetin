package io.github.mthli.Tweetin.Twitter;

import android.content.Context;
import android.content.SharedPreferences;
import io.github.mthli.Tweetin.R;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TwitterUnit {

    public static Twitter getTwitterFromInstance() {
        TwitterFactory twitterFactory = new TwitterFactory();

        return twitterFactory.getInstance();
    }

    public static Twitter getTwitterFromSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.sp_tweetin),
                Context.MODE_PRIVATE
        );

        String consumerKey = context.getString(R.string.app_consumer_key);
        String consumerSecret = context.getString(R.string.app_consumer_secret);
        String accessToken = sharedPreferences.getString(
                context.getString(R.string.sp_access_token),
                null
        );
        String accessTokenSecret = sharedPreferences.getString(
                context.getString(R.string.sp_access_token_secret),
                null
        );

        Twitter twitter = getTwitterFromInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);

        AccessToken token = new AccessToken(accessToken, accessTokenSecret);
        twitter.setOAuthAccessToken(token);

        return twitter;
    }

    public static String getUseScreenNameFromSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.sp_tweetin),
                Context.MODE_PRIVATE
        );

        return sharedPreferences.getString(
                context.getString(R.string.sp_use_screen_name),
                null
        );
    }

}
