package uk.ac.rgu.socweather;

import static java.net.Proxy.Type.HTTP;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import uk.ac.rgu.socweather.data.ForecastRepository;
import uk.ac.rgu.socweather.data.HourForecast;
import uk.ac.rgu.socweather.data.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForecastFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForecastFragment extends Fragment implements View.OnClickListener {


    // member variables for the setting up the display
    private String mLocationName;
    private int mNumDays;

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

        // get the data to display
        List<HourForecast> hourForecasts = ForecastRepository.getRepository(getContext()).getHourlyForecasts(mNumDays*24);

        // create the adapter for the RecyclerView
        HourForecastRecyclerViewAdapter adapter = new HourForecastRecyclerViewAdapter(getContext(), hourForecasts);

        // get the RecyclerView
        RecyclerView rvForecast = view.findViewById(R.id.rv_forecast);

        // wireup the RecyclerView with the adapter
        rvForecast.setLayoutManager(new LinearLayoutManager(getContext()));
        rvForecast.setAdapter(adapter);

        // add action listeners to the three buttons
        Button btnShowMap = view.findViewById(R.id.btnShowLocationMap);
        btnShowMap.setOnClickListener(this);

        Button btnOpenBrowswer = view.findViewById(R.id.btnCheckForecastOnline);
        btnOpenBrowswer.setOnClickListener(this);

        Button btnShare = view.findViewById(R.id.btnShareForecast);
        btnShare.setOnClickListener(this);
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
    }






}