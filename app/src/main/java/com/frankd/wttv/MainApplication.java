package com.frankd.wttv;

import android.app.Application;
import android.database.SQLException;

import java.io.IOException;

/**
 * Created by Tom on 2/7/15.
 */
public class MainApplication extends Application {


    private static DataBaseHelper mDbHelper;


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
        DataFetcher dataFetcher = new DataFetcher();
        dataFetcher.getDataFromServer(this, mDbHelper);
        dataFetcher.getNewsFromServer(this, mDbHelper);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mDbHelper = null;
    }

    public static DataBaseHelper getDatabaseHelper() {
        return mDbHelper;
    }
}
