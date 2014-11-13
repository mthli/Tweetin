package io.github.mthli.Tweetin.Database.Timeline;

public class TimelineRecord {
    public static final String TABLE = "TIMELINE";
    public static final String STATUS_ID = "STATUS_ID";
    public static final String REPLY_TO_STATUS_ID = "REPLY_TO_STATUS_ID";
    public static final String USER_ID = "USER_ID";
    public static final String RETWEETED_BY_USER_ID = "RETWEETED_BY_USER_ID";
    public static final String AVATAR_URL = "AVATAR_URL";
    public static final String CREATED_AT = "CREATED_AT";
    public static final String NAME = "NAME";
    public static final String SCREEN_NAME = "SCREEN_NAME";
    public static final String PROTECT = "PROTECT";
    public static final String CHECK_IN = "CHECK_IN";
    public static final String PHOTO_URL = "PHOTO_URL";
    public static final String TEXT = "TEXT";
    public static final String RETWEET = "RETWEET";
    public static final String RETWEETED_BY_USER_NAME = "REWTEETED_BY_USER_NAME";
    public static final String FAVORITE = "FAVORITE";

    public static final String CREATE_SQL = "CREATE TABLE "
            + TABLE
            + " ("
            + " " + STATUS_ID + " integer,"
            + " " + REPLY_TO_STATUS_ID + " integer,"
            + " " + USER_ID + " integer,"
            + " " + RETWEETED_BY_USER_ID + " integer,"
            + " " + AVATAR_URL + " text,"
            + " " + CREATED_AT + " text,"
            + " " + NAME + " text,"
            + " " + SCREEN_NAME + " text,"
            + " " + PROTECT + " text,"
            + " " + CHECK_IN + " text,"
            + " " + PHOTO_URL + " text,"
            + " " + TEXT + " text,"
            + " " + RETWEET + " text,"
            + " " + RETWEETED_BY_USER_NAME + " text,"
            + " " + FAVORITE + " text"
            + ")";

    private long statusId;
    private long replyToStatusId;
    private long userId;
    private long retweetedByUserId;
    private String avatarURL;
    private String createdAt;
    private String name;
    private String screenName;
    private String protect;
    private String checkIn;
    private String photoURL;
    private String text;
    private String retweet;
    private String retweetedByUserName;
    private String favorite;

    public TimelineRecord() {
        this.statusId = -1;
        this.replyToStatusId = -1;
        this.userId = -1;
        this.retweetedByUserId = -1;
        this.avatarURL = null;
        this.createdAt = null;
        this.name = null;
        this.screenName = null;
        this.protect = null;
        this.checkIn = null;
        this.text = null;
        this.retweet = null;
        this.retweetedByUserName = null;
        this.favorite = null;
    }

    public long getStatusId() {
        return statusId;
    }
    public void setStatusId(long statusId) {
        this.statusId = statusId;
    }

    public long getReplyToStatusId() {
        return replyToStatusId;
    }
    public void setReplyToStatusId(long replyToStatusId) {
        this.replyToStatusId = replyToStatusId;
    }

    public long getUserId() {
        return userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getRetweetedByUserId() {
        return retweetedByUserId;
    }
    public void setRetweetedByUserId(long retweetedByUserId) {
        this.retweetedByUserId = retweetedByUserId;
    }

    public String getAvatarURL() {
        return avatarURL;
    }
    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getScreenName() {
        return screenName;
    }
    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public boolean isProtect() {
        return protect.equals("true");
    }
    public void setProtect(boolean protect) {
        if (protect) {
            this.protect = "true";
        } else {
            this.protect = "false";
        }
    }

    public String getCheckIn() {
        return checkIn;
    }
    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }

    public String getPhotoURL() {
        return photoURL;
    }
    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public boolean isRetweet() {
        return retweet.equals("true");
    }
    public void setRetweet(boolean retweet) {
        if (retweet) {
            this.retweet = "true";
        } else {
            this.retweet = "false";
        }
    }

    public String getRetweetedByUserName() {
        return retweetedByUserName;
    }
    public void setRetweetedByUserName(String retweetedByUserName) {
        this.retweetedByUserName = retweetedByUserName;
    }

    public boolean isFavorite() {
        return favorite.equals("true");
    }
    public void setFavorite(boolean favorite) {
        if (favorite) {
            this.favorite = "true";
        } else {
            this.favorite = "false";
        }
    }
}
