package io.github.mthli.Tweetin.Tweet;

public class Tweet {
    private String avatarURL;
    private String name;
    private String screenName;
    private long createdAt;
    private String checkIn;
    private boolean protect;
    private String pictureURL;
    private String text;
    private String retweetedByName;
    private boolean favorite;

    private long statusId;
    private long inReplyToStatusId;
    private String retweetedByScreenName;

    private boolean detail;

    public Tweet() {
        this.avatarURL = null;
        this.name = null;
        this.screenName = null;
        this.createdAt = -1l;
        this.checkIn = null;
        this.protect = false;
        this.pictureURL = null;
        this.text = null;
        this.retweetedByName = null;
        this.favorite = false;

        this.statusId = -1l;
        this.inReplyToStatusId = -1l;
        this.retweetedByScreenName = null;

        this.detail = false;
    }

    public String getAvatarURL() {
        return avatarURL;
    }
    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
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

    public long getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getCheckIn() {
        return checkIn;
    }
    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }

    public boolean isProtect() {
        return protect;
    }
    public void setProtect(boolean protect) {
        this.protect = protect;
    }

    public String getPictureURL() {
        return pictureURL;
    }
    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public String getRetweetedByName() {
        return retweetedByName;
    }
    public void setRetweetedByName(String retweetedByName) {
        this.retweetedByName = retweetedByName;
    }

    public boolean isFavorite() {
        return favorite;
    }
    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public long getStatusId() {
        return statusId;
    }
    public void setStatusId(long statusId) {
        this.statusId = statusId;
    }

    public long getInReplyToStatusId() {
        return inReplyToStatusId;
    }
    public void setInReplyToStatusId(long inReplyToStatusId) {
        this.inReplyToStatusId = inReplyToStatusId;
    }

    public String getRetweetedByScreenName() {
        return retweetedByScreenName;
    }
    public void setRetweetedByScreenName(String retweetedByScreenName) {
        this.retweetedByScreenName = retweetedByScreenName;
    }

    public boolean isDetail() {
        return detail;
    }
    public void setDetail(boolean detail) {
        this.detail = detail;
    }
}
