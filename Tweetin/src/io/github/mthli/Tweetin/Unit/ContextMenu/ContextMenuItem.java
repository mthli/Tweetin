package io.github.mthli.Tweetin.Unit.ContextMenu;

import android.graphics.drawable.Drawable;

public class ContextMenuItem {
    private Drawable icon;
    private String title;
    private int flag;
    private boolean active;

    public ContextMenuItem(
            Drawable icon,
            String title,
            int flag,
            boolean active
    ) {
        this.icon = icon;
        this.title = title;
        this.flag = flag;
        this.active = active;
    }

    public Drawable getIcon() {
        return icon;
    }
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public int getFlag() {
        return flag;
    }
    public void setFlag(int flag) {
        this.flag = flag;
    }

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
}
