package com.example.newsfeed;

public class News {
    private String title;
    private String thumbnail;
    private String date;
    private String url;

    public News(String title, String thumbnail, String date, String url) {
        this.title = title;
        this.thumbnail = thumbnail;
        this.date = date;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }
}
