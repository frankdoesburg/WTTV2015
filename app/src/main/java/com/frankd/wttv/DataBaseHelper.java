package com.frankd.wttv;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by FrankD on 8-6-2015.
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    private final String TAG = "DataBaseHelper";
    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.frankd.wttv/databases/";

    private static String DB_NAME = "wttv.sqlite";

    private SQLiteDatabase myDataBase;

    private final Context myContext;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if(dbExist){
            //do nothing - database already exist
        }else{

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){
        //run this line when replacing database file
       // myContext.deleteDatabase(DB_NAME);

        SQLiteDatabase checkDB = null;

        try{
            String myPath = myContext.getFilesDir().getAbsolutePath().replace("files", "databases")+File.separator + DB_NAME;
            Log.v(TAG,"myPath " + myPath);
           // checkDB = dbfile.exists();

            //String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            //database does't exist yet.

        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }


    public Artist getArtistFromDB(int ID){
        Artist artist = null;

        String query = "SELECT * FROM " + "acts WHERE id='"+ ID + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()) {
            do {
                artist = new Artist();
                artist.setId(Integer.parseInt(cursor.getString(0)));
                artist.setName(cursor.getString(1));
                artist.setDescription(cursor.getString(2));
                artist.setDay(cursor.getString(3));
                String startTimeString = cursor.getString(4);
                String endTimeString = cursor.getString(5);
                DateFormat iso8601Format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                try {
                    artist.setStartTime(iso8601Format.parse(startTimeString));
                } catch (ParseException e) {
                    Log.e(TAG, "Parsing ISO8601 starttime failed", e);

                }
                try {
                    artist.setEndTime(iso8601Format.parse(endTimeString));
                } catch (ParseException e) {
                    Log.e(TAG, "Parsing ISO8601 endtime failed", e);

                }

                artist.setLocation(cursor.getString(6));
                artist.setYoutubeLink(cursor.getString(7));
                artist.setFavorite(cursor.getInt(8) == 1);
                byte[] blob = cursor.getBlob(9);
                artist.setThumbnailImageBlob(blob);
                byte[] blob2 = cursor.getBlob(10);
                artist.setLargeImageBlob(blob2);
                String updateAtString = cursor.getString(11);

                try {
                    artist.setUpdatedAt(iso8601Format.parse(updateAtString));
                } catch (ParseException e) {
                    Log.e(TAG, "Parsing ISO8601 updatedAt failed", e);
                }


            } while (cursor.moveToNext());
        }
        cursor.close();
        return artist;
    }

    public Bitmap getImageFromBase64Blob(byte[] blob){
        byte[] decodedString = Base64.decode(blob, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    public ArrayList<Artist> getFavoritesFromDB(){
        ArrayList<Artist> favorites = new ArrayList<>();

        Artist artist = null;

        String query = "SELECT * FROM acts WHERE favorite='1'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()) {
            do {
                artist = new Artist();
                artist.setId(Integer.parseInt(cursor.getString(0)));
                artist.setName(cursor.getString(1));
                artist.setDescription(cursor.getString(2));
                artist.setDay(cursor.getString(3));
                String startTimeString = cursor.getString(4);
                String endTimeString = cursor.getString(5);
                DateFormat iso8601Format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                try {
                    artist.setStartTime(iso8601Format.parse(startTimeString));
                } catch (ParseException e) {
                    Log.e(TAG, "Parsing ISO8601 starttime failed", e);

                }
                try {
                    artist.setEndTime(iso8601Format.parse(endTimeString));
                } catch (ParseException e) {
                    Log.e(TAG, "Parsing ISO8601 endtime failed", e);

                }

                artist.setLocation(cursor.getString(6));
                artist.setYoutubeLink(cursor.getString(7));
                artist.setFavorite(cursor.getInt(8) == 1);
                byte[] blob = cursor.getBlob(9);
                artist.setThumbnailImageBlob(blob);
                byte[] blob2 = cursor.getBlob(10);
                artist.setLargeImageBlob(blob2);
                String updateAtString = cursor.getString(11);

                try {
                    artist.setUpdatedAt(iso8601Format.parse(updateAtString));
                } catch (ParseException e) {
                    Log.e(TAG, "Parsing ISO8601 updatedAt failed", e);
                }
                favorites.add(artist);

            } while (cursor.moveToNext());
        }
        cursor.close();
        Log.v(TAG,"found favorites " + favorites.size());

        return favorites;
    }

    public void insertArtist(Artist artist) {
        ContentValues values = new ContentValues();

        DateFormat iso8601Format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        values.put("id", artist.getId());
        values.put("name", artist.getName());
        values.put("description", artist.getDescription());
        values.put("location", artist.getLocation());
        if(artist.getStartTime()!= null) {
            values.put("startTime", iso8601Format.format(artist.getStartTime()));
        } else {
            values.put("startTime", "");
        }

        if(artist.getEndTime()!= null) {
            values.put("endTime", iso8601Format.format(artist.getEndTime()));
        } else {
            values.put("endTime", "");
        }
        values.put("day", artist.getDay());
        values.put("youtube", artist.getYoutubeLink());
        values.put("favorite", artist.isFavorite());
        values.put("image", artist.getThumbnailImageBlob());
        values.put("largeImage", artist.getLargeImageBlob());
        values.put("updatedAt", iso8601Format.format(artist.getUpdatedAt()));

        SQLiteDatabase db = this.getWritableDatabase();

        db.insertWithOnConflict("acts", "null", values, db.CONFLICT_REPLACE);
    }

    public void insertNews(News news) {
        ContentValues values = new ContentValues();

        DateFormat iso8601Format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        values.put("id", news.getId());
        values.put("title", news.getTitle());
        values.put("datePublish", iso8601Format.format(news.getDatePublish()));
        values.put("teaser", news.getTeaser());
        values.put("body", news.getBody());
        values.put("image", news.getImageBlob());
        values.put("largeImage", news.getLargeImageBlob());
        values.put("video", news.getVideo());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insertWithOnConflict("news", "null", values, db.CONFLICT_REPLACE);
    }

    public ArrayList<Artist> getAllArtistsFromDB() {
        ArrayList<Artist> artists = new ArrayList<>();

        String query = "SELECT * FROM acts ORDER BY name";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);


        Artist artist = null;
        if(cursor.moveToFirst()) {
            do {
                artist = new Artist();
                artist.setId(Integer.parseInt(cursor.getString(0)));
                artist.setName(cursor.getString(1));
                artist.setDescription(cursor.getString(2));
                artist.setDay(cursor.getString(3));
                String startTimeString = cursor.getString(4);
                String endTimeString = cursor.getString(5);
                DateFormat iso8601Format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                try {
                    artist.setStartTime(iso8601Format.parse(startTimeString));
                } catch (Exception e) {
                    Log.e(TAG, "Parsing ISO8601 starttime failed", e);
                }
                try {
                    artist.setEndTime(iso8601Format.parse(endTimeString));
                } catch (Exception e) {
                    Log.e(TAG, "Parsing ISO8601 endtime failed", e);
                }
                artist.setLocation(cursor.getString(6));
                artist.setYoutubeLink(cursor.getString(7));
                artist.setFavorite(cursor.getInt(8)==1);


                byte[] blob = cursor.getBlob(9);
                artist.setThumbnailImageBlob(blob);
                byte[] blob2 = cursor.getBlob(10);
                artist.setLargeImageBlob(blob2);
                String updateAtString = cursor.getString(11);

                try {
                    artist.setUpdatedAt(iso8601Format.parse(updateAtString));
                } catch (Exception e) {
                    Log.e(TAG, "Parsing ISO8601 updatedAt failed", e);
                }

                artists.add(artist);
            } while (cursor.moveToNext());
        }

        Log.d("getAllArtistsFromDB()", "loaded");
        cursor.close();


        return artists;
    }

    public ArrayList<News> getAllNewsFromDB() {
        ArrayList<News> newsArrayList = new ArrayList<>();

        String query = "SELECT * FROM news ORDER by id DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        News news;
        if(cursor.moveToFirst()) {
            do {
                news = new News();
                news.setId(Integer.parseInt(cursor.getString(0)));
                news.setTitle(cursor.getString(1));

                String datePublish = cursor.getString(2);
                DateFormat iso8601Format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                try {
                    news.setDatePublish(iso8601Format.parse(datePublish));
                } catch (Exception e) {
                    Log.e(TAG, "Parsing ISO8601 starttime failed", e);
                }
                news.setTeaser(cursor.getString(3));
                news.setBody(cursor.getString(4));

                byte[] blob = cursor.getBlob(5);
                news.setImageBlob(blob);
                byte[] blob2 = cursor.getBlob(6);
                news.setLargeImageBlob(blob2);

                news.setVideo(cursor.getString(7));

                newsArrayList.add(news);
            } while (cursor.moveToNext());
        }
        cursor.close();

        Log.d("getAllNewssFromDB()", "loaded");

        return newsArrayList;
    }

    public News getNewsFromDB(int id) {

        News news = null;

        String query = "SELECT * FROM " + "news WHERE id='"+ id + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()) {
            do {
                news = new News();
                news.setId(Integer.parseInt(cursor.getString(0)));
                news.setTitle(cursor.getString(1));

                String datePublish = cursor.getString(2);
                DateFormat iso8601Format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                try {
                    news.setDatePublish(iso8601Format.parse(datePublish));
                } catch (Exception e) {
                    Log.e(TAG, "Parsing ISO8601 starttime failed", e);
                }
                news.setTeaser(cursor.getString(3));
                news.setBody(cursor.getString(4));

                byte[] blob = cursor.getBlob(5);
                news.setImageBlob(blob);
                byte[] blob2 = cursor.getBlob(6);
                news.setLargeImageBlob(blob2);

                news.setVideo(cursor.getString(7));

            } while (cursor.moveToNext());
        }
        cursor.close();

        return news;
    }

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }



    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.

}
