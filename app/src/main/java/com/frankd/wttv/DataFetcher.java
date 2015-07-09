package com.frankd.wttv;

import android.content.Context;
import android.graphics.Bitmap;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Tom on 26/6/15.
 */
public class DataFetcher {

    public void getDataFromServer(final Context context, final DataBaseHelper myDbHelper, final ArrayList<Artist> artists, final MainApplication mainApplication) {
        String url = "http://welcometothevillage.nl/json/acts";
        mainApplication.addPendingRequest();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<Artist> artistArrayList = artists;
                        try {
                            JSONArray data = response.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    Artist artist = new Artist();
                                    JSONObject obj = data.getJSONObject(i);
                                    artist.setId(obj.optInt("id"));

                                    if (getArtist(artistArrayList, artist.getId()) != null) {
                                        artist = getArtist(artistArrayList, artist.getId());
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

                                    if (artist.getUpdatedAt() == null || !artist.getUpdatedAt().equals(updatedAt)) {
                                        artist.setUpdatedAt(updatedAt);


                                        artist.setName(attributes.optString("title"));

                                        try {
                                            String date = attributes.optString("datetime");
                                            artist.setStartTime(jsonDateFormat.parse(date));

                                        } catch (Exception e) {
                                            artist.setStartTime(null);
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
                                            artist.setLocation("");
                                        }

                                        artist.setDescription(removeHtmlFromString(attributes.optString("body")));

                                        try {
                                            String imageUrl = attributes.optJSONObject("image").optString("thumbnail");
                                            saveArtistUrlBlobs(imageUrl, context, artist, myDbHelper, mainApplication);
                                        } catch (Exception e) {
                                            System.out.println("failed!");
                                        }

                                        System.out.println("Parsed: " + artist.getName());
                                        myDbHelper.insertArtist(artist);
                                        artistArrayList = updateArtist(artist, artistArrayList);
                                    }

                                } catch (JSONException e) {
                                    Log.d("JSON", "Could not fetch JSON element 'object'");
                                    e.printStackTrace();

                                }
                            }
                            // return artists
                            mainApplication.setArtistList(artists);
                            mainApplication.removePendingRequest();


                        } catch (JSONException e) {
                            Log.d("JSON", "Could not fetch JSON element 'data'");
                            e.printStackTrace();
                            mainApplication.removePendingRequest();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        mainApplication.removePendingRequest();


                    }
                });

        MySingleton.getInstance(context).addToRequestQueue(jsObjRequest);
    }

    public void getNewsFromServer(final Context context, final DataBaseHelper myDbHelper, final ArrayList<News> newsArrayList, final MainApplication mainApplication) {
        String url = "http://welcometothevillage.nl/json/nieuws";
        mainApplication.addPendingRequest();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray data = response.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    final News news = new News();
                                    JSONObject obj = data.getJSONObject(i);
                                    news.setId(obj.optInt("id"));

                                    JSONObject attributes = obj.getJSONObject("attributes");
                                    news.setTitle(attributes.optString("title"));

                                    DateFormat jsonDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                    Date datePublish;

                                    try {
                                        String date = attributes.optString("datepublish");
                                        datePublish = jsonDateFormat.parse(date);
                                        news.setDatePublish(datePublish);
                                        Calendar datePublishCalendar = Calendar.getInstance();
                                        datePublishCalendar.setTime(datePublish);
                                        Calendar now = Calendar.getInstance();
                                        now.setTime(new Date());
                                        if (datePublishCalendar.get(Calendar.YEAR) == now.get(Calendar.YEAR)) { // only add news items from this year
                                            if (getNews(newsArrayList, news.getId()) == null) { // only get new newsItems

                                                if (obj.optJSONObject("links") != null) {
                                                    String newsItemUrl = obj.optJSONObject("links").optString("self");
                                                    if (newsItemUrl != null) {
                                                        fetchNewsItem(news, newsItemUrl, myDbHelper, context, mainApplication);
                                                    }
                                                }
                                            }
                                        }
                                    } catch (ParseException e) {

                                    }

                                } catch (JSONException e) {
                                    Log.d("JSON", "Could not fetch JSON element 'object'");
                                    e.printStackTrace();
                                }
                            }

                            mainApplication.removePendingRequest();

                        } catch (JSONException e) {
                            Log.d("JSON", "Could not fetch JSON element 'data'");
                            e.printStackTrace();
                            mainApplication.removePendingRequest();

                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        mainApplication.removePendingRequest();

                    }
                });

        MySingleton.getInstance(context).addToRequestQueue(jsObjRequest);
    }

    private void fetchNewsItem(final News news, String newsItemUrl, final DataBaseHelper myDbHelper, final Context context, final MainApplication mainApplication) {
        mainApplication.addPendingRequest();
        JsonObjectRequest jsNewsItemObjectRequest = new JsonObjectRequest
                (Request.Method.GET, newsItemUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONObject data = response.getJSONObject("data");
                            JSONObject attributes = data.optJSONObject("attributes");

                            news.setTeaser(removeHtmlFromString(attributes.optString("teaser")));
                            news.setBody(removeHtmlFromString(attributes.optString("body")));
                            if(attributes.optJSONObject("video") != null) {
                                news.setVideo(attributes.optJSONObject("video").optString("url"));
                            }

                            String url = attributes.optJSONObject("image").optString("thumbnail");

                            saveNewsUrlBlobs(url, context, news, myDbHelper, mainApplication);
                            System.out.println("Saved: " + news.getTitle());
                            myDbHelper.insertNews(news);
                            ArrayList<News> newsArrayList = updateNews(news, mainApplication.getNewsList());
                            mainApplication.setNewsList(newsArrayList);
                            mainApplication.removePendingRequest();

                        } catch (JSONException e) {
                            Log.d("JSON", "Could not fetch JSON element 'data'");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mainApplication.removePendingRequest();

                    }
                });
        MySingleton.getInstance(context).addToRequestQueue(jsNewsItemObjectRequest);
    }

    private String removeHtmlFromString(String htmlString) {
        htmlString = htmlString.replaceAll("<[^>]+>", "");
        htmlString = htmlString.replaceAll("&amp;", "&");
        htmlString = htmlString.replaceAll("&nbsp;", " ");
        return htmlString;
    }

    private void saveArtistUrlBlobs(String url, Context context, final Artist artist, final DataBaseHelper myDbHelper, final MainApplication mainApplication) {
        String urlString = url.replaceAll("/[\\d]{3}x[\\d]{3}", "/" + "240x240");
        String urlStringLarge = url.replaceAll("/[\\d]{3}x[\\d]{3}", "/" + "600x400");
        urlString = urlString.replaceAll("\\s", "%20");
        urlStringLarge = urlStringLarge.replaceAll("\\s", "%20");
        mainApplication.addPendingRequest();
        ImageRequest request = new ImageRequest(urlString,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        byte[] base64Image = getBase64Bytes(bitmap);
                        artist.setThumbnailImageBlob(base64Image);
                        myDbHelper.insertArtist(artist);
                        ArrayList<Artist> artists = updateArtist(artist, mainApplication.getArtists());
                        mainApplication.setArtistList(artists);
                        System.out.println("saved artist image!!");
                        mainApplication.removePendingRequest();



                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        mainApplication.removePendingRequest();

                    }
                });
        MySingleton.getInstance(context).addToRequestQueue(request);

        mainApplication.addPendingRequest();
        request = new ImageRequest(urlStringLarge,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        byte[] base64Image = getBase64Bytes(bitmap);
                        artist.setLargeImageBlob(base64Image);
                        myDbHelper.insertArtist(artist);
                        ArrayList<Artist> artists = updateArtist(artist, mainApplication.getArtists());
                        mainApplication.setArtistList(artists);
                        System.out.println("saved large artist image!!!!");
                        mainApplication.removePendingRequest();


                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        mainApplication.removePendingRequest();

                    }
                });
        MySingleton.getInstance(context).addToRequestQueue(request);

    }

    private void saveNewsUrlBlobs(String url, Context context, final News news, final DataBaseHelper myDbHelper, final MainApplication mainApplication) {
        String urlString = url.replaceAll("/[\\d]{3}x[\\d]{3}", "/" + "240x240");
        String urlStringLarge = url.replaceAll("/[\\d]{3}x[\\d]{3}", "/" + "600x400");
        urlString = urlString.replaceAll("\\s", "%20");
        urlStringLarge = urlStringLarge.replaceAll("\\s", "%20");

        mainApplication.addPendingRequest();
        ImageRequest request = new ImageRequest(urlString,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        byte[] base64Image = getBase64Bytes(bitmap);
                        news.setImageBlob(base64Image);
                        myDbHelper.insertNews(news);
                        ArrayList<News> newsArrayList = updateNews(news, mainApplication.getNewsList());
                        mainApplication.setNewsList(newsArrayList);
                        System.out.println("saved news image!!");
                        mainApplication.removePendingRequest();


                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        mainApplication.removePendingRequest();

                    }
                });
        MySingleton.getInstance(context).addToRequestQueue(request);

        mainApplication.addPendingRequest();
        request = new ImageRequest(urlStringLarge,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        byte[] base64Image = getBase64Bytes(bitmap);
                        news.setLargeImageBlob(base64Image);
                        myDbHelper.insertNews(news);
                        ArrayList<News> newsArrayList = updateNews(news, mainApplication.getNewsList());
                        mainApplication.setNewsList(newsArrayList);
                        System.out.println("saved large news image!!!!");
                        mainApplication.removePendingRequest();


                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        mainApplication.removePendingRequest();

                    }
                });
        MySingleton.getInstance(context).addToRequestQueue(request);

    }

    private byte[] getBase64Bytes(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return Base64.encode(byteArray, Base64.DEFAULT);
    }


    private Artist getArtist(ArrayList<Artist> acts, int id) {
        for (Artist artist : acts) {
            if (artist.getId() == id) {
                return artist;
            }
        }
        return null;
    }

    private ArrayList<Artist> updateArtist(Artist updatedArtist, ArrayList<Artist> artists) {
        for (int i = 0; i < artists.size(); i++) {
            if (artists.get(i).getId() == updatedArtist.getId()) {
                artists.remove(i);
                artists.add(updatedArtist);
            }
        }
        Collections.sort(artists, new Comparator<Artist>() {
            @Override
            public int compare(Artist artist1, Artist artist2) {

                return artist1.getName().compareTo(artist2.getName());
            }
        });
        return artists;

    }

    private News getNews(ArrayList<News> newsArrayList, int id) {
        for (News news : newsArrayList) {
            if (news.getId() == id) {
                return news;
            }
        }
        return null;
    }

    private ArrayList<News> updateNews(News updatedNews, ArrayList<News> newsList) {
        for (int i = 0; i < newsList.size(); i++) {
            if (newsList.get(i).getId() == updatedNews.getId()) {
                newsList.remove(i);
            }
        }
        newsList.add(updatedNews);

        Collections.sort(newsList, new Comparator<News>() {
            @Override
            public int compare(News news1, News news2) {

                return news1.getDatePublish().compareTo(news2.getDatePublish());
            }
        });
        return newsList;

    }


}


