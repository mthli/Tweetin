package io.github.mthli.Tweetin.Database.Tweet;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.Tweet;

import java.util.ArrayList;
import java.util.List;

public class TweetAction {
    private TweetHelper helper;
    private SQLiteDatabase database;
    private Context context;

    public TweetAction(Context context) {
        helper = new TweetHelper(context);
        this.context = context;
    }

    public void opewDatabase(boolean rw) {
        if (rw) {
            database = helper.getWritableDatabase();
        } else {
            database = helper.getReadableDatabase();
        }
    }

    public void closeDatabase() {
        helper.close();
    }

    public void addTweet(TweetData data) {
        ContentValues values = new ContentValues();
        values.put(TweetData.TWEET_ID, data.getTweetId());
        values.put(TweetData.USER_ID, data.getUserId());
        values.put(TweetData.AVATAR_URL, data.getAvatarUrl());
        values.put(TweetData.CREATED_AT, data.getCreatedAt());
        values.put(TweetData.NAME, data.getName());
        values.put(TweetData.SCREEN_NAME, data.getScreenName());
        if (data.isProtected()) {
            values.put(TweetData.PROTECT, "true");
        } else {
            values.put(TweetData.PROTECT, "false");
        }
        values.put(TweetData.TEXT, data.getText());
        if (data.isRetweet()) {
            values.put(TweetData.RETWEET, "true");
        } else {
            values.put(TweetData.RETWEET, "false");
        }
        values.put(TweetData.RETWEETED_BY_NAME, data.getRetweetedByName());
        values.put(TweetData.RETWEETED_BY_ID, data.getRetweetedById());
        values.put(TweetData.REPLY_TO, data.getReplyTo()); //
        database.insert(TweetData.TABLE, null, values);
    }

    /* Do something */
    public void updateByMe(long oldTweetId, Tweet newTweet) {
        SharedPreferences preferences = context.getSharedPreferences(
                context.getString(R.string.sp_name),
                Context.MODE_PRIVATE
        );
        long useId = preferences.getLong(context.getString(R.string.sp_use_id), 0);

        ContentValues values = new ContentValues();
        values.put(TweetData.TWEET_ID, newTweet.getTweetId());
        values.put(TweetData.RETWEET, "true");
        values.put(TweetData.RETWEETED_BY_NAME, context.getString(R.string.tweet_retweeted_by_me));
        values.put(TweetData.RETWEETED_BY_ID, useId);
        values.put(TweetData.REPLY_TO, newTweet.getReplyTo()); //
        database.update(
                TweetData.TABLE,
                values,
                TweetData.TWEET_ID + "=?",
                new String[]{String.valueOf(oldTweetId)}
        );
    }

    public void deleteAll() {
        database.execSQL("DELETE FROM " + TweetData.TABLE);
    }

    private TweetData getData(Cursor cursor) {
        TweetData data = new TweetData();
        data.setTweetId(cursor.getLong(0));
        data.setUserId(cursor.getLong(1));
        data.setAvatarUrl(cursor.getString(2));
        data.setCreatedAt(cursor.getString(3));
        data.setName(cursor.getString(4));
        data.setScreenName(cursor.getString(5));
        if (cursor.getString(6).equals("true")) {
            data.setProtect(true);
        } else {
            data.setProtect(false);
        }
        data.setText(cursor.getString(7));
        if (cursor.getString(8).equals("true")) {
            data.setRetweet(true);
        } else {
            data.setRetweet(false);
        }
        data.setRetweetedByName(cursor.getString(9));
        data.setRetweetedById(cursor.getLong(10));
        data.setReplyTo(cursor.getLong(11)); //

        return data;
    }

    public List<TweetData> getTweetDataList() {
        List<TweetData> tweetDataList = new ArrayList<TweetData>();
        Cursor cursor = database.query(
                TweetData.TABLE,
                new String[] {
                        TweetData.TWEET_ID,
                        TweetData.USER_ID,
                        TweetData.AVATAR_URL,
                        TweetData.CREATED_AT,
                        TweetData.NAME,
                        TweetData.SCREEN_NAME,
                        TweetData.PROTECT,
                        TweetData.TEXT,
                        TweetData.RETWEET,
                        TweetData.RETWEETED_BY_NAME,
                        TweetData.RETWEETED_BY_ID,
                        TweetData.REPLY_TO //
                },
                null,
                null,
                null,
                null,
                null
        );

        if (cursor == null) {
            return tweetDataList;
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            TweetData data = getData(cursor);
            tweetDataList.add(data);
            cursor.moveToNext();
        }
        cursor.close();

        return tweetDataList;
    }
}
