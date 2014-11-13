package io.github.mthli.Tweetin.Database.Timeline;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import io.github.mthli.Tweetin.R;

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
        values.put(TimelineRecord.STATUS_ID, record.getStatusId());
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
        values.put(TimelineRecord.PHOTO_URL, record.getPhotoURL());
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

    public void updatedByRetweet(long statusId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.sp_name),
                Context.MODE_PRIVATE
        );
        long useId = sharedPreferences.getLong(
                context.getString(R.string.sp_use_id),
                -1
        );

        ContentValues values = new ContentValues();
        values.put(
                TimelineRecord.RETWEETED_BY_USER_ID,
                useId
        );
        values.put(
                TimelineRecord.RETWEET,
                "true"
        );
        values.put(
                TimelineRecord.RETWEETED_BY_USER_NAME,
                context.getString(R.string.tweet_info_retweeted_by_me)
        );

        database.update(
                TimelineRecord.TABLE,
                values,
                TimelineRecord.STATUS_ID + "=?",
                new String[] {String.valueOf(statusId)}
        );
    }

    public void updatedByFavorite(long statusId) {
        ContentValues values = new ContentValues();
        values.put(
                TimelineRecord.FAVORITE,
                "true"
        );
        database.update(
                TimelineRecord.TABLE,
                values,
                TimelineRecord.STATUS_ID + "=?",
                new String[] {String.valueOf(statusId)}
        );
    }

    public void deleteRecord(long statusId) {
        database.execSQL("DELETE FROM "
                        + TimelineRecord.TABLE
                        + " WHERE "
                        + TimelineRecord.STATUS_ID
                        + " like \""
                        + String.valueOf(statusId)
                        + "\""
        );
    }

    public void deleteAll() {
        database.execSQL("DELETE FROM " + TimelineRecord.TABLE);
    }

    private TimelineRecord getTimelineRecord(Cursor cursor) {
        TimelineRecord record = new TimelineRecord();
        record.setStatusId(cursor.getLong(0));
        record.setReplyToStatusId(cursor.getLong(1));
        record.setUserId(cursor.getLong(2));
        record.setRetweetedByUserId(cursor.getLong(3));
        record.setAvatarURL(cursor.getString(4));
        record.setCreatedAt(cursor.getString(5));
        record.setName(cursor.getString(6));
        record.setScreenName(cursor.getString(7));
        record.setProtect(
                cursor.getString(8).equals("true")
        );
        record.setCheckIn(cursor.getString(9));
        record.setPhotoURL(cursor.getString(10));
        record.setText(cursor.getString(11));
        record.setRetweet(
                cursor.getString(12).equals("true")
        );
        record.setRetweetedByUserName(cursor.getString(13));
        record.setFavorite(
                cursor.getString(14).equals("true")
        );

        return record;
    }
    public List<TimelineRecord> getTimelineRecordList() {
        List<TimelineRecord> timelineRecordList = new ArrayList<TimelineRecord>();
        Cursor cursor = database.query(
                TimelineRecord.TABLE,
                new String[] {
                        TimelineRecord.STATUS_ID,
                        TimelineRecord.REPLY_TO_STATUS_ID,
                        TimelineRecord.USER_ID,
                        TimelineRecord.RETWEETED_BY_USER_ID,
                        TimelineRecord.AVATAR_URL,
                        TimelineRecord.CREATED_AT,
                        TimelineRecord.NAME,
                        TimelineRecord.SCREEN_NAME,
                        TimelineRecord.PROTECT,
                        TimelineRecord.CHECK_IN,
                        TimelineRecord.PHOTO_URL,
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
