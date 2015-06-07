package com.frankd.wttv;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by FrankD on 5-6-2015.
 */
public class ArtistListActivity extends Activity {


    @Override
       protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artist_list_layout);

        //allow backward navigation to parent activity via actionbar
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //set custom font to action bar
        SpannableString s = new SpannableString(getString(R.string.acts));
        s.setSpan(new TypefaceSpan(this, "big_noodle_titling.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(s);

        GridView artistGrid = (GridView) findViewById(R.id.gridView);
        ArtistListAdapter adapter = new ArtistListAdapter(this,getArtistList());
        artistGrid.setAdapter(adapter);

    }

    public ArrayList<Artist> getArtistList(){
        ArrayList<Artist> artists = new ArrayList<Artist>();

        for(int i = 0; i < 40; i++){
           if(i%4 == 0){
                artists.add(new Artist(i,"DEUS", "beschrijving hier", Day.FRIDAY, Stage.STAGE1, "14:30",R.mipmap.deus_vierkant));
            }else if(i%3==0){
               artists.add(new Artist(i,"TYPHOON", "beschrijving hier", Day.FRIDAY, Stage.STAGE1, "14:30",R.mipmap.typhoon_vierkant));
           }else if(i%2==0){
               artists.add(new Artist(i,"DEWOLFF", "beschrijving hier", Day.FRIDAY, Stage.STAGE1, "14:30",R.mipmap.dewolff_vierkant));
           }else{
               artists.add(new Artist(i,"GOD DAMN", "beschrijving hier", Day.FRIDAY, Stage.STAGE1, "14:30",R.mipmap.goddamn_vierkant));
           }


        }
        DataStore.artists = artists;
        return artists;
    }


}
