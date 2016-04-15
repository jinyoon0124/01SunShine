package com.example.jinyoon.a01sunshine;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

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
            case R.id.action_refresh:
                //Toast.makeText(this.getContext(), "Refresh!!", Toast.LENGTH_SHORT).show();
                FetchWeatherTask fetchWeatherTask=new FetchWeatherTask();
                fetchWeatherTask.execute("94043");
                break;
            case R.id.action_date:
                Calendar calendar = new GregorianCalendar(2016,Calendar.APRIL,15);
                int result = calendar.get(Calendar.DAY_OF_WEEK);
                Toast.makeText(this.getContext(), String.valueOf(result), Toast.LENGTH_SHORT).show();
                break;

        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Need to "inflate" the view to render : XML layout -> Java View Object
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        String[] forecastArray={
                "Today-Sunny-88/63",
                "Today-Sunny-88/63",
                "Today-Sunny-88/63",
                "Today-Sunny-88/63",
                "Today-Sunny-88/63",
        };

        //set string arrays to list
        List<String> weekForecast = new ArrayList<String>(Arrays.asList(forecastArray));

        //Initialize Adapter
        ArrayAdapter<String> forecastAdapter=
                new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.list_item_forecast,
                        R.id.list_item_forecast_textview,
                        weekForecast);

        //find list view
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);


        //set adapter to list view
        listView.setAdapter(forecastAdapter);




        return rootView;
    }

    public class FetchWeatherTask extends AsyncTask<String,Void,String>{

        //gets class name so that it can be seen on error log
        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();



        @Override
        protected String doInBackground(String... params) {

            if(params.length==0){
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String forecastJsonStr=null;

            String mode="json";
            String units="metric";
            int numDays=7;

            try {

                final String FORECAST_BASE_URL=
                        "http://api.openweathermap.org/data/2.5/forecast/daily";
                final String QUERY_PARAM= "zip";
                final String FORMAT_PARAM="mode";
                final String UNITS_PARAM="units";
                final String DAYS_PARAM="cnt";
                final String APPID_PARAM="APPID";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM,params[0])
                        .appendQueryParameter(FORMAT_PARAM,mode)
                        .appendQueryParameter(UNITS_PARAM,units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .appendQueryParameter(APPID_PARAM,BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                        .build();  //API is stored in gradle.properties and defined in build.gradle

                URL url = new URL(builtUri.toString());

                urlConnection=(HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if(inputStream==null){
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line=reader.readLine())!=null){
                    buffer.append(line+"\n");
                }

                if(buffer.length()==0){
                    return null;
                }
                forecastJsonStr=buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error",e);
                return null;
            }finally {
                if(urlConnection!=null){
                    urlConnection.disconnect();
                }
                if(reader!=null){
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "error closing stream",e);
                    }

                }
            }


            Log.v("RETURNED STRING",forecastJsonStr);
            return null;
        }
    }

    private String formatHighLows(double high, double low){
        long roundedHigh=Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh+"/"+roundedLow;
        return highLowStr;
    }

    private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays) throws JSONException {

        final String OWM_LIST="list";
        final String OWM_WEATHER="weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX="max";
        final String OWN_MIN="min";
        final String OWM_DESCRIPTION="main";

        JSONObject forecastJson= new JSONObject(forecastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);


        return null;
    }

}
