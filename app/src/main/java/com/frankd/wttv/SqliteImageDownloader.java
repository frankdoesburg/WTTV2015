package com.frankd.wttv;

import android.content.Context;
import android.util.Base64;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Tom on 2/7/15.
 */
public class SqliteImageDownloader extends BaseImageDownloader {

    private static final String SCHEME_DB = "db";
    private static final String DB_URI_PREFIX = SCHEME_DB + "://";
    private DataBaseHelper myDbHelper;

    public SqliteImageDownloader(Context context, DataBaseHelper myDbHelper) {
        super(context);
        this.myDbHelper = myDbHelper;

    }

    @Override
    protected InputStream getStreamFromOtherSource(String imageUri, Object extra) throws IOException {
        if (imageUri.startsWith(DB_URI_PREFIX)) {
            String path = imageUri.substring(DB_URI_PREFIX.length());
            Artist artist = myDbHelper.getArtistFromDB(Integer.parseInt(path));

            byte[] imageData = Base64.decode(artist.getThumbnailImageBlob(), Base64.DEFAULT);

            return new ByteArrayInputStream(imageData);
        } else {
            return super.getStreamFromOtherSource(imageUri, extra);
        }
    }
}
