package uk.ac.rgu.socweather;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationConfirmationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationConfirmationFragment extends Fragment implements View.OnClickListener {


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_LOCATION_NAME = "locationName";
    public static final String ARG_NUM_DAYS = "numberOfDays";


    private String mLocationName;
    private int mNumDays;

    public LocationConfirmationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locationName The name of the location to display the forecast for.
     * @param numDays The number of days to display the forecast for.
     * @return A new instance of fragment LocationConfirmationFragment.
     */
    public static LocationConfirmationFragment newInstance(String locationName, int numDays) {
        LocationConfirmationFragment fragment = new LocationConfirmationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LOCATION_NAME, locationName);
        args.putInt(ARG_NUM_DAYS, numDays);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLocationName = getArguments().getString(ARG_LOCATION_NAME);
            mNumDays = getArguments().getInt(ARG_NUM_DAYS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location_confirmation, container, false);

        Button btnGetForecast = view.findViewById(R.id.btnGetForecastLocationConfirm);
        btnGetForecast.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnGetForecastLocationConfirm){
            // create bundle for the arguments
            Bundle args = new Bundle();
            args.putString(ARG_LOCATION_NAME, mLocationName);
            args.putInt(ARG_NUM_DAYS, mNumDays);

            Navigation.findNavController(v).navigate(
                    R.id.action_locationConfirmationFragment_to_forecastFragment, args);
        }
    }
}