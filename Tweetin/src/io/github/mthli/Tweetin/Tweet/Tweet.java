package io.github.mthli.Tweetin.Tweet;

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
    private String text;
    private String checkIn;
    private boolean retweet;
    private String retweetedByUserName;

    public Tweet() {
        this.statusId = -1;
        this.replyToStatusId = -1;
        this.userId = -1;
        this.retweetedByUserId = -1;
        this.avatarURL = null;
        this.createdAt = null;
        this.name = null;
        this.screenName = null;
        this.protect = false;
        this.text = null;
        this.checkIn = null;
        this.retweet = false;
        this.retweetedByUserName = null;
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

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public String getCheckIn() {
        return checkIn;
    }
    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
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
}
