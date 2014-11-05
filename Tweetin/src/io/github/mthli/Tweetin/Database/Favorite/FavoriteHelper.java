package io.github.mthli.Tweetin.Database.Favorite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavoriteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "FAVORITE_2.db";
    private static final int DATABASE_VERSION = 2;

    public FavoriteHelper(Context context) {
        super(
                context,
                DATABASE_NAME,
                null,
                DATABASE_VERSION
        );
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(FavoriteRecord.CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        /* Do nothing */
    }
}
