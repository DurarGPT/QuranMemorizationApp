package com.example.quranmemorizationapp;
public class VideoModel {

    private String title;
    private String description;
    private String videoUrl;

    private int thumbnail;

    public VideoModel(String title,
                      String description,
                      String videoUrl,
                      int thumbnail) {

        this.title = title;
        this.description = description;
        this.videoUrl = videoUrl;
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public int getThumbnail() {
        return thumbnail;
    }
}