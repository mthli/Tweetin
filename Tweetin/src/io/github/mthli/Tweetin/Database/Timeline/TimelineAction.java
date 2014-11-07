package io.github.mthli.Tweetin.Database.Timeline;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;

import java.util.ArrayList;
import java.util.List;

public class TimelineAction {
    private SQLiteDatabase database;
    private TimelineHelper helper;
    private Context context;

    public TimelineAction(Context context) {
        this.helper = new TimelineHelper(context);
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

    public void addRecord(TimelineRecord record) {
        ContentValues values = new ContentValues();
        values.put(TimelineRecord.ORIGINAL_STATUS_ID, record.getOriginalStatusId());
        values.put(TimelineRecord.AFTER_RETWEET_STATUS_ID, record.getAfterRetweetStatusId());
        values.put(TimelineRecord.AFTER_FAVORITE_STATUS_ID, record.getAfterFavoriteStatusId());
        values.put(TimelineRecord.REPLY_TO_STATUS_ID, record.getReplyToStatusId());
        values.put(TimelineRecord.USER_ID, record.getUserId());
        values.put(TimelineRecord.RETWEETED_BY_USER_ID, record.getRetweetedByUserId());
        values.put(TimelineRecord.AVATAR_URL, record.getAvatarURL());
        values.put(TimelineRecord.CREATED_AT, record.getCreatedAt());
        values.put(TimelineRecord.NAME, record.getName());
        values.put(TimelineRecord.SCREEN_NAME, record.getScreenName());
        if (record.isProtect()) {
            values.put(TimelineRecord.PROTECT, "true");
        } else {
            values.put(TimelineRecord.PROTECT, "false");
        }
        values.put(TimelineRecord.CHECK_IN, record.getCheckIn());
        values.put(TimelineRecord.TEXT, record.getText());
        if (record.isRetweet()) {
            values.put(TimelineRecord.RETWEET, "true");
        } else {
            values.put(TimelineRecord.RETWEET, "false");
        }
        values.put(TimelineRecord.RETWEETED_BY_USER_NAME, record.getRetweetedByUserName());
        if (record.isFavorite()) {
            values.put(TimelineRecord.FAVORITE, "true");
        } else {
            values.put(TimelineRecord.FAVORITE, "false");
        }
        database.insert(TimelineRecord.TABLE, null, values);
    }

    /* Do something */
    public void updatedByRetweet(Tweet newTweet) {
        ContentValues values = new ContentValues();
        values.put(
                TimelineRecord.AFTER_RETWEET_STATUS_ID,
                newTweet.getAfterRetweetStatusId()
        );
        values.put(
                TimelineRecord.RETWEETED_BY_USER_ID,
                newTweet.getRetweetedByUserId()
        );
        if (newTweet.isRetweet()) {
            values.put(
                    TimelineRecord.RETWEET,
                    "true"
            );
        } else {
            values.put(
                    TimelineRecord.RETWEET,
                    "false"
            );
        }
        values.put(
                TimelineRecord.RETWEETED_BY_USER_NAME,
                newTweet.getRetweetedByUserName()
        );

        database.update(
                TimelineRecord.TABLE,
                values,
                TimelineRecord.ORIGINAL_STATUS_ID + "=?",
                new String[] {String.valueOf(newTweet.getOriginalStatusId())}
        );
    }

    /* Do something */
    public void updatedByFavorite(Tweet newTweet) {
        ContentValues values = new ContentValues();
        values.put(
                TimelineRecord.AFTER_FAVORITE_STATUS_ID,
                newTweet.getAfterFavoriteStatusId()
        );
        if (newTweet.isFavorite()) {
            values.put(
                    TimelineRecord.FAVORITE,
                    "true"
            );
        } else {
            values.put(
                    TimelineRecord.FAVORITE,
                    "false"
            );
        }

        database.update(
                TimelineRecord.TABLE,
                values,
                TimelineRecord.ORIGINAL_STATUS_ID + "=?",
                new String[] {String.valueOf(newTweet.getOriginalStatusId())}
        );
    }

    public void deleteAll() {
        database.execSQL("DELETE FROM " + TimelineRecord.TABLE);
    }

    private TimelineRecord getTimelineRecord(Cursor cursor) {
        TimelineRecord record = new TimelineRecord();
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
    public List<TimelineRecord> getTimelineRecordList() {
        List<TimelineRecord> timelineRecordList = new ArrayList<TimelineRecord>();
        Cursor cursor = database.query(
                TimelineRecord.TABLE,
                new String[] {
                        TimelineRecord.ORIGINAL_STATUS_ID,
                        TimelineRecord.AFTER_RETWEET_STATUS_ID,
                        TimelineRecord.AFTER_FAVORITE_STATUS_ID,
                        TimelineRecord.REPLY_TO_STATUS_ID,
                        TimelineRecord.USER_ID,
                        TimelineRecord.RETWEETED_BY_USER_ID,
                        TimelineRecord.AVATAR_URL,
                        TimelineRecord.CREATED_AT,
                        TimelineRecord.NAME,
                        TimelineRecord.SCREEN_NAME,
                        TimelineRecord.PROTECT,
                        TimelineRecord.CHECK_IN,
                        TimelineRecord.TEXT,
                        TimelineRecord.RETWEET,
                        TimelineRecord.RETWEETED_BY_USER_NAME,
                        TimelineRecord.FAVORITE
                },
                null,
                null,
                null,
                null,
                null
        );

        if (cursor == null) {
            return timelineRecordList;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            TimelineRecord record = getTimelineRecord(cursor);
            timelineRecordList.add(record);
            cursor.moveToNext();
        }
        cursor.close();

        return timelineRecordList;
    }
}
