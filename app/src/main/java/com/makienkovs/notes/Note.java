package com.makienkovs.notes;

public class Note {

    private String content;
    private long time;
    private boolean isDone;
    private boolean isCancel;
    private boolean isSelected;

    public Note() {
        isDone = false;
        isCancel = false;
        isSelected = false;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public boolean isCancel() { return isCancel; }

    public void setCancel(boolean cancel) { isCancel = cancel; }

    public boolean isSelected() { return isSelected; }

    public void setSelected(boolean selected) { isSelected = selected; }
}
