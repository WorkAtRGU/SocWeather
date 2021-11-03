package uk.ac.rgu.socweather;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationSelectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationSelectionFragment extends Fragment implements View.OnClickListener {



    public LocationSelectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
      * @return A new instance of fragment LocationSelectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LocationSelectionFragment newInstance() {
        LocationSelectionFragment fragment = new LocationSelectionFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location_selection, container, false);

        Button btnGetForecast = view.findViewById(R.id.btnGetForecast);
        btnGetForecast.setOnClickListener(this);
        Button btnGpsForecast = view.findViewById(R.id.btnGpsForecast);
        btnGpsForecast.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

        // Get the number of days entered by the user
        EditText etNumberOfDays = getView().findViewById(R.id.editTextNumber);
        int numberOfDays = Integer.parseInt(etNumberOfDays.getText().toString());

        if (v.getId() == R.id.btnGetForecast){
            // Get the location entered by the yser
            EditText etLocation = getView().findViewById(R.id.etEnterLocation);
            String location = etLocation.getText().toString();

            // create bundle for the arguments
            Bundle args = new Bundle();
            args.putString("locationName", location);
            args.putInt("numberOfDays", numberOfDays);

            Navigation.findNavController(v).navigate(
                    R.id.action_locationSelectionFragment_to_locationConfirmationFragment, args);
        } else if (v.getId() == R.id.btnGpsForecast) {
            Navigation.findNavController(v).navigate(R.id.action_locationSelectionFragment_to_forecastFragment);
        }
    }
}