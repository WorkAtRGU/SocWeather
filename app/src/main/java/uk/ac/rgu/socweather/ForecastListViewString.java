package uk.ac.rgu.socweather;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import uk.ac.rgu.socweather.data.ForecastRepository;
import uk.ac.rgu.socweather.data.HourForecast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForecastListViewString#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForecastListViewString extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ForecastListViewString() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ForecastListViewString.
     */
    // TODO: Rename and change types and number of parameters
    public static ForecastListViewString newInstance(String param1, String param2) {
        ForecastListViewString fragment = new ForecastListViewString();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forecast_list_view_string, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // get the data for the weather forecast
        List<HourForecast> forecast = ForecastRepository.getRepository(getContext()).getRandomHourlyForecasts(24*3);

        // create a array of string for each hour forecast
        List<String> forecastStrs = new ArrayList<String>();
        for (HourForecast hourForecast : forecast){
            String hrStr = getContext().getString(
                    R.string.forecasts_hour_str,
                    hourForecast.getDate(), hourForecast.getHour(),hourForecast.getTemperature(), hourForecast.getHumidity());
            forecastStrs.add(hrStr);
        }


        // create a ArrayAdapter for the weather list view
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_list_item_1, forecastStrs);


        // update the ListView with that array adapter
        ListView forecastListView = view.findViewById(R.id.lv_forecast_str);
        forecastListView.setAdapter(arrayAdapter);
    }
}

