package io.github.mthli.Tweetin.Unit.ContextMenu;

public class ContextMenuItem {
    private String title;
    private int flag;
    private boolean active;

    public ContextMenuItem(
            String title,
            int flag,
            boolean active
    ) {
        this.title = title;
        this.flag = flag;
        this.active = active;
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
