package com.frankd.wttv;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.database.SQLException;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Tom on 2/7/15.
 */
public class MainApplication extends Application {


    private static DataBaseHelper mDbHelper;
    private ArrayList<Artist> artistList;
    private ArrayList<News> newsList;


    @Override
    public void onCreate() {
        super.onCreate();

        mDbHelper = new DataBaseHelper(this);

        try {

            mDbHelper.createDataBase();

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }

        try {

            mDbHelper.openDataBase();

        }catch(SQLException sqle){

            throw sqle;

        }

        mDbHelper.close();
        System.out.println();
        DataFetcher dataFetcher = new DataFetcher();
        newsList = mDbHelper.getAllNewsFromDB();
        artistList = mDbHelper.getAllArtistsFromDB();
        dataFetcher.getDataFromServer(this, mDbHelper, artistList);
        dataFetcher.getNewsFromServer(this, mDbHelper, newsList);
        
        //Config for async image loader
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
        .imageDownloader(new SqliteImageDownloader(this, mDbHelper))
                .build();

        ImageLoader.getInstance().init(config);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mDbHelper = null;
    }

    public static DataBaseHelper getDatabaseHelper() {
        return mDbHelper;
    }

    public ArrayList<Artist> getArtists() {
        return artistList;
    }

    public ArrayList<News> getNewsList() {
        return newsList;
    }
}
