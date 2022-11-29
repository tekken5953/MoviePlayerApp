package app.smartscreenapp;

import android.net.Uri;

public class VideoListItem {
    private Uri thumbnail;
    private String title;
    private String time;

    public VideoListItem(Uri thumbnail, String title, String time) {
        this.thumbnail = thumbnail;
        this.title = title;
        this.time = time;
    }

    public Uri getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Uri thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}