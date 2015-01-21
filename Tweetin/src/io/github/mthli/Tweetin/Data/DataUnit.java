package io.github.mthli.Tweetin.Data;

import android.app.Activity;
import android.content.Context;
import io.github.mthli.Tweetin.Tweet.Tweet;
import io.github.mthli.Tweetin.Tweet.TweetUnit;

public class DataUnit {

    public static final String TIMELINE_TABLE = "TIMELINE_TABLE";
    public static final String MENTION_TABLE = "MENTION_TABLE";
    public static final String FAVORITE_TABLE = "FAVORITE_TABLE";

    public static final String AVATAR_URL = "AVATAR_URL";
    public static final String NAME = "NAME";
    public static final String SCREEN_NAME = "SCREEN_NAME";
    public static final String CREATED_AT = "CREATED_AT";
    public static final String CHECK_IN = "CHECK_IN";
    public static final String PROTECT = "PROTECT";
    public static final String PICTURE_URL = "PICTURE_URL";
    public static final String TEXT = "TEXT";
    public static final String RETWEETED_BY_NAME = "RETWEETED_BY_NAME";
    public static final String FAVORITE = "FAVORITE";

    public static final String STATUS_ID = "STATUS_ID";
    public static final String IN_REPLY_TO_STATUS_ID = "IN_REPLY_TO_STATUS_ID";
    public static final String RETWEETED_BY_SCREEN_NAME = "RETWEETED_BY_SCREEN_NAME";

    public static final String CREATE_TIMELINE_TABLE = "CREATE TABLE "
            + TIMELINE_TABLE

            + " ("
            + " " + AVATAR_URL + " text,"
            + " " + NAME + " text,"
            + " " + SCREEN_NAME + " text,"
            + " " + CREATED_AT + " integer,"
            + " " + CHECK_IN + " text,"
            + " " + PROTECT + " text,"
            + " " + PICTURE_URL + " text,"
            + " " + TEXT + " text,"
            + " " + RETWEETED_BY_NAME + " text,"
            + " " + FAVORITE + " text,"

            + " " + STATUS_ID + " integer,"
            + " " + IN_REPLY_TO_STATUS_ID + " integer,"
            + " " + RETWEETED_BY_SCREEN_NAME + " text"
            + ")";

    public static final String CREATE_MENTION_TABLE = "CREATE TABLE "
            + MENTION_TABLE

            + " ("
            + " " + AVATAR_URL + " text,"
            + " " + NAME + " text,"
            + " " + SCREEN_NAME + " text,"
            + " " + CREATED_AT + " integer,"
            + " " + CHECK_IN + " text,"
            + " " + PROTECT + " text,"
            + " " + PICTURE_URL + " text,"
            + " " + TEXT + " text,"
            + " " + RETWEETED_BY_NAME + " text,"
            + " " + FAVORITE + " text,"

            + " " + STATUS_ID + " integer,"
            + " " + IN_REPLY_TO_STATUS_ID + " integer,"
            + " " + RETWEETED_BY_SCREEN_NAME + " text"
            + ")";

    public static final String CREATE_FAVORITE_TABLE = "CREATE TABLE "
            + FAVORITE_TABLE

            + " ("
            + " " + AVATAR_URL + " text,"
            + " " + NAME + " text,"
            + " " + SCREEN_NAME + " text,"
            + " " + CREATED_AT + " integer,"
            + " " + CHECK_IN + " text,"
            + " " + PROTECT + " text,"
            + " " + PICTURE_URL + " text,"
            + " " + TEXT + " text,"
            + " " + RETWEETED_BY_NAME + " text,"
            + " " + FAVORITE + " text,"

            + " " + STATUS_ID + " integer,"
            + " " + IN_REPLY_TO_STATUS_ID + " integer,"
            + " " + RETWEETED_BY_SCREEN_NAME + " text"
            + ")";

    public static void updateByRetweet(Activity activity, Tweet tweet) {
        DataAction action = new DataAction(activity);

        DataRecord record = (new TweetUnit(activity)).getDataRecordFromTweet(tweet);

        action.openDatabase(true);

        action.updatedByRetweet(record, TIMELINE_TABLE);
        action.updatedByRetweet(record, MENTION_TABLE);
        action.updatedByRetweet(record, FAVORITE_TABLE);

        action.closeDatabase();
    }

    public static void updateByFavorite(Activity activity, Tweet tweet) {
        DataAction action = new DataAction(activity);

        DataRecord record = (new TweetUnit(activity)).getDataRecordFromTweet(tweet);

        action.openDatabase(true);

        action.updatedByFavorite(record, TIMELINE_TABLE);
        action.updatedByFavorite(record, MENTION_TABLE);
        action.updatedByFavorite(record, FAVORITE_TABLE);

        action.closeDatabase();
    }

    public static void updateByDelete(Activity activity, Tweet tweet) {
        DataAction action = new DataAction(activity);

        DataRecord record = (new TweetUnit(activity)).getDataRecordFromTweet(tweet);

        action.openDatabase(true);

        action.deleteDataRecord(record, TIMELINE_TABLE);
        action.deleteDataRecord(record, MENTION_TABLE);
        action.deleteDataRecord(record, FAVORITE_TABLE);

        action.closeDatabase();
    }
}
