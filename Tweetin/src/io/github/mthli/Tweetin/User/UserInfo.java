package io.github.mthli.Tweetin.User;

public class UserInfo {
    private String avatarUrl;
    private String backgroundUrl;
    private String name;
    private String screenName;
    private String description;
    private long userId;
    private boolean following;

    public UserInfo() {
        this.avatarUrl = null;
        this.backgroundUrl = null;
        this.name = null;
        this.screenName = null;
        this.description = null;
        this.userId = 0;
        this.following = false;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBackgroundUrl() {
        return backgroundUrl;
    }
    public void setBackgroundUrl(String backgroundUrl) {
        this.backgroundUrl = backgroundUrl;
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

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public long getUserId() {
        return userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean isFollowing() {
        return following;
    }
    public void setFollowing(boolean following) {
        this.following = following;
    }
}

