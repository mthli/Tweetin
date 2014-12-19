package io.github.mthli.Tweetin.Database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import io.github.mthli.Tweetin.R;

import java.util.ArrayList;
import java.util.List;

public class DataAction {

    private SQLiteDatabase database;
    private DataHelper helper;

    private Context context;

    public DataAction(Context context) {
        this.helper = new DataHelper(context);

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

    public void addRecord(DataRecord record, String targetTable) {
        ContentValues values = new ContentValues();

        values.put(DataUnit.AVATAR_URL, record.getAvatarURL());
        values.put(DataUnit.NAME, record.getName());
        values.put(DataUnit.SCREEN_NAME, record.getScreenName());
        values.put(DataUnit.CREATED_AT, record.getCreatedAt());
        values.put(DataUnit.CHECK_IN, record.getCheckIn());
        values.put(DataUnit.PROTECT, String.valueOf(record.isProtect()));
        values.put(DataUnit.PICTURE_URL, record.getPictureURL());
        values.put(DataUnit.TEXT, record.getText());
        values.put(DataUnit.RETWEETED_BY, record.getRetweetedBy());
        values.put(DataUnit.FAVORITE, String.valueOf(record.isFavorite()));

        values.put(DataUnit.STATUS_ID, record.getStatusId());
        values.put(DataUnit.IN_REPLY_TO_STATUS_ID, record.getInReplyToStatusId());

        database.insert(targetTable, null, values);
    }

    public void deleteRecord(DataRecord record, String targetTable) {
        database.execSQL("DELETE FROM "
                        + targetTable
                        + " WHERE "
                        + DataUnit.STATUS_ID
                        + " like \""
                        + String.valueOf(record.getStatusId())
                        + "\""
        );
    }

    public void deleteAll(String targetTable) {
        database.execSQL("DELETE FROM " + targetTable);
    }

    public void updatedByRetweet(DataRecord record, String targetTable) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.sp_name),
                Context.MODE_PRIVATE
        );
        String useScreenName = sharedPreferences.getString(
                context.getString(R.string.sp_use_screen_name),
                null
        );

        ContentValues values = new ContentValues();
        values.put(DataUnit.RETWEETED_BY, useScreenName);

        database.update(
                targetTable,
                values,
                DataUnit.STATUS_ID + "=?",
                new String[]{String.valueOf(record.getStatusId())}
        );
    }

    public void updatedByFavorite(DataRecord record, String targetTable) {
        ContentValues values = new ContentValues();
        values.put(DataUnit.FAVORITE, String.valueOf(record.isFavorite()));

        database.update(
                targetTable,
                values,
                DataUnit.STATUS_ID + "=?",
                new String[] {String.valueOf(record.getStatusId())}
        );
    }

    private DataRecord getDataRecord(Cursor cursor) {
        DataRecord record = new DataRecord();

        record.setAvatarURL(cursor.getString(0));
        record.setName(cursor.getString(1));
        record.setScreenName(cursor.getString(2));
        record.setCreatedAt(cursor.getString(3));
        record.setCheckIn(cursor.getString(4));
        record.setProtect(Boolean.valueOf(cursor.getString(5)));
        record.setPictureURL(cursor.getString(6));
        record.setText(cursor.getString(7));
        record.setRetweetedBy(cursor.getString(8));
        record.setFavorite(Boolean.valueOf(cursor.getString(9)));

        record.setStatusId(cursor.getLong(10));
        record.setInReplyToStatusId(cursor.getLong(11));

        return record;
    }

    public List<DataRecord> getDataRecordList(String targetTable) {
        List<DataRecord> dataRecordList = new ArrayList<DataRecord>();

        Cursor cursor = database.query(
                targetTable,
                new String[] {
                        DataUnit.AVATAR_URL,
                        DataUnit.NAME,
                        DataUnit.SCREEN_NAME,
                        DataUnit.CREATED_AT,
                        DataUnit.CHECK_IN,
                        DataUnit.PROTECT,
                        DataUnit.PICTURE_URL,
                        DataUnit.TEXT,
                        DataUnit.RETWEETED_BY,
                        DataUnit.FAVORITE,

                        DataUnit.STATUS_ID,
                        DataUnit.IN_REPLY_TO_STATUS_ID
                },
                null,
                null,
                null,
                null,
                null
        );

        if (cursor == null) {
            return dataRecordList;
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            DataRecord record = getDataRecord(cursor);
            dataRecordList.add(record);
            cursor.moveToNext();
        }

        cursor.close();

        return dataRecordList;
    }
}
