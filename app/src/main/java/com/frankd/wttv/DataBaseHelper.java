package com.frankd.wttv;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
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
                artist.setID(Integer.parseInt(cursor.getString(0)));
                artist.setName(cursor.getString(1));
                artist.setDescription(cursor.getString(2));
                artist.setDay(cursor.getString(3));
                artist.setStartTime(cursor.getString(4));
                artist.setEndTime(cursor.getString(5));
                artist.setLocation(cursor.getString(6));
                artist.setYoutubeLink(cursor.getString(7));
                artist.setFavorite(cursor.getInt(8) == 1);
                //TODO getImage returns null
                // error description: 06-20 16:21:06.990  11460-11460/com.frankd.wttv D/skia? --- SkImageDecoder::Factory returned null
                byte[] byteIMG = cursor.getBlob(9);
                artist.setThumbnailImage(getImageFromBlob(byteIMG));
                byte[] byteIMG2 = cursor.getBlob(10);
                artist.setLargeImage(getImageFromBlob(byteIMG2));


          } while (cursor.moveToNext());
        }


        return artist;
    }

    public static Bitmap getImageFromBlob(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);


    }

    public ArrayList<Artist> getAllArtistsFromDB() {
        ArrayList<Artist> artists = new ArrayList<Artist>();

        String query = "SELECT * FROM " + "acts";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);


        Artist artist = null;
        if(cursor.moveToFirst()) {
            do {
                artist = new Artist();
                artist.setID(Integer.parseInt(cursor.getString(0)));
                artist.setName(cursor.getString(1));
                artist.setDescription(cursor.getString(2));
                artist.setDay(cursor.getString(3));
                artist.setStartTime(cursor.getString(4));
                artist.setEndTime(cursor.getString(5));
                artist.setLocation(cursor.getString(6));
                artist.setYoutubeLink(cursor.getString(7));
                artist.setFavorite(cursor.getInt(8)==1);

                artists.add(artist);
            } while (cursor.moveToNext());
        }

        Log.d("getAllArtistsFromDB()", "loaded");

        return artists;
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
