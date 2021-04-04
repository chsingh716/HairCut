package uk.ac.tees.S0321466;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

//access global variable of registerUser activity
import static uk.ac.tees.S0321466.registerUser.ADDRESS_USER;
import static uk.ac.tees.S0321466.registerUser.MAP_CODE_REQUEST;
public class map extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private EditText et_location;
    private Button _submitButton;
    private GoogleMap _map;

    private OnMapReadyCallback _callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            _map = googleMap;
            if (ActivityCompat.checkSelfPermission(map.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(map.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            getDeviceLocation();
            _map.setMyLocationEnabled(true);
        }
    };

    private Location lastKnownLocation;
    private float DEFAULT_ZOOM = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        et_location = findViewById(R.id.map_address);
        _submitButton = findViewById(R.id.map_submit_button);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                getDeviceLocation();
            }
        });

        _submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String location = et_location.getText().toString();
                if (location.isEmpty()) {
                    Toast.makeText(map.this, getString(R.string.location_error), Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(map.this, registerUser.class);
                    intent.putExtra(ADDRESS_USER, location);
                    setResult(MAP_CODE_REQUEST, intent);
                    finish();
                }
            }
        });
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.map_container, mapFragment)
                .commit();

        if (mapFragment != null) {
            mapFragment.getMapAsync(_callback);
        }

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void getDeviceLocation() {
        try {
            Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            //LatLng _dummy = new LatLng(54.5742982466006, -1.2349123090100282);
                            //_map.addMarker(new MarkerOptions().position(_dummy).title("My Location234"));
                           _map.addMarker(new MarkerOptions().position(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude())).title("My Location"));

//                            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
//                                    _dummy, DEFAULT_ZOOM);

                            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), DEFAULT_ZOOM);
                            _map.animateCamera(location);
                            getLocationName();
                        }
                    }
                }
            });

        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void getLocationName() {
        Geocoder geoc;
        List<Address> addresses;
        geoc = new Geocoder(this, Locale.getDefault());
//        LatLng _dummy = new LatLng(54.5742982466006, -1.2349123090100282);

        try {
           // addresses = geocoder.getFromLocation(_dummy.latitude, _dummy.longitude, 1);
            addresses = geoc.getFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), 1); // Here 1 represent max location result is returned, range varied from 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            et_location.setText(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        getDeviceLocation();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        getDeviceLocation();
    }
}


