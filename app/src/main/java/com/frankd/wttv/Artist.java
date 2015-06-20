package com.frankd.wttv;

import android.graphics.Bitmap;

/**
 * Created by FrankD on 5-6-2015.
 */
public class Artist {
    private int ID;
    private String name;
    private String description;
    private String day;
    private String stage;
    private String time;
    private String startTime;
    private String endTime;
    private String location;
    private String youtubeLink;
    private boolean favorite;
    private Bitmap thumbnailImage;
    private Bitmap largeImage;
    private byte[] thumbnailImageBlob;
    private byte[] largeImageBlob;

    private int thumbnailImageId; //id of thumbnail square image
    private int largeImageID; //id of 3x2 size large image

    public Artist(){
        //do nothing
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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

    public int getThumbnailImageId() {
        return thumbnailImageId;
    }

    public void setThumbnailImageId(int thumbnailImageId) {
        this.thumbnailImageId = thumbnailImageId;
    }

    public int getLargeImageID() {
        return largeImageID;
    }

    public void setLargeImageID(int largeImageID) {
        this.largeImageID = largeImageID;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public void setYoutubeLink(String youtubeLink) {
        this.youtubeLink = youtubeLink;
    }

    public String toString(){
        return "ID: " + ID + ", name: " + name;
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
