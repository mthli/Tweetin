package io.github.mthli.Tweetin.Database.Mention;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;

import java.util.ArrayList;
import java.util.List;

public class MentionAction {
    private SQLiteDatabase database;
    private MentionHelper helper;
    private Context context;

    public MentionAction(Context context) {
        this.helper = new MentionHelper(context);
        this.context = context;
    }

    public void openDatabase(boolean rw) {
        if (rw) {
            database = helper.getWritableDatabase();
        } else {
            database = helper.getReadableDatabase();
        }
    }

    public void closeDatabase() {
        helper.close();
    }

    public void addRecord(MentionRecord record) {
        ContentValues values = new ContentValues();
        values.put(MentionRecord.ORIGINAL_STATUS_ID, record.getOriginalStatusId());
        values.put(MentionRecord.AFTER_RETWEET_STATUS_ID, record.getAfterRetweetStatusId());
        values.put(MentionRecord.AFTER_FAVORITE_STATUS_ID, record.getAfterFavoriteStatusId());
        values.put(MentionRecord.REPLY_TO_STATUS_ID, record.getReplyToStatusId());
        values.put(MentionRecord.USER_ID, record.getUserId());
        values.put(MentionRecord.RETWEETED_BY_USER_ID, record.getRetweetedByUserId());
        values.put(MentionRecord.AVATAR_URL, record.getAvatarURL());
        values.put(MentionRecord.CREATED_AT, record.getCreatedAt());
        values.put(MentionRecord.NAME, record.getName());
        values.put(MentionRecord.SCREEN_NAME, record.getScreenName());
        if (record.isProtect()) {
            values.put(MentionRecord.PROTECT, "true");
        } else {
            values.put(MentionRecord.PROTECT, "false");
        }
        values.put(MentionRecord.CHECK_IN, record.getCheckIn());
        values.put(MentionRecord.TEXT, record.getText());
        if (record.isRetweet()) {
            values.put(MentionRecord.RETWEET, "true");
        } else {
            values.put(MentionRecord.RETWEET, "false");
        }
        values.put(MentionRecord.RETWEETED_BY_USER_NAME, record.getRetweetedByUserName());
        if (record.isFavorite()) {
            values.put(MentionRecord.FAVORITE, "true");
        } else {
            values.put(MentionRecord.FAVORITE, "false");
        }
        database.insert(MentionRecord.TABLE, null, values);
    }

    /* Do something */
    public void updatedByRetweet(Tweet newTweet) {
        ContentValues values = new ContentValues();
        values.put(
                MentionRecord.AFTER_RETWEET_STATUS_ID,
                newTweet.getAfterRetweetStatusId()
        );
        values.put(
                MentionRecord.RETWEETED_BY_USER_ID,
                newTweet.getRetweetedByUserId()
        );
        if (newTweet.isRetweet()) {
            values.put(
                    MentionRecord.RETWEET,
                    "true"
            );
        } else {
            values.put(
                    MentionRecord.RETWEET,
                    "false"
            );
        }
        values.put(
                MentionRecord.RETWEETED_BY_USER_NAME,
                newTweet.getRetweetedByUserName()
        );

        database.update(
                MentionRecord.TABLE,
                values,
                MentionRecord.ORIGINAL_STATUS_ID + "=?",
                new String[] {String.valueOf(newTweet.getOriginalStatusId())}
        );
    }

    /* Do something */
    public void updatedByFavorite(Tweet newTweet) {
        ContentValues values = new ContentValues();
        values.put(
                MentionRecord.AFTER_FAVORITE_STATUS_ID,
                newTweet.getAfterFavoriteStatusId()
        );
        if (newTweet.isFavorite()) {
            values.put(
                    MentionRecord.FAVORITE,
                    "true"
            );
        } else {
            values.put(
                    MentionRecord.FAVORITE,
                    "false"
            );
        }

        database.update(
                MentionRecord.TABLE,
                values,
                MentionRecord.ORIGINAL_STATUS_ID + "=?",
                new String[] {String.valueOf(newTweet.getOriginalStatusId())}
        );
    }

    public void deleteAll() {
        database.execSQL("DELETE FROM " + MentionRecord.TABLE);
    }

    private MentionRecord getMentionRecord(Cursor cursor) {
        MentionRecord record = new MentionRecord();
        record.setOriginalStatusId(cursor.getLong(0));
        record.setAfterRetweetStatusId(cursor.getLong(1));
        record.setAfterFavoriteStatusId(cursor.getLong(2));
        record.setReplyToStatusId(cursor.getLong(3));
        record.setUserId(cursor.getLong(4));
        record.setRetweetedByUserId(cursor.getLong(5));
        record.setAvatarURL(cursor.getString(6));
        record.setCreatedAt(cursor.getString(7));
        record.setName(cursor.getString(8));
        record.setScreenName(cursor.getString(9));
        record.setProtect(
                cursor.getString(10).equals("true")
        );
        record.setCheckIn(cursor.getString(11));
        record.setText(cursor.getString(12));
        record.setRetweet(
                cursor.getString(13).equals("true")
        );
        record.setRetweetedByUserName(cursor.getString(14));
        record.setFavorite(
                cursor.getString(15).equals("true")
        );

        return record;
    }
    public List<MentionRecord> getMentionRecordList() {
        List<MentionRecord> mentionRecordList = new ArrayList<MentionRecord>();
        Cursor cursor = database.query(
                MentionRecord.TABLE,
                new String[] {
                        MentionRecord.ORIGINAL_STATUS_ID,
                        MentionRecord.AFTER_RETWEET_STATUS_ID,
                        MentionRecord.AFTER_FAVORITE_STATUS_ID,
                        MentionRecord.REPLY_TO_STATUS_ID,
                        MentionRecord.USER_ID,
                        MentionRecord.RETWEETED_BY_USER_ID,
                        MentionRecord.AVATAR_URL,
                        MentionRecord.CREATED_AT,
                        MentionRecord.NAME,
                        MentionRecord.SCREEN_NAME,
                        MentionRecord.PROTECT,
                        MentionRecord.CHECK_IN,
                        MentionRecord.TEXT,
                        MentionRecord.RETWEET,
                        MentionRecord.RETWEETED_BY_USER_NAME,
                        MentionRecord.FAVORITE
                },
                null,
                null,
                null,
                null,
                null
        );

        if (cursor == null) {
            return mentionRecordList;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MentionRecord record = getMentionRecord(cursor);
            mentionRecordList.add(record);
            cursor.moveToNext();
        }
        cursor.close();

        return mentionRecordList;
    }
}
