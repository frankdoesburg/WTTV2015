package com.frankd.wttv;

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
    private String youtubeLink;
    private int thumbnailImageId;

    public Artist(){
        //do nothing
    }

    public Artist(int ID, String name, String description, String day, String stage, String time){
        this.name = name;
        this.description = description;
        this.day = day;
        this.stage = stage;
        this.time = time;
    }

    public Artist(int ID,String name, String description, String day, String stage, String time, int squareImageId){
        this.name = name;
        this.description = description;
        this.day = day;
        this.stage = stage;
        this.time = time;
        this.thumbnailImageId = squareImageId;
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

    public int getThumbnailImageId() {
        return thumbnailImageId;
    }

    public void setThumbnailImageId(int thumbnailImageId) {
        this.thumbnailImageId = thumbnailImageId;
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
}
