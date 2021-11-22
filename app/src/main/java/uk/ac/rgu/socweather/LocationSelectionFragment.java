package uk.ac.rgu.socweather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationSelectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationSelectionFragment extends Fragment implements View.OnClickListener {


    private static final String TAG = "LocSelectFrag";
    private ActivityResultLauncher<String[]> mLocationPermissionRequest;
    private Boolean mFineLocationGranted = null;
    private Boolean mCoarseLocationGranted = null;
    private FusedLocationProviderClient mFusedLocationClient;

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
        registerForLocationPermissionCheck();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationPermissionRequest.unregister();
    }

    private void registerForLocationPermissionCheck() {
        mLocationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                        .RequestMultiplePermissions(), result -> {

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        mFineLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                        mCoarseLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_COARSE_LOCATION, false);
                    } else {
                        mFineLocationGranted = (ContextCompat.checkSelfPermission(
                                getContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED);
                        mCoarseLocationGranted = (ContextCompat.checkSelfPermission(
                                getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED);
                    }
                });
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

        if (v.getId() == R.id.btnGetForecast) {
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
            if (checkIfLocationPermissionGranted()){
                getWeatherForUsersLocation(numberOfDays);
            } else {
                requestLocationPermissions();
            }
        }
    }

    /**
     * Checks to see if the user has grated appropriate persmissions
     */
    private boolean checkIfLocationPermissionGranted() {
        if (mFineLocationGranted != null && mFineLocationGranted) {
            // Precise location access granted.
            Log.d(TAG, "Fine location granted");
            return true;
        } else if (mCoarseLocationGranted != null && mCoarseLocationGranted) {
            // Only approximate location access granted.
            Log.d(TAG, "Course location granted");
            return true;
        }
        // No location access granted.
        Log.d(TAG, "No location granted");
        return false;


    }

    private void requestLocationPermissions() {
        mLocationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    /**
     * Get the user's location and laucnh the forecast fragment
     * @param numberOfDays
     */
    private void getWeatherForUsersLocation(int numberOfDays) {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissions();
            return;
        }

        // get the user's location
        GoogleApiAvailability gaa = new GoogleApiAvailability();
        if (ConnectionResult.SUCCESS == gaa.isGooglePlayServicesAvailable(getContext())) {
            // use Google play services

            Executor executer = Executors.newSingleThreadExecutor();
            mFusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY,null)
                    .addOnSuccessListener(executer, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                Log.d(TAG, "Play Location Services Location " + location.getLatitude() + ", " + location.getLongitude());
                                switchToForecastFragment(location.getLatitude(), location.getLongitude(), numberOfDays);
                            } else {
                                Log.d(TAG, "Play Location Services Location did not return a location ");
                            }
                        }
                    });
        } else {
            // do it using LocationManager
            Log.d(TAG, "No location services on the device");
            LocationManager locationManager = (LocationManager)getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            // check if the GPS Provider is available
            boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (gpsEnabled){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    locationManager.getCurrentLocation(LocationManager.GPS_PROVIDER, null,
                            Executors.newSingleThreadExecutor(),
                            new Consumer<Location>() {
                                @Override
                                public void accept(Location location) {
                                    if (location != null) {
                                        // Logic to handle location object
                                        Log.d(TAG, "LocationManager Location " + location.getLatitude() + ", " + location.getLongitude());
                                        switchToForecastFragment(location.getLatitude(), location.getLongitude(), numberOfDays);
                                    } else {
                                        Log.d(TAG, "LocationManager did not return a GPS location ");
                                    }
                                }
                            });
                }
            } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                    // similar code to above, but using the network provider
            }
        }
    }

    /**
     * Launch the forecast fragement for the user's location
     * @param latitude
     * @param longitude
     * @param numberOfDays
     */
    private void switchToForecastFragment(double latitude, double longitude, int numberOfDays){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Bundle args = new Bundle();
                args.putString("locationName", String.format("%s,%s", String.valueOf(latitude),String.valueOf(longitude)));
                args.putInt("numberOfDays", numberOfDays);
                Navigation.findNavController(getView()).navigate(R.id.action_locationSelectionFragment_to_forecastFragment, args);
            }
        });

    }


}