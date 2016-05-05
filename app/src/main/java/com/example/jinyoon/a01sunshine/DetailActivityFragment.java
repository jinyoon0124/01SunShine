package com.example.jinyoon.a01sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    private String detailForecastStr;
    private static final String LOG_TAG=DetailActivityFragment.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG=" #SunShineApp";

    public DetailActivityFragment() {
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.detail_fragment, menu);

        //Share Item!!!!
        MenuItem item =  menu.findItem(R.id.menu_item_share);
        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        if(mShareActionProvider!=null){
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }else{
            Log.d(LOG_TAG, "Share Action Provider is null");
        }

    }

    private Intent createShareForecastIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        //it prevents the activity we are sharing to from being placed onto the activity stack
        //When click on the icon later, we might end up being in different app
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,detailForecastStr+ FORECAST_SHARE_HASHTAG);

        return shareIntent;
    }

    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.action_settings:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);

        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView detailForecast = (TextView) rootView.findViewById(R.id.detailForecast);
        Intent intent = getActivity().getIntent();
        if(intent!=null){
            detailForecastStr = intent.getStringExtra(Intent.EXTRA_TEXT);
            detailForecast.setText(detailForecastStr);
        }else{
            Log.e("NO INENT","SHIT!!!!!!!!");
        }
        return rootView;
    }
}
