package app.smartscreenapp;

import android.net.Uri;

public class VideoListItem {
    private Uri thumbnail;

    public VideoListItem(Uri thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Uri getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Uri thumbnail) {
        this.thumbnail = thumbnail;
    }
}