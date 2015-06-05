package com.frankd.wttv;

/**
 * Created by FrankD on 5-6-2015.
 */
public class Artist {
    private int ID;
    private String name;
    private String description;
    private Day day;
    private Stage stage;
    private String time;
    private int thumbnailImageId;

    public Artist(int ID, String name, String description, Day day, Stage stage, String time){
        this.name = name;
        this.description = description;
        this.day = day;
        this.stage = stage;
        this.time = time;
    }

    public Artist(int ID,String name, String description, Day day, Stage stage, String time, int squareImageId){
        this.name = name;
        this.description = description;
        this.day = day;
        this.stage = stage;
        this.time = time;
        this.thumbnailImageId = squareImageId;
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

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
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
}
