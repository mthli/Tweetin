package io.github.mthli.Tweetin.Database.Mention;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.Base.Tweet;

import java.util.ArrayList;
import java.util.List;

public class MentionAction {
    private MentionHelper helper;
    private SQLiteDatabase database;
    private Context context;
    
    public MentionAction(Context context) {
        helper = new MentionHelper(context);
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

    public void addTweet(MentionData data) {
        ContentValues values = new ContentValues();
        values.put(MentionData.TWEET_ID, data.getTweetId());
        values.put(MentionData.USER_ID, data.getUserId());
        values.put(MentionData.AVATAR_URL, data.getAvatarUrl());
        values.put(MentionData.CREATED_AT, data.getCreatedAt());
        values.put(MentionData.NAME, data.getName());
        values.put(MentionData.SCREEN_NAME, data.getScreenName());
        if (data.isProtected()) {
            values.put(MentionData.PROTECT, "true");
        } else {
            values.put(MentionData.PROTECT, "false");
        }
        values.put(MentionData.TEXT, data.getText());
        values.put(MentionData.CHECK_IN, data.getCheckIn());
        if (data.isRetweet()) {
            values.put(MentionData.RETWEET, "true");
        } else {
            values.put(MentionData.RETWEET, "false");
        }
        values.put(MentionData.RETWEETED_BY_NAME, data.getRetweetedByName());
        values.put(MentionData.RETWEETED_BY_ID, data.getRetweetedById());
        values.put(MentionData.REPLY_TO, data.getReplyTo());
        database.insert(MentionData.TABLE, null, values);
    }

    public void updateByMe(long oldTweetId, Tweet newTweet) {
        SharedPreferences preferences = context.getSharedPreferences(
                context.getString(R.string.sp_name),
                Context.MODE_PRIVATE
        );
        long useId = preferences.getLong(context.getString(R.string.sp_use_id), 0);

        ContentValues values = new ContentValues();
        values.put(MentionData.TWEET_ID, newTweet.getTweetId());
        values.put(MentionData.RETWEET, "true");
        values.put(MentionData.RETWEETED_BY_NAME, context.getString(R.string.tweet_retweeted_by_me));
        values.put(MentionData.RETWEETED_BY_ID, useId);
        values.put(MentionData.REPLY_TO, newTweet.getReplyTo());
        database.update(
                MentionData.TABLE,
                values,
                MentionData.TWEET_ID + "=?",
                new String[]{String.valueOf(oldTweetId)}
        );
    }

    public void deleteAll() {
        database.execSQL("DELETE FROM " + MentionData.TABLE);
    }

    private MentionData getData(Cursor cursor) {
        MentionData data = new MentionData();
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
    public List<MentionData> getMentionDataList() {
        List<MentionData> mentionDataList = new ArrayList<MentionData>();
        Cursor cursor = database.query(
                MentionData.TABLE,
                new String[] {
                        MentionData.TWEET_ID,
                        MentionData.USER_ID,
                        MentionData.AVATAR_URL,
                        MentionData.CREATED_AT,
                        MentionData.NAME,
                        MentionData.SCREEN_NAME,
                        MentionData.PROTECT,
                        MentionData.TEXT,
                        MentionData.CHECK_IN,
                        MentionData.RETWEET,
                        MentionData.RETWEETED_BY_NAME,
                        MentionData.RETWEETED_BY_ID,
                        MentionData.REPLY_TO
                },
                null,
                null,
                null,
                null,
                null
        );

        if (cursor == null) {
            return mentionDataList;
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MentionData data = getData(cursor);
            mentionDataList.add(data);
            cursor.moveToNext();
        }
        cursor.close();

        return mentionDataList;
    }
}
