package io.github.mthli.Tweetin.Tweet;

public class Tweet {
    private long tweetId;
    private long userId;
    private String avatarUrl;
    private String createdAt;
    private String name;
    private String screenName;
    private String text;
    private boolean retweeted;
    private String retweetedBy;

    public Tweet() {
        this.tweetId = 0;
        this.userId = 0;
        this.avatarUrl = null;
        this.createdAt = null;
        this.name = null;
        this.screenName = null;
        this.text = null;
        this.retweeted = false;
        this.retweetedBy = null;
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

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public boolean isRetweeted() {
        return retweeted;
    }
    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public String getRetweetedBy() {
        return retweetedBy;
    }
    public void setRetweetedBy(String retweetedBy) {
        this.retweetedBy = retweetedBy;
    }
}
