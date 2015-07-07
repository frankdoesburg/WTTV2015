package com.frankd.wttv;

import android.graphics.Bitmap;
import java.util.Date;

public class Artist {
    public static final String TAG = "ARTIST";

    private int id;
    private String name;
    private String description;
    private String day;
    private Date startTime;
    private Date endTime;
    private String location;
    private String youtubeLink;
    private boolean favorite;
    private Bitmap thumbnailImage;
    private Bitmap largeImage;
    private byte[] thumbnailImageBlob;
    private byte[] largeImageBlob;
    private Date updatedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public byte[] getThumbnailImageBlob() {
        return thumbnailImageBlob;
    }

    public void setThumbnailImageBlob(byte[] thumbnailImageBlob) {
        this.thumbnailImageBlob = thumbnailImageBlob;
    }

    public byte[] getLargeImageBlob() {
        return largeImageBlob;
    }

    public void setLargeImageBlob(byte[] largeImageBlob) {
        this.largeImageBlob = largeImageBlob;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public void setYoutubeLink(String youtubeLink) {
        this.youtubeLink = youtubeLink;
    }

    public String toString(){
        return "id: " + id + ", name: " + name;
    }

    public Bitmap getThumbnailImage() {
        return thumbnailImage;
    }

    public void setThumbnailImage(Bitmap thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
    }

    public Bitmap getLargeImage() {
        return largeImage;
    }

    public void setLargeImage(Bitmap largeImage) {
        this.largeImage = largeImage;
    }

}
