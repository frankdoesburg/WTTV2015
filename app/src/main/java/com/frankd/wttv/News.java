package com.frankd.wttv;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by Tom on 28/6/15.
 */
public class News {

    private int id;
    private Date datePublish;
    private String title;
    private String teaser;
    private String body;
    private String video;
    private byte[] imageBlob;
    private byte[] largeImageBlob;
    private Bitmap image;
    private Bitmap largeImage;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDatePublish() {
        return datePublish;
    }

    public void setDatePublish(Date datePublish) {
        this.datePublish = datePublish;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTeaser() {
        return teaser;
    }

    public void setTeaser(String teaser) {
        this.teaser = teaser;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public byte[] getImageBlob() {
        return imageBlob;
    }

    public void setImageBlob(byte[] imageBlob) {
        this.imageBlob = imageBlob;
    }

    public byte[] getLargeImageBlob() {
        return largeImageBlob;
    }

    public void setLargeImageBlob(byte[] largeImageBlob) {
        this.largeImageBlob = largeImageBlob;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public Bitmap getLargeImage() {
        return largeImage;
    }

    public void setLargeImage(Bitmap largeImage) {
        this.largeImage = largeImage;
    }
}
