package com.example.jinyoon.a01sunshine;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.jinyoon.a01sunshine.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {
    ForecastAdapter mforecastAdapter;

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
               updateWeather();

               break;
//            case R.id.action_date:
//                Calendar calendar = new GregorianCalendar();
//                //long result = System.currentTimeMillis();
//                calendar.add(GregorianCalendar.DATE,1);
//                Date result_1 = calendar.getTime();
//                Toast.makeText(this.getContext(), getReadableDateString(result_1), Toast.LENGTH_SHORT).show();
//               break;
//

//            case R.id.action_location:
//                SharedPreferences spr = PreferenceManager.getDefaultSharedPreferences(getActivity());
//                String location = spr.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
//                String locationUri = "geo:0,0?q="+location;
//                showMap(Uri.parse(locationUri));
//
//                break;
//            case R.id.action_settings:
//                Intent intent = new Intent(getActivity(), SettingsActivity.class);
//                startActivity(intent);
//                break;
        }
        return true;
    }




    public void updateWeather(){
        FetchWeatherTask fetchWeatherTask = new FetchWeatherTask(this.getContext());
        SharedPreferences spr = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = spr.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        //Toast.makeText(this.getContext(), units, Toast.LENGTH_SHORT).show();
        fetchWeatherTask.execute(location);
    }

    @Override
    public void onStart(){
        super.onStart();
        updateWeather();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Need to "inflate" the view to render : XML layout -> Java View Object
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        updateWeather();
        //Initialize Adapter
        String locationSetting = Utility.getPreferredLocation(getActivity());
        String sortOrder= WeatherContract.WeatherEntry.COLUMN_DATE +" ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());
        Cursor cur = getActivity().getContentResolver().query(
                weatherForLocationUri,
                null,
                null,
                null,
                sortOrder
        );

//
//        mforecastAdapter=
//                new ArrayAdapter<>(
//                        getActivity(),
//                        R.layout.list_item_forecast,
//                        R.id.list_item_forecast_textview,
//                        new ArrayList<String>());

        mforecastAdapter=new ForecastAdapter(getActivity(),cur,0);


        //find list view
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        //set adapter to list view
        listView.setAdapter(mforecastAdapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String forecast = parent.getItemAtPosition(position).toString();
//                Intent intent = new Intent(getActivity(), DetailActivity.class);
//                intent.putExtra(Intent.EXTRA_TEXT, forecast);
//                startActivity(intent);
//
//            }
//        });





        return rootView;
    }


}
