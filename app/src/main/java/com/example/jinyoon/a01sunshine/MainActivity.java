package com.example.jinyoon.a01sunshine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.jinyoon.a01sunshine.sync.SunshineSyncAdapter;

public class MainActivity extends AppCompatActivity implements ForecastFragment.Callback{
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private static String mLocation;
    private static boolean mTwoPane;
    private static final String PANE_KEY = "pane_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLocation= Utility.getPreferredLocation(this);
        if (findViewById(R.id.weather_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailActivityFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
        ForecastFragment forecastFragment=(ForecastFragment)getSupportFragmentManager().
                findFragmentById(R.id.forecast_fragment);
        forecastFragment.setUseTodayLayout(!mTwoPane);

        SunshineSyncAdapter.initializeSyncAdapter(this);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_location:
                showMap();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showMap(){
//        SharedPreferences spr = PreferenceManager.getDefaultSharedPreferences(this);
//        String location = spr.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        String location = Utility.getPreferredLocation(this);
        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location)
                .build();
        Intent locationIntent = new Intent(Intent.ACTION_VIEW);
        locationIntent.setData(geoLocation);
        startActivity(locationIntent);


    }

    @Override
    protected void onResume() {
        super.onResume();
        String location = Utility.getPreferredLocation(this);
        if(location!=null && !location.equals(mLocation)){
            ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.forecast_fragment);
            if(null!=ff){
                ff.onLocationChanged();
            }
            DetailActivityFragment df = (DetailActivityFragment)getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if(null!=df){
                df.onLocationChanged(location);
            }
            mLocation=location;
        }

    }

    @Override
    public void onItemSelected(Uri dateUri) {
        if(mTwoPane){
            Bundle args = new Bundle();
            args.putParcelable(DetailActivityFragment.DETAIL_URI, dateUri);

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        }else{
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(dateUri);
            startActivity(intent);
        }
    }
}
