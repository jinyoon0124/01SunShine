package com.example.jinyoon.a01sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_detail, menu);
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
        String detailForecastStr = intent.getStringExtra(Intent.EXTRA_TEXT);
        detailForecast.setText(detailForecastStr);

        return rootView;
    }
}
