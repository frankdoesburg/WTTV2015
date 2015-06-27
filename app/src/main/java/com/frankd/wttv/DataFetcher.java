package com.frankd.wttv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Tom on 26/6/15.
 */
public class DataFetcher {

    public void getDataFromServer(final Context context, final DataBaseHelper myDbHelper) {
        String url = "http://welcometothevillage.nl/json/acts";

        final ArrayList<Artist> artists = myDbHelper.getAllArtistsFromDB();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray data = response.getJSONArray("data");
                            for(int i = 0; i < data.length(); i++) {
                                try {
                                    Artist artist = new Artist();
                                    JSONObject obj = data.getJSONObject(i);
                                    artist.setId(obj.optInt("id"));

                                    if(getArtist(artists, artist.getId()) != null) {
                                        artist = getArtist(artists, artist.getId());
                                    }

                                    JSONObject attributes = obj.getJSONObject("attributes");

                                    DateFormat jsonDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                    Date updatedAt;
                                    try {
                                        String date = attributes.optString("datechanged");
                                        updatedAt = jsonDateFormat.parse(date);
                                    } catch (ParseException e) {
                                        updatedAt = new Date();
                                    }

                                    if(artist.getUpdatedAt() == null || !artist.getUpdatedAt().equals(updatedAt)) {
                                        artist.setUpdatedAt(updatedAt);


                                        artist.setName(attributes.optString("title"));

                                        try {
                                            String date = attributes.optString("datetime_aanvang");
                                            artist.setStartTime(jsonDateFormat.parse(date));

                                        } catch (Exception e) {
                                            artist.setStartTime(new Date());
                                        }

                                        try {

                                            String date = attributes.optString("datetime_eind");
                                            artist.setEndTime(jsonDateFormat.parse(date));

                                        } catch (Exception e) {
                                            artist.setEndTime(new Date());

                                        }

                                        try {
                                            artist.setYoutubeLink(attributes.optJSONObject("video").optString("url"));

                                        } catch (Exception e) {
                                            artist.setYoutubeLink("");
                                        }

                                        try {
                                            JSONObject days = attributes.optJSONObject("taxonomy").optJSONObject("speeldagen");
                                            artist.setDay(days.optString(days.keys().next()));
                                        } catch (Exception e) {
                                            artist.setDay("");
                                        }

                                        try {
                                            JSONObject location = attributes.optJSONObject("taxonomy").optJSONObject("locaties");
                                            artist.setLocation(location.optString(location.keys().next()));
                                        } catch (Exception e) {
                                            artist.setLocation("Podium");
                                        }

                                        artist.setDescription(removeHtmlFromString(attributes.optString("body")));

//                                        var urlString = appDict["attributes"]["image"]["thumbnail"].stringValue
//                                        act.imageBlob = self.getBlobFromUrl(urlString, resolution:"240x240")
//                                        act.largeImageBlob = self.getBlobFromUrl(urlString, resolution: "600x400")

                                        try {
                                            String imageUrl = attributes.optJSONObject("image").optString("thumbnail");
                                            saveUrlBlobs(imageUrl, context, artist, myDbHelper);
                                        } catch (Exception e) {
                                            System.out.println("failed!");
                                        }

                                        System.out.println("Parsed: " + artist.getName());
                                        myDbHelper.insertArtist(artist);
                                    }



                                } catch (JSONException e) {
                                    Log.d("JSON", "Could not fetch JSON element 'object'");
                                    e.printStackTrace();
                                }
                            }


                        } catch (JSONException e) {
                            Log.d("JSON", "Could not fetch JSON element 'data'");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });

        MySingleton.getInstance(context).addToRequestQueue(jsObjRequest);
    }

    private String removeHtmlFromString(String htmlString) {
        htmlString = htmlString.replaceAll("<[^>]+>","");
        htmlString = htmlString.replaceAll("&amp;","&");
        htmlString = htmlString.replaceAll("&nbsp;"," ");
        return htmlString;
    }

    private void saveUrlBlobs(String url, Context context, final Artist artist, final DataBaseHelper myDbHelper) {
        String urlString = url.replaceAll("/[\\d]{3}x[\\d]{3}", "/" + "240x240");
        String urlStringLarge = url.replaceAll("/[\\d]{3}x[\\d]{3}", "/" + "600x400");
        urlString = urlString.replaceAll("\\s", "%20");
        urlStringLarge = urlStringLarge.replaceAll("\\s", "%20");

        ImageRequest request = new ImageRequest(urlString,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream .toByteArray();

                        byte[] base64Image = Base64.encode(byteArray, Base64.DEFAULT);
                        artist.setThumbnailImageBlob(base64Image);
                        myDbHelper.insertArtist(artist);
                        System.out.println("saved!!");


                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        MySingleton.getInstance(context).addToRequestQueue(request);

        request = new ImageRequest(urlStringLarge,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream .toByteArray();

                        byte[] base64Image = Base64.encode(byteArray, Base64.DEFAULT);
                        artist.setLargeImageBlob(base64Image);
                        myDbHelper.insertArtist(artist);
                        System.out.println("saved!!!!");

                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        MySingleton.getInstance(context).addToRequestQueue(request);

    }

    private Artist getArtist(ArrayList<Artist> acts, int id) {
        for (Artist artist : acts) {
            if(artist.getId() == id) {
                return artist;
            }
        }
        return null;
    }


}


