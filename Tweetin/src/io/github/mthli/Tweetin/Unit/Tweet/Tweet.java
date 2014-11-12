package io.github.mthli.Tweetin.Unit.Tweet;

public class Tweet {
    private long statusId;
    private long replyToStatusId;
    private long userId;
    private long retweetedByUserId;
    private String avatarURL;
    private String createdAt;
    private String name;
    private String screenName;
    private boolean protect;
    private String checkIn;
    private String photoURL;
    private String text;
    private boolean retweet;
    private String retweetedByUserName;
    private boolean favorite;

    public Tweet() {
        this.statusId = -1l;
        this.replyToStatusId = -1l;
        this.userId = -1l;
        this.retweetedByUserId = -1l;
        this.avatarURL = null;
        this.createdAt = null;
        this.name = null;
        this.screenName = null;
        this.protect = false;
        this.checkIn = null;
        this.photoURL = null;
        this.text = null;
        this.retweet = false;
        this.retweetedByUserName = null;
        this.favorite = false;
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
        return protect;
    }
    public void setProtect(boolean protect) {
        this.protect = protect;
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
        return retweet;
    }
    public void setRetweet(boolean retweet) {
        this.retweet = retweet;
    }

    public String getRetweetedByUserName() {
        return retweetedByUserName;
    }
    public void setRetweetedByUserName(String retweetedByUserName) {
        this.retweetedByUserName = retweetedByUserName;
    }

    public boolean isFavorite() {
        return favorite;
    }
    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
