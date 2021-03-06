package com.example.jinyoon.a01sunshine;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.jinyoon.a01sunshine.data.WeatherContract;
//import com.example.jinyoon.a01sunshine.service.SunShineService;
import com.example.jinyoon.a01sunshine.sync.SunshineSyncAdapter;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    ForecastAdapter mforecastAdapter;
    private static final int MY_LOADER_ID = 0;
    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY="selected_position";
    private ListView mListView;
    private boolean mUseTodayLayout;
    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;

    public ForecastFragment() {
    }


    //Fragment to handle menu events
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    //inflate the menu... should enable setHasOptionsMenu first
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.forecast_fragment, menu);
    }

    //select which action should be performed once the menu is clicked
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
//           case R.id.action_refresh:
//               updateWeather();
//
//               break;
            case R.id.action_location:
                showMap();
                break;

        }
        return true;
    }

    private void showMap(){
//        SharedPreferences spr = PreferenceManager.getDefaultSharedPreferences(this);
//        String location = spr.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        Cursor c = mforecastAdapter.getCursor();
        c.moveToPosition(0);
        String corLat = c.getString(COL_COORD_LAT);
        String corLon = c.getString(COL_COORD_LONG);
//        String location = Utility.getPreferredLocation(this);
        Uri geoLocation = Uri.parse("geo:"+corLat+","+corLon);
        Intent locationIntent = new Intent(Intent.ACTION_VIEW);
        locationIntent.setData(geoLocation);
        startActivity(locationIntent);


    }


    public void updateWeather(){
//        Intent alarmIntent = new Intent(getActivity(), SunShineService.AlarmReceiver.class);
//        alarmIntent.putExtra(SunShineService.LOCATION_QUERY_EXTRA, Utility.getPreferredLocation(getActivity()));
//        //getActivity().startService(intent);
//        PendingIntent pi = PendingIntent.getBroadcast(getActivity(),0,alarmIntent,PendingIntent.FLAG_ONE_SHOT);
//
//        AlarmManager arm = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
//
//        arm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+10000, pi);
        SunshineSyncAdapter.syncImmediately(getActivity());

    }

    public void onLocationChanged(){
        updateWeather();
        getLoaderManager().restartLoader(MY_LOADER_ID, null, this);
    }

//    @Override
//    public void onStart(){
//        super.onStart();
//        updateWeather();
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Need to "inflate" the view to render : XML layout -> Java View Object
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        //updateWeather();
        //Initialize Adapter
//        String locationSetting = Utility.getPreferredLocation(getActivity());
//        String sortOrder= WeatherContract.WeatherEntry.COLUMN_DATE +" ASC";
//        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
//                locationSetting, System.currentTimeMillis());
//        Cursor cur = getActivity().getContentResolver().query(
//                weatherForLocationUri,
//                FORECAST_COLUMNS,
//                null,
//                null,
//                sortOrder
//        );

        mforecastAdapter=new ForecastAdapter(getActivity(),null,0);


        //find list view
        mListView = (ListView) rootView.findViewById(R.id.listview_forecast);
        //set adapter to list view
        mListView.setAdapter(mforecastAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if(cursor!=null){
                    String locationSetting = Utility.getPreferredLocation(getActivity());

                    ((Callback)getActivity())
                            .onItemSelected(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                                    locationSetting,cursor.getLong(COL_WEATHER_DATE)
                            ));
                    mPosition=position;
                }
            }
        });

        if(savedInstanceState!=null&&savedInstanceState.containsKey(SELECTED_KEY)){
            mPosition=savedInstanceState.getInt(SELECTED_KEY);
        }

        mforecastAdapter.setUseTodayLayout(mUseTodayLayout);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mPosition!=ListView.INVALID_POSITION){
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MY_LOADER_ID,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle){
        String locationSetting = Utility.getPreferredLocation(getActivity());
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE+" ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting,System.currentTimeMillis());

        if(id==MY_LOADER_ID){
            return new CursorLoader(
                    getActivity(),
                    weatherForLocationUri,
                    FORECAST_COLUMNS,
                    null,
                    null,
                    sortOrder);
        }else{
            return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mforecastAdapter.swapCursor(cursor);
        if(mPosition!=ListView.INVALID_POSITION){
            mListView.smoothScrollToPosition(mPosition);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mforecastAdapter.swapCursor(null);
    }

    public void setUseTodayLayout(boolean useTodayLayout){
        mUseTodayLayout=useTodayLayout;
        if(mforecastAdapter!=null){
            mforecastAdapter.setUseTodayLayout(mUseTodayLayout);
        }
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }


}
