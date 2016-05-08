package com.example.jinyoon.a01sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jinyoon.a01sunshine.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private String detailForecastStr;
    private static final String LOG_TAG=DetailActivityFragment.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG=" #SunShineApp";
    private static Uri mUri;
    static final String DETAIL_URI = "URI";

    private static final int DETAIL_LOADER_ID = 0;
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
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_WEATHER_HUMIDITY = 5;
    static final int COL_WEATHER_WIND_SPEED = 6;
    static final int COL_WEATHER_DEGREES = 7;
    static final int COL_WEATHER_PRESSURE = 8;
    static final int COL_WEATHER_CONDITION_ID=9;

    private ImageView mIconImageView;
    private TextView mDayTextView;
    private TextView mDateTextView;
    private TextView mHighTextView;
    private TextView mLowTextView;
    private TextView mDescTextView;
    private TextView mHumidityTextView;
    private TextView mWindTextView;
    private TextView mPressureTextView;

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
        mIconImageView = (ImageView) rootView.findViewById(R.id.detail_icon);
        mDayTextView = (TextView) rootView.findViewById(R.id.detail_day_textview);
        mDateTextView = (TextView) rootView.findViewById(R.id.detail_date_textview);
        mHighTextView = (TextView) rootView.findViewById(R.id.detail_high_textview);
        mLowTextView = (TextView) rootView.findViewById(R.id.detail_low_textview);
        mDescTextView = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
        mHumidityTextView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
        mWindTextView = (TextView) rootView.findViewById(R.id.detail_wind_textview);
        mPressureTextView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);

        Bundle arguments = getArguments();
        if(arguments!=null){
            mUri=arguments.getParcelable(DETAIL_URI);
        }


        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        Intent intent = getActivity().getIntent();
//        if(intent==null || intent.getData()==null){
//            return null;
//        }

        if(mUri!=null){
            return new CursorLoader(
                    this.getContext(),
                    mUri,
                    FORECAST_COLUMNS,
                    null,
                    null,
                    null
            );
        }else{
            return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursor.moveToFirst();

        int weatherId = cursor.getInt(COL_WEATHER_CONDITION_ID);
        mIconImageView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

        String dayString = Utility.getDayName(this.getActivity(), cursor.getLong(COL_WEATHER_DATE));
        mDayTextView.setText(dayString);

        String dateString = Utility.getFormattedMonthDay(this.getActivity(),cursor.getLong(COL_WEATHER_DATE));
        mDateTextView.setText(dateString);

        String maxTempString = Utility.formatTemperature(
                this.getActivity(),cursor.getDouble(COL_WEATHER_MAX_TEMP), Utility.isMetric(getContext()));
        mHighTextView.setText(maxTempString);

        String minTempString = Utility.formatTemperature(
                this.getActivity(),cursor.getDouble(COL_WEATHER_MIN_TEMP), Utility.isMetric(getContext()));
        mLowTextView.setText(minTempString);

        String descString = cursor.getString(COL_WEATHER_DESC);
        mDescTextView.setText(descString);

        mHumidityTextView.setText(
                getString(R.string.format_humidity, cursor.getFloat(COL_WEATHER_HUMIDITY)));

        mWindTextView.setText(Utility.getFormattedWind(
                this.getActivity(), cursor.getFloat(COL_WEATHER_WIND_SPEED), cursor.getFloat(COL_WEATHER_DEGREES)));

        mPressureTextView.setText(
                getString(R.string.format_pressure, cursor.getFloat(COL_WEATHER_PRESSURE)));

        detailForecastStr = String.format("%s - %s - %s/%s",dateString,descString,maxTempString,minTempString);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Nothing to do here because there is no data that we are holdin onto
    }
    void onLocationChanged( String newLocation ) {
        // replace the uri, since the location has changed
        Uri uri = mUri;
        if (null != uri) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            mUri = updatedUri;
            getLoaderManager().restartLoader(DETAIL_LOADER_ID, null, this);
        }
    }

}
