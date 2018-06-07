package kh.com.touristguide.activities;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import kh.com.touristguide.R;
import kh.com.touristguide.helpers.Distance;
import kh.com.touristguide.helpers.VolleyRequestQueue;
import kh.com.touristguide.models.Place;

public class NearMeOnMapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener, GoogleMap.OnInfoWindowClickListener {

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;
    private Location currentLocation;
    private GoogleMap googleMap;

    // Keys for storing activity state.
    private static final String KEY_LOCATION = "location";
    private static final int DEFAULT_ZOOM = 10;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1001;
    private final LatLng mDefaultLocation = new LatLng(11.556447, 104.928213);

    private boolean locationPermissionGranted;
    private List<Place> placeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        setContentView(R.layout.activity_near_me_on_map);
        setTitle("Near Me");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        currentLocation = getIntent().getParcelableExtra("location");
        try {
            placeList = (List<Place>) getIntent().getSerializableExtra("places");
        } catch (Exception e) {
            Log.e("app", "onCreate: ", e.fillInStackTrace());
        }

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (googleMap != null) {
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setOnMapClickListener(this);

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        googleMap.setInfoWindowAdapter(infoWindowAdapter());

        googleMap.setOnInfoWindowClickListener(this);

        // Prompt the user for permission.
        getLocationPermission();
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();
        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
        // Add near by location to maps
        addNearByLocationOnMap();
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void getDeviceLocation() {
        if (currentLocation != null) {
            lastKnownLocation = currentLocation;
            // Set the map's camera position to the current location of the device.
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(currentLocation.getLatitude(),
                            currentLocation.getLongitude()), DEFAULT_ZOOM));
            reverseGeoCoding(currentLocation.getLatitude(), currentLocation.getLongitude());
        } else {
            try {
                if (locationPermissionGranted) {
                    fusedLocationProviderClient.getLastLocation()
                            .addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Set the map's camera position to the current location of the device.
                                    lastKnownLocation = location;
                                    if (location != null) {
                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                                new LatLng(location.getLatitude(),
                                                        location.getLongitude()), DEFAULT_ZOOM));
                                        reverseGeoCoding(location.getLatitude(), location.getLongitude());
                                    } else {
                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                                        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                                        Toast.makeText(getApplicationContext(),
                                                "No location detected, Use PHNOM PENH as default",
                                                Toast.LENGTH_LONG).show();
                                        Log.d("app", "Current location is null. Using defaults.");
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("app", "Current location is null. Using defaults.");
                                    Log.e("app", e.getMessage());
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                                    googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                                }
                            });
                }
            } catch (SecurityException e) {
                Log.e("app: %s", e.getMessage());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
//        updateLocationUI();
    }

    private void updateLocationUI() {
        if (googleMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                googleMap.setMyLocationEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void reverseGeoCoding(double lat, double lng) {
        Geocoder geocoder = new Geocoder(getApplicationContext());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                Log.d("app", "reverseGeoCoding: " + address.getAdminArea());
            }
        } catch (Exception e) {
            Log.e("app", e.getMessage());
        }
    }

    private void addNearByLocationOnMap() {
        if (placeList != null) {
            for (Place place : placeList) {
                double lat = 0;
                double lng = 0;
                if (place.getGeoPoint() != null) {
                    try {
                        lat = place.getGeoPoint().get("lat");
                        lng = place.getGeoPoint().get("lng");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    double distance = Distance.getDistance(lat, lng, currentLocation.getLatitude(),
                            currentLocation.getLongitude());
                    String[] values = String.valueOf(distance).split("\\.");
                    String distanceStr = values[0] + "." + values[1].substring(0, 2) + " km from you";
                    MarkerOptions options = new MarkerOptions().position(new LatLng(lat, lng))
                            .title(place.getName()).snippet(place.getPhotoUrl()).draggable(false);
                    googleMap.addMarker(options);
                }
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        reverseGeoCoding(latLng.latitude, latLng.longitude);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //                Place place = placeList.get(0);
        Toast.makeText(getApplicationContext(), "Click " + marker.getTitle()
                + "/" + marker.getId(), Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(getApplicationContext(), PlaceDetailActivity.class)
//                .putExtra("place", ));
    }


    // Google Map info window adapter
    private GoogleMap.InfoWindowAdapter infoWindowAdapter() {
        return new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.dialog_info_contents,
                        (FrameLayout) findViewById(R.id.google_map), false);
                NetworkImageView thumbnail = infoWindow.findViewById(R.id.thumbnail);
                ImageLoader imageLoader = VolleyRequestQueue.getInstance(getApplicationContext()).getImageLoader();
                thumbnail.setImageUrl(marker.getSnippet(), imageLoader);
                TextView title = infoWindow.findViewById(R.id.title);
                title.setText(marker.getTitle());
//                TextView snippet = infoWindow.findViewById(R.id.snippet);
//                snippet.setText(marker.getSnippet());
                return infoWindow;
            }
        };
    }

}
