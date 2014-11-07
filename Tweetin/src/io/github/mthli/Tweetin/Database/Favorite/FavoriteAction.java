package io.github.mthli.Tweetin.Database.Favorite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;

import java.util.ArrayList;
import java.util.List;

public class FavoriteAction {
    private SQLiteDatabase database;
    private FavoriteHelper helper;
    private Context context;
    
    public FavoriteAction(Context context) {
        this.helper = new FavoriteHelper(context);
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


    public void addRecord(FavoriteRecord record) {
        ContentValues values = new ContentValues();
        values.put(FavoriteRecord.ORIGINAL_STATUS_ID, record.getOriginalStatusId());
        values.put(FavoriteRecord.AFTER_RETWEET_STATUS_ID, record.getAfterRetweetStatusId());
        values.put(FavoriteRecord.AFTER_FAVORITE_STATUS_ID, record.getAfterFavoriteStatusId());
        values.put(FavoriteRecord.REPLY_TO_STATUS_ID, record.getReplyToStatusId());
        values.put(FavoriteRecord.USER_ID, record.getUserId());
        values.put(FavoriteRecord.RETWEETED_BY_USER_ID, record.getRetweetedByUserId());
        values.put(FavoriteRecord.AVATAR_URL, record.getAvatarURL());
        values.put(FavoriteRecord.CREATED_AT, record.getCreatedAt());
        values.put(FavoriteRecord.NAME, record.getName());
        values.put(FavoriteRecord.SCREEN_NAME, record.getScreenName());
        if (record.isProtect()) {
            values.put(FavoriteRecord.PROTECT, "true");
        } else {
            values.put(FavoriteRecord.PROTECT, "false");
        }
        values.put(FavoriteRecord.CHECK_IN, record.getCheckIn());
        values.put(FavoriteRecord.TEXT, record.getText());
        if (record.isRetweet()) {
            values.put(FavoriteRecord.RETWEET, "true");
        } else {
            values.put(FavoriteRecord.RETWEET, "false");
        }
        values.put(FavoriteRecord.RETWEETED_BY_USER_NAME, record.getRetweetedByUserName());
        if (record.isFavorite()) {
            values.put(FavoriteRecord.FAVORITE, "true");
        } else {
            values.put(FavoriteRecord.FAVORITE, "false");
        }
        database.insert(FavoriteRecord.TABLE, null, values);
    }

    /* Do something */
    public void updatedByRetweet(Tweet newTweet) {
        ContentValues values = new ContentValues();
        values.put(
                FavoriteRecord.AFTER_RETWEET_STATUS_ID,
                newTweet.getAfterRetweetStatusId()
        );
        values.put(
                FavoriteRecord.RETWEETED_BY_USER_ID,
                newTweet.getRetweetedByUserId()
        );
        if (newTweet.isRetweet()) {
            values.put(
                    FavoriteRecord.RETWEET,
                    "true"
            );
        } else {
            values.put(
                    FavoriteRecord.RETWEET,
                    "false"
            );
        }
        values.put(
                FavoriteRecord.RETWEETED_BY_USER_NAME,
                newTweet.getRetweetedByUserName()
        );

        database.update(
                FavoriteRecord.TABLE,
                values,
                FavoriteRecord.ORIGINAL_STATUS_ID + "=?",
                new String[] {String.valueOf(newTweet.getOriginalStatusId())}
        );
    }

    /* Do something */
    public void updatedByFavorite(Tweet newTweet) {
        ContentValues values = new ContentValues();
        values.put(
                FavoriteRecord.AFTER_FAVORITE_STATUS_ID,
                newTweet.getAfterFavoriteStatusId()
        );
        if (newTweet.isFavorite()) {
            values.put(
                    FavoriteRecord.FAVORITE,
                    "true"
            );
        } else {
            values.put(
                    FavoriteRecord.FAVORITE,
                    "false"
            );
        }

        database.update(
                FavoriteRecord.TABLE,
                values,
                FavoriteRecord.ORIGINAL_STATUS_ID + "=?",
                new String[] {String.valueOf(newTweet.getOriginalStatusId())}
        );
    }

    public void deleteAll() {
        database.execSQL("DELETE FROM " + FavoriteRecord.TABLE);
    }

    private FavoriteRecord getFavoriteRecord(Cursor cursor) {
        FavoriteRecord record = new FavoriteRecord();
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
    public List<FavoriteRecord> getFavoriteRecordList() {
        List<FavoriteRecord> timelineRecordList = new ArrayList<FavoriteRecord>();
        Cursor cursor = database.query(
                FavoriteRecord.TABLE,
                new String[] {
                        FavoriteRecord.ORIGINAL_STATUS_ID,
                        FavoriteRecord.AFTER_RETWEET_STATUS_ID,
                        FavoriteRecord.AFTER_FAVORITE_STATUS_ID,
                        FavoriteRecord.REPLY_TO_STATUS_ID,
                        FavoriteRecord.USER_ID,
                        FavoriteRecord.RETWEETED_BY_USER_ID,
                        FavoriteRecord.AVATAR_URL,
                        FavoriteRecord.CREATED_AT,
                        FavoriteRecord.NAME,
                        FavoriteRecord.SCREEN_NAME,
                        FavoriteRecord.PROTECT,
                        FavoriteRecord.CHECK_IN,
                        FavoriteRecord.TEXT,
                        FavoriteRecord.RETWEET,
                        FavoriteRecord.RETWEETED_BY_USER_NAME,
                        FavoriteRecord.FAVORITE
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
            FavoriteRecord record = getFavoriteRecord(cursor);
            timelineRecordList.add(record);
            cursor.moveToNext();
        }
        cursor.close();

        return timelineRecordList;
    }
}
