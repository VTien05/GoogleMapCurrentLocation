package edu.hanu.myapplication;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import edu.hanu.myapplication.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    Location currentLocation;
    
    //is used for accessing the device's last known location and receiving location updates
    FusedLocationProviderClient fusedLocationProviderClient;
    
    //request permission from the user to access the device's location, 
    //and to check whether the permission was granted or not in the onRequestPermissionsResult() method
    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocation();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        if (currentLocation != null){
            LatLng sydney = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(sydney).title("Current Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }

    }

    private void getCurrentLocation(){
        //check if the app has been granted permission to access the user's location.
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this
                , Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION
                    }, REQUEST_CODE);
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    Toast.makeText(getApplicationContext(), String.valueOf(currentLocation.getLatitude()), Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), String.valueOf(currentLocation.getLongitude()), Toast.LENGTH_LONG).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(MapsActivity.this);
                }
            }
        });

        //create a request for the user's current location
        LocationRequest mLocationRequest = LocationRequest.create();
        
        //sets the interval at which the app receives location updates
        mLocationRequest.setInterval(60000);
        
        //sets the fastest rate at which the app can handle location updates
        mLocationRequest.setFastestInterval(5000);
        
        //sets the priority level of the request, which determines the accuracy of the location data received
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        
        //receive location updates when they become available
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Toast.makeText(getApplicationContext()," location result is  " + locationResult, Toast.LENGTH_LONG).show();

                if (locationResult == null) {
                    Toast.makeText(getApplicationContext(),"current location is null ", Toast.LENGTH_LONG).show();

                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        Toast.makeText(getApplicationContext(),"current location is " + location.getLongitude(), Toast.LENGTH_LONG).show();

                        //TODO: UI updates.
                    }
                }
            }
        };
    }

    // handle the result of a permission request and take appropriate action based on whether the permission was granted or not
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        // the switch statement checks the requestCode to see if it matches the REQUEST_CODE constant that was defined earlier.
        // If the user granted the permission, the getCurrentLocation() method is called to retrieve the current location of the device.
        // If the permission was not granted, the method does nothing.
        switch (REQUEST_CODE) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                }
                break;
        }
    }
}
