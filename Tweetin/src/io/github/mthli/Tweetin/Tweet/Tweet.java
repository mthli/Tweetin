package io.github.mthli.Tweetin.Tweet;

public class Tweet {
    private long tweetId;
    private long userId;
    private String avatarUrl;
    private String createdAt;
    private String name;
    private String screenName;
    private boolean protect;
    private String text;
    private boolean retweet;
    private String retweetedByName;
    private long retweetedById;
    private long replyTo;

    public Tweet() {
        this.tweetId = 0;
        this.userId = 0;
        this.avatarUrl = null;
        this.createdAt = null;
        this.name = null;
        this.screenName = null;
        this.protect = false;
        this.text = null;
        this.retweet = false;
        this.retweetedByName = null;
        this.retweetedById = 0;
        this.replyTo = -1;
    }

    public long getTweetId() {
        return tweetId;
    }
    public void setTweetId(long tweetId) {
        this.tweetId = tweetId;
    }

    public long getUserId() {
        return userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
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

    public boolean isProtected() {
        return protect;
    }
    public void setProtect(Boolean protect) {
        this.protect = protect;
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

    public String getRetweetedByName() {
        return retweetedByName;
    }
    public void setRetweetedByName(String retweetedByName) {
        this.retweetedByName = retweetedByName;
    }

    public long getRetweetedById() {
        return retweetedById;
    }
    public void setRetweetedById(long retweetedById) {
        this.retweetedById = retweetedById;
    }

    public long getReplyTo() {
        return replyTo;
    }
    public void setReplyTo(long replyTo) {
        this.replyTo = replyTo;
    }
}
