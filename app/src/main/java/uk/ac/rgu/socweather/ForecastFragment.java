package uk.ac.rgu.socweather;

import static java.net.Proxy.Type.HTTP;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import uk.ac.rgu.socweather.data.ForecastRepository;
import uk.ac.rgu.socweather.data.HourForecast;
import uk.ac.rgu.socweather.data.HourForecastDAO;
import uk.ac.rgu.socweather.data.Utils;
import uk.ac.rgu.socweather.data.WeatherApiParser;
import uk.ac.rgu.socweather.data.WeatherDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForecastFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForecastFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ForecastFragment";

    // member variables for the setting up the display
    private String mLocationName;
    private int mNumDays;

    // for database access
    private HourForecastDAO hourForecastDAO;

    // for the data being display
    private List<HourForecast> hourForecasts;

    // the adapter for the RecyclerView
    private HourForecastRecyclerViewAdapter adapter;

    public ForecastFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locationName The name of the location to display the forecast for.
     * @param numDays The number of days to display the forecast for.
     * @return A new instance of fragment ForecastFragment.
     */
    public static ForecastFragment newInstance(String locationName, int numDays) {
        ForecastFragment fragment = new ForecastFragment();
        Bundle args = new Bundle();
        args.putString(LocationConfirmationFragment.ARG_LOCATION_NAME, locationName);
        args.putInt(LocationConfirmationFragment.ARG_NUM_DAYS, numDays);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLocationName = getArguments().getString(LocationConfirmationFragment.ARG_LOCATION_NAME);
            mNumDays = getArguments().getInt(LocationConfirmationFragment.ARG_NUM_DAYS);
        }
        // get the database for storing and loading the forecast
        WeatherDatabase weatherDatabase = WeatherDatabase.getDatabase(getContext());
        // get the DAO for HourForecasts
        hourForecastDAO = weatherDatabase.hourForecastDAO();

        // get the data to display - a placeholder while waiting to download
        hourForecasts = new ArrayList<>();//ForecastRepository.getRepository(getContext()).getHourlyForecasts(mNumDays*24);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forecast, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // update text in the forecast label
        TextView tvForecastLabel = view.findViewById(R.id.tvForecastLabel);
        String label = getContext().getString(R.string.tvForecastLabel, mLocationName);
        tvForecastLabel.setText(label);

        // create the adapter for the RecyclerView
        adapter = new HourForecastRecyclerViewAdapter(getContext(), hourForecasts);

        // get the RecyclerView
        RecyclerView rvForecast = view.findViewById(R.id.rv_forecast);

        // wireup the RecyclerView with the adapter
        rvForecast.setLayoutManager(new LinearLayoutManager(getContext()));
        rvForecast.setAdapter(adapter);

        /****************************************************
         * While this works, having all this code in onViewCreated()
         * is not very maintainable. It would be far better to have
         * it in a Repository, or at least to have it in other methods
         * that are being called from here to make it easier
         * to read and maintain - this below is by no means an example
         * of good coding standards and practices.
         ***************************************************/



        // check for any weather forecasts to retrieve and display from the database
        SimpleDateFormat dateOutFormatter = new SimpleDateFormat(ForecastRepository.DATE_FORMAT);
        Date date = new Date();
        date.setTime(System.currentTimeMillis());
        String dateStr = dateOutFormatter.format(date);

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // get any forecasts on the background thread
                List<HourForecast> cachedForecasts = hourForecastDAO.findByLocationDate(mLocationName, dateStr);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // use these to update the UI on the UI Thread
                        if (cachedForecasts.size() > 0){
                            hourForecasts.clear();
                            hourForecasts.addAll(cachedForecasts);
                            adapter.notifyDataSetChanged();
                        } else {
                            downloadWeatherForecast();
                        }
                    }
                });

            }
        });


        // add action listeners to the three buttons
        Button btnShowMap = view.findViewById(R.id.btnShowLocationMap);
        btnShowMap.setOnClickListener(this);

        Button btnOpenBrowswer = view.findViewById(R.id.btnCheckForecastOnline);
        btnOpenBrowswer.setOnClickListener(this);

        Button btnShare = view.findViewById(R.id.btnShareForecast);
        btnShare.setOnClickListener(this);

        Button btnRedownload = view.findViewById(R.id.btnRedownloadWeather);
        btnRedownload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnShowLocationMap){
            // launch the map app to show this location.
            Intent intent = new Intent(Intent.ACTION_VIEW);
            // create a URI for geo:0,0?q=mLocationName

            // update the intent with the data (URI)
            intent.setData(Utils.buildUri("geo:0,0", "q", mLocationName));
            // launch it
//            if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                startActivity(intent);
//            }
        }
        else if (v.getId() == R.id.btnCheckForecastOnline){
            Uri webpage = Utils.buildUri(
                    "https://www.bing.com/search",
                    "q",
                    mLocationName + " weather");
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
//            if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                startActivity(intent);
//            }
        }
        else if (v.getId() == R.id.btnShareForecast){
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String message = "Forecast for " + mLocationName;
            intent.putExtra("sms_body", message);
//            if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                startActivity(intent);
//            }
        }
        else if (v.getId() == R.id.btnRedownloadWeather){
            // delete any existing data for this location

            Executor executor = Executors.newSingleThreadExecutor();
            Handler hander = new Handler(Looper.getMainLooper());
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    // call the DAO to delete the data for this location
                    hourForecastDAO.deleteForLocation(mLocationName);
                    // download the weather forecast on the main / UI thread
                    hander.post(new Runnable() {
                        @Override
                        public void run() {
                            // redownload it
                            downloadWeatherForecast();
                        }
                    });
                }
            });



        }
    }

    private void downloadWeatherForecast(){
        // now make the HTTP request to get the forecast
        // build our URI
        Uri uri = Uri.parse("https://api.weatherapi.com/v1/forecast.json?key=a3b9cc3fb35943d5826152257210311");
        Uri.Builder uriBuilder = uri.buildUpon();
        uriBuilder.appendQueryParameter("q", mLocationName);
        uriBuilder.appendQueryParameter("days", String.valueOf(mNumDays));
        // create the final URL
        uri = uriBuilder.build();

        // use Volley to make the request
        StringRequest request = new StringRequest(
                Request.Method.GET,
                uri.toString(),
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);

                        // parsing code moved to WeatherApiParser class

                        // clear the data in hourForecasts if there is any
                        hourForecasts.clear();

                        // parse the response with a WeatherApiParser
                        WeatherApiParser parser = new WeatherApiParser();

                        try {
                            List<HourForecast> forecasts = parser.convertForecastJson(response, mLocationName);
                            // add the parsed forecasts to hourForecasts
                            hourForecasts.addAll(forecasts);
                            // store the forecasts in the database on a background thread
                            Executor executor = Executors.newSingleThreadExecutor();
                            executor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    // insert forecasts into the database
                                    hourForecastDAO.insert(forecasts);
                                }
                            });


                            int i = 0;
                        } catch (JSONException | ParseException e) {
                            Log.d(TAG, e.getLocalizedMessage());
                            // display a mesasge to the user
                            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.forecast_download_error), Toast.LENGTH_LONG);
                        }

                        // inform the adapter that the data has changed, to update the RecyclerView
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.forecast_download_error), Toast.LENGTH_LONG);
                Log.e(TAG, error.getLocalizedMessage());
            }
        });
        // now make the request
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }






}