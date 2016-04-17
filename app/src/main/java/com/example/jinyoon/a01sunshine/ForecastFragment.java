package com.example.jinyoon.a01sunshine;

import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {
    ArrayAdapter<String> mforecastAdapter;

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
//            case R.id.action_refresh:
//                //Toast.makeText(this.getContext(), "Refresh!!", Toast.LENGTH_SHORT).show();
//
//
//                break;
//            case R.id.action_date:
//                Calendar calendar = new GregorianCalendar();
//                //long result = System.currentTimeMillis();
//                calendar.add(GregorianCalendar.DATE,1);
//                Date result_1 = calendar.getTime();
//                Toast.makeText(this.getContext(), getReadableDateString(result_1), Toast.LENGTH_SHORT).show();
//                break;
//
            case R.id.action_settings:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);

        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Need to "inflate" the view to render : XML layout -> Java View Object
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        FetchWeatherTask fetchWeatherTask=new FetchWeatherTask();
        fetchWeatherTask.execute("94043");


        String[] forecastArray={};

        //set string arrays to list
        List<String> weekForecast = new ArrayList<>(Arrays.asList(forecastArray));

        //Initialize Adapter
        mforecastAdapter=
                new ArrayAdapter<>(
                        getActivity(),
                        R.layout.list_item_forecast,
                        R.id.list_item_forecast_textview,
                        weekForecast);

        //find list view
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        //set adapter to list view
        listView.setAdapter(mforecastAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String forecast = parent.getItemAtPosition(position).toString();
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(intent);

            }
        });





        return rootView;
    }

    public class FetchWeatherTask extends AsyncTask<String,Void,String[]>{

        //gets class name so that it can be seen on error log
        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        private String getReadableDateString(Date time){
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE, MMM dd");
            return shortenedDateFormat.format(time);
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

            Calendar calendar = new GregorianCalendar();

            String[] resultStrs = new String[numDays];
            for(int i = 0; i<weatherArray.length(); i++){
                String day;
                String description;
                String highAndLow;

                JSONObject dayForecast = weatherArray.getJSONObject(i);


                Date result_1 = calendar.getTime();
                day= getReadableDateString(result_1);
                calendar.add(GregorianCalendar.DATE,1);
                //Log.v("!!!DAY TEST: ",day );

                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                //Log.v("!!! WEATHER OBJECT : ", weatherObject.toString());
                description = weatherObject.getString(OWM_DESCRIPTION);
                //Log.v("!!!Description Test:",description);


                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWN_MIN);

                highAndLow=formatHighLows(high, low);
                resultStrs[i]=day+" - "+description+" - "+highAndLow;

            }

            return resultStrs;
        }


        @Override
        protected String[] doInBackground(String... params) {

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
                final String QUERY_PARAM= "q";
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


//            Log.v("RETURNED STRING",forecastJsonStr);
            try {
                return getWeatherDataFromJson(forecastJsonStr, numDays);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if(result != null){
                mforecastAdapter.clear();
                mforecastAdapter.addAll(result);

            }



        }


    }


}
