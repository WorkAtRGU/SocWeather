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
 * Use the {@link LocationSelectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationSelectionFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LocationSelectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LocationSelectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LocationSelectionFragment newInstance(String param1, String param2) {
        LocationSelectionFragment fragment = new LocationSelectionFragment();
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
        View view = inflater.inflate(R.layout.fragment_location_selection, container, false);

        Button btnGetForecast = view.findViewById(R.id.btnGetForecast);
        btnGetForecast.setOnClickListener(this);
        Button btnGpsForecast = view.findViewById(R.id.btnGpsForecast);
        btnGpsForecast.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnGetForecast){
            Navigation.findNavController(v).navigate(R.id.action_locationSelectionFragment_to_locationConfirmationFragment);
        } else if (v.getId() == R.id.btnGpsForecast) {
            Navigation.findNavController(v).navigate(R.id.action_locationSelectionFragment_to_forecastFragment);
        }
    }
}