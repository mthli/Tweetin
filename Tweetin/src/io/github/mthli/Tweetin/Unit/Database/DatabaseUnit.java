package io.github.mthli.Tweetin.Unit.Database;

import android.content.Context;
import io.github.mthli.Tweetin.Database.Favorite.FavoriteAction;
import io.github.mthli.Tweetin.Database.Mention.MentionAction;
import io.github.mthli.Tweetin.Database.Timeline.TimelineAction;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;

public class DatabaseUnit {

    public static void updatedByRetweet(
            Context context,
            Tweet oldTweet
    ) {
        TimelineAction action = new TimelineAction(context);
        action.openDatabase(true);
        action.updatedByRetweet(oldTweet);
        action.closeDatabase();
        MentionAction mentionAction = new MentionAction(context);
        mentionAction.openDatabase(true);
        mentionAction.updatedByRetweet(oldTweet);
        mentionAction.closeDatabase();
        FavoriteAction favoriteAction = new FavoriteAction(context);
        favoriteAction.openDatabase(true);
        favoriteAction.updatedByRetweet(oldTweet);
        favoriteAction.closeDatabase();
    }

    public static void updatedByFavorite(
            Context context,
            Tweet oldTweet
    ) {
        TimelineAction action = new TimelineAction(context);
        action.openDatabase(true);
        action.updatedByFavorite(oldTweet);
        action.closeDatabase();
        MentionAction mentionAction = new MentionAction(context);
        mentionAction.openDatabase(true);
        mentionAction.updatedByFavorite(oldTweet);
        mentionAction.closeDatabase();
        FavoriteAction favoriteAction = new FavoriteAction(context);
        favoriteAction.openDatabase(true);
        favoriteAction.updatedByFavorite(oldTweet);
        favoriteAction.closeDatabase();
    }

    public static void deleteRecord(
            Context context,
            Tweet oldTweet
    ) {
        TimelineAction timelineAction = new TimelineAction(context);
        timelineAction.openDatabase(true);
        timelineAction.deleteRecord(oldTweet);
        timelineAction.closeDatabase();
        MentionAction mentionAction = new MentionAction(context);
        mentionAction.openDatabase(true);
        mentionAction.deleteRecord(oldTweet);
        mentionAction.closeDatabase();
        FavoriteAction favoriteAction = new FavoriteAction(context);
        favoriteAction.openDatabase(true);
        favoriteAction.deleteRecord(oldTweet);
        favoriteAction.closeDatabase();
    }

    public static void deleteAll(Context context) {
        TimelineAction timelineAction = new TimelineAction(context);
        timelineAction.openDatabase(true);
        timelineAction.deleteAll();
        timelineAction.closeDatabase();
        MentionAction mentionAction = new MentionAction(context);
        mentionAction.openDatabase(true);
        mentionAction.deleteAll();
        mentionAction.closeDatabase();
        FavoriteAction favoriteAction = new FavoriteAction(context);
        favoriteAction.openDatabase(true);
        favoriteAction.deleteAll();
        favoriteAction.closeDatabase();
    }

}
