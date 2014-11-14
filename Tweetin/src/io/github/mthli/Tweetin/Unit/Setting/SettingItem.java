package io.github.mthli.Tweetin.Unit.Setting;

public class SettingItem {
    private String title;
    private String content;
    private boolean showCheckBox;
    private boolean checked;

    public SettingItem() {
        this.title = null;
        this.content = null;
        this.showCheckBox = false;
        this.checked = false;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public boolean isShowCheckBox() {
        return showCheckBox;
    }
    public void setShowCheckBox(boolean showCheckBox) {
        this.showCheckBox = showCheckBox;
    }

    public boolean isChecked() {
        return checked;
    }
    public void setChecked(boolean check) {
        this.checked = check;
    }
}
