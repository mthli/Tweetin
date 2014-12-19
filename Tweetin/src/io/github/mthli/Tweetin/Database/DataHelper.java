package io.github.mthli.Tweetin.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TWEETIN_3.db";
    private static final int DATABASE_VERSION = 3;

    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DataUnit.CREATE_TIMELINE_TABLE);
        database.execSQL(DataUnit.CREATE_MENTION_TABLE);
        database.execSQL(DataUnit.CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        /* Do nothing */
    }
}
