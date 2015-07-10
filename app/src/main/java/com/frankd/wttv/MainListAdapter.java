package com.frankd.wttv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Tom on 28/6/15.
 */
public class MainListAdapter extends ArrayAdapter {
    private final String TAG = "MainListAdapter";


    private Context context;
    private ArrayList<News> news;

    public MainListAdapter(Context context, ArrayList<News> newsArrayList) {
        super(context,R.layout.news_item,newsArrayList);
        this.context = context;
        news = newsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.news_item, parent, false);
        }
        //get artist info
        News curNews = news.get(position);

        //get screen width
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        //set imageview and scale the image making the width 50% of the screen size
        ImageView imageView = (ImageView) convertView.findViewById(R.id.newsCellImageView);

        try {
            imageView.setImageBitmap(getImageFromBase64Blob(curNews.getImageBlob()));
        }catch (NullPointerException ex){
            Log.v(TAG,ex.toString());
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.wttv_logo));
        }

        TextView title = (TextView) convertView.findViewById(R.id.newsTitle);
        TextView teaser = (TextView) convertView.findViewById(R.id.newsTeaser);

        title.setText(curNews.getTitle());
        teaser.setText(curNews.getTeaser());

        convertView.setId(curNews.getId());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ID = v.getId();
                Intent intent = new Intent(context.getApplicationContext(),NewsDetailActivity.class);
                intent.putExtra("ID",ID);
                context.startActivity(intent);
            }
        });

        return convertView;
    }


    public Bitmap getImageFromBase64Blob(byte[] blob){
        byte[] decodedString = Base64.decode(blob, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    public void refresh(ArrayList<News> newNews){
        news.clear();
        news.addAll(newNews);
        notifyDataSetChanged();
    }

}
