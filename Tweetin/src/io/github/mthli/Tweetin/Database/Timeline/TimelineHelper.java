package io.github.mthli.Tweetin.Database.Timeline;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TimelineHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TIMELINE_4.db";
    private static final int DATABASE_VERSION = 4;

    public TimelineHelper(Context context) {
        super(
                context,
                DATABASE_NAME,
                null,
                DATABASE_VERSION
        );
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TimelineRecord.CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        /* Do nothing */
    }
}
