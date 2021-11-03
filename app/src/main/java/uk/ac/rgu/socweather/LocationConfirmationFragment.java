package uk.ac.rgu.socweather;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.List;

import uk.ac.rgu.socweather.data.Utils;

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

        // add the button click listener
        Button btnGetForecast = view.findViewById(R.id.btnGetForecastLocationConfirm);
        btnGetForecast.setOnClickListener(this);

        // get the suitable / candidate locations from the web service
        Uri uri = Utils.buildUri("https://api.weatherapi.com/v1/search.json?key=a3b9cc3fb35943d5826152257210311", "q", mLocationName);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // convert response to a JSON Object
                        try {
                            List<String> locations = new ArrayList<String>();
                            JSONArray rootObj = new JSONArray(response);
                            for (int i = 0, j = rootObj.length(); i<j; i++){
                                JSONObject locationOjb = rootObj.getJSONObject(i);
                                if (locationOjb.has("name")) {
                                    String name = locationOjb.getString("name");
                                    locations.add(name);
                                }
                            }
                            // update the spinner by creating an ArrayAdapter
                            Spinner spinner = view.findViewById(R.id.spConfirmLocation);
                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                                    getContext(), android.R.layout.simple_spinner_item , locations);
                            spinner.setAdapter(spinnerAdapter);
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), getString(R.string.error_downloading_locations), Toast.LENGTH_LONG);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), getString(R.string.error_downloading_locations), Toast.LENGTH_LONG);
                    }

        });

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnGetForecastLocationConfirm){

            // get the selection from the spinner
            Spinner spinner = getView().findViewById(R.id.spConfirmLocation);
            mLocationName = spinner.getSelectedItem().toString();

            // create bundle for the arguments
            Bundle args = new Bundle();
            args.putString(ARG_LOCATION_NAME, mLocationName);
            args.putInt(ARG_NUM_DAYS, mNumDays);

            Navigation.findNavController(v).navigate(
                    R.id.action_locationConfirmationFragment_to_forecastFragment, args);
        }
    }
}