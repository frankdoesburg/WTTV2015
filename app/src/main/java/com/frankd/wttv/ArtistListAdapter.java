package com.frankd.wttv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class ArtistListAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList<Artist> artists;
    private ArrayList<Artist> filteredData;
    private ItemFilter mFilter = new ItemFilter();
    private ImageLoader imageLoader = ImageLoader.getInstance();

    public ArtistListAdapter(Context context, ArrayList<Artist> artists){
        super(context,R.layout.artist_grid_item,artists);
        this.context = context;
        this.artists = artists;
        filteredData = artists;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.artist_grid_item, parent, false);
        }
        //get artist info
        Artist curArtist = filteredData.get(position);

        //get screen width
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;

        //set imageview and scale the image making the width 50% of the screen size
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);

//        imageView.setImageBitmap(getImageFromBase64Blob(curArtist.getThumbnailImageBlob()));
        RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(width/2,width/2);
        imageView.setLayoutParams(parms);
        imageLoader.displayImage("db://" + curArtist.getId(), imageView);

        TextView artistNameTV = (TextView) convertView.findViewById(R.id.artistNameTV);
        TextView timeDayTV = (TextView) convertView.findViewById(R.id.timeDayTV);

        artistNameTV.setText(curArtist.getName());
        SimpleDateFormat time = new SimpleDateFormat("HH:mm");
        String startTime = "";
        if(curArtist.getStartTime() != null) {
            startTime = time.format(curArtist.getStartTime());
        }
        String location = " @ ";
        if(curArtist.getLocation() != "") {
            location = location.concat(curArtist.getLocation());

        }
        timeDayTV.setText(curArtist.getDay() + " " + startTime + location);

        convertView.setId(curArtist.getId());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ID = v.getId();
                Intent intent = new Intent(context.getApplicationContext(),ArtistDetailActivity.class);
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

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<Artist> list = artists;

            if(filterString == "") {

                results.values = list;
                results.count = list.size();
                return results;
            }

            int count = list.size();
            final ArrayList<Artist> nlist = new ArrayList<>();

            String filterableString ;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).getDay();
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(list.get(i));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<Artist>) results.values;
            notifyDataSetChanged();
        }


    }
}
