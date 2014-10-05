package io.github.mthli.Tweetin.Database.Tweet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class TweetAction {
    private TweetHelper helper;
    private SQLiteDatabase database;

    public TweetAction(Context context) {
        helper = new TweetHelper(context);
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

    public boolean checkRepeat(String tweetId) {
        Cursor cursor = database.query(
                TweetData.TABLE,
                new String[] {TweetData.TWEET_ID},
                TweetData.TWEET_ID + "=?",
                new String[] {tweetId},
                null,
                null,
                null
        );

        if (cursor != null) {
            boolean result = false;
            if (cursor.moveToFirst()) {
                result = true;
            }
            cursor.close();
            return result;
        }
        return false;
    }

    public void addTweet(TweetData data) {
        ContentValues values = new ContentValues();
        values.put(TweetData.TWEET_ID, data.getTweetId());
        values.put(TweetData.USER_ID, data.getUserId());
        values.put(TweetData.AVATAR_URL, data.getAvatarUrl());
        values.put(TweetData.CREATED_AT, data.getCreatedAt());
        values.put(TweetData.NAME, data.getName());
        values.put(TweetData.SCREEN_NAME, data.getScreenName());
        values.put(TweetData.TEXT, data.getText());
        if (data.isRetweet()) {
            values.put(TweetData.RETWEET, "true");
        } else {
            values.put(TweetData.RETWEET, "false");
        }
        values.put(TweetData.RETWEETED_BY, data.getRetweetedBy());
        database.insert(TweetData.TABLE, null, values);
    }

    public void deleteTweet(long tweetId) {
        database.execSQL("DELETE FROM "
                        + TweetData.TABLE
                        + "WHERE "
                        + TweetData.TWEET_ID
                        + " = "
                        + tweetId
        );
    }

    public void deleteAll() {
        database.execSQL("DELETE FROM " + TweetData.TABLE);
    }

    private TweetData getData(Cursor cursor) {
        TweetData data = new TweetData();
        data.setTweetId(cursor.getInt(0));
        data.setUserId(cursor.getInt(1));
        data.setAvatarUrl(cursor.getString(2));
        data.setCreatedAt(cursor.getString(3));
        data.setName(cursor.getString(4));
        data.setScreenName(cursor.getString(5));
        data.setText(cursor.getString(6));
        if (cursor.getString(7).equals("true")) {
            data.setRetweet(true);
        } else {
            data.setRetweet(false);
        }
        data.setRetweetedBy(cursor.getString(8));

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
                        TweetData.TEXT,
                        TweetData.RETWEET,
                        TweetData.RETWEETED_BY
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
