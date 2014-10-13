package io.github.mthli.Tweetin.Database.Main;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.Base.Tweet;

import java.util.ArrayList;
import java.util.List;

public class MainAction {
    private MainHelper helper;
    private SQLiteDatabase database;
    private Context context;

    public MainAction(Context context) {
        helper = new MainHelper(context);
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

    public void addTweet(MainData data) {
        ContentValues values = new ContentValues();
        values.put(MainData.TWEET_ID, data.getTweetId());
        values.put(MainData.USER_ID, data.getUserId());
        values.put(MainData.AVATAR_URL, data.getAvatarUrl());
        values.put(MainData.CREATED_AT, data.getCreatedAt());
        values.put(MainData.NAME, data.getName());
        values.put(MainData.SCREEN_NAME, data.getScreenName());
        if (data.isProtected()) {
            values.put(MainData.PROTECT, "true");
        } else {
            values.put(MainData.PROTECT, "false");
        }
        values.put(MainData.TEXT, data.getText());
        values.put(MainData.CHECK_IN, data.getCheckIn());
        if (data.isRetweet()) {
            values.put(MainData.RETWEET, "true");
        } else {
            values.put(MainData.RETWEET, "false");
        }
        values.put(MainData.RETWEETED_BY_NAME, data.getRetweetedByName());
        values.put(MainData.RETWEETED_BY_ID, data.getRetweetedById());
        values.put(MainData.REPLY_TO, data.getReplyTo()); //
        database.insert(MainData.TABLE, null, values);
    }

    public void updateByMe(long oldTweetId, Tweet newTweet) {
        SharedPreferences preferences = context.getSharedPreferences(
                context.getString(R.string.sp_name),
                Context.MODE_PRIVATE
        );
        long useId = preferences.getLong(context.getString(R.string.sp_use_id), 0);

        ContentValues values = new ContentValues();
        values.put(MainData.TWEET_ID, newTweet.getTweetId());
        values.put(MainData.RETWEET, "true");
        values.put(MainData.RETWEETED_BY_NAME, context.getString(R.string.tweet_retweeted_by_me));
        values.put(MainData.RETWEETED_BY_ID, useId);
        values.put(MainData.REPLY_TO, newTweet.getReplyTo());
        database.update(
                MainData.TABLE,
                values,
                MainData.TWEET_ID + "=?",
                new String[]{String.valueOf(oldTweetId)}
        );
    }

    public void deleteAll() {
        database.execSQL("DELETE FROM " + MainData.TABLE);
    }

    private MainData getData(Cursor cursor) {
        MainData data = new MainData();
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
        data.setCheckIn(cursor.getString(8));
        if (cursor.getString(9).equals("true")) {
            data.setRetweet(true);
        } else {
            data.setRetweet(false);
        }
        data.setRetweetedByName(cursor.getString(10));
        data.setRetweetedById(cursor.getLong(11));
        data.setReplyTo(cursor.getLong(12)); //

        return data;
    }
    public List<MainData> getTweetDataList() {
        List<MainData> mainDataList = new ArrayList<MainData>();
        Cursor cursor = database.query(
                MainData.TABLE,
                new String[] {
                        MainData.TWEET_ID,
                        MainData.USER_ID,
                        MainData.AVATAR_URL,
                        MainData.CREATED_AT,
                        MainData.NAME,
                        MainData.SCREEN_NAME,
                        MainData.PROTECT,
                        MainData.TEXT,
                        MainData.CHECK_IN,
                        MainData.RETWEET,
                        MainData.RETWEETED_BY_NAME,
                        MainData.RETWEETED_BY_ID,
                        MainData.REPLY_TO
                },
                null,
                null,
                null,
                null,
                null
        );

        if (cursor == null) {
            return mainDataList;
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MainData data = getData(cursor);
            mainDataList.add(data);
            cursor.moveToNext();
        }
        cursor.close();

        return mainDataList;
    }
}
