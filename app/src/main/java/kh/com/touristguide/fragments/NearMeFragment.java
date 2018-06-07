package kh.com.touristguide.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kh.com.touristguide.R;
import kh.com.touristguide.activities.NearMeOnMapActivity;
import kh.com.touristguide.adapters.ResortAdapter;
import kh.com.touristguide.helpers.ConstantValue;
import kh.com.touristguide.helpers.Distance;
import kh.com.touristguide.models.Place;

public class NearMeFragment extends Fragment implements ResortAdapter.ItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView txtStatus;
    private FirebaseFirestore firestore;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;

    private boolean locationPermissionGranted = false;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1001;
    private ResortAdapter adapter;
    private List<Place> places = new ArrayList<>();


    public NearMeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // initialize fused location provider client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(container.getContext());
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_near_me, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firestore = FirebaseFirestore.getInstance();
        FloatingActionButton floatingActionButton = view.findViewById(R.id.near_me_fab);
        swipeRefreshLayout = view.findViewById(R.id.near_me_swipe_refresh_layout);
        txtStatus = view.findViewById(R.id.near_me_txt_status);
        RecyclerView recyclerView = view.findViewById(R.id.near_me_recycler_view);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));
        adapter = new ResortAdapter(getContext(), places, 3);
        adapter.setOnClickListener(this);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        floatingActionButton.setOnClickListener(this);

        getLocationPermission();
    }

    private void getLocationPermission() {
        if (getContext() != null && getActivity() != null) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
                getCurrentLocation();
            } else {
                Toast.makeText(getContext(), R.string.request_location_permission,
                        Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                    getCurrentLocation();
                }
                break;
            }
        }
    }

    private void getCurrentLocation() {
        swipeRefreshLayout.setRefreshing(true);
        try {
            if (locationPermissionGranted) {
                fusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                currentLocation = location;
                                getNearByPlace(location);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Fail to get current location",
                                        Toast.LENGTH_LONG).show();
                                swipeRefreshLayout.setRefreshing(false);
                                Log.d("app", "getCurrentLocation: ", e.fillInStackTrace());
                            }
                        });
            } else {
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("app", "getCurrentLocation: ", e.fillInStackTrace());
        }
    }

    private void getNearByPlace(Location location) {
        try {
            String province = reverseGeoCoding(location.getLatitude(), location.getLongitude());
            firestore.collection(ConstantValue.PLACES).whereEqualTo("province", province).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            places.clear();
                            if (queryDocumentSnapshots.getDocuments().size() == 0) {
                                txtStatus.setText(R.string.no_result);
                                txtStatus.setVisibility(View.VISIBLE);
                                swipeRefreshLayout.setRefreshing(false);
                            } else {
                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                    places.add(documentSnapshot.toObject(Place.class));
                                }
                                adapter.setPlaces(places);
                                txtStatus.setVisibility(View.GONE);
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            txtStatus.setVisibility(View.VISIBLE);
                            Toast.makeText(getContext(), "Fail to load document",
                                    Toast.LENGTH_SHORT).show();
                            swipeRefreshLayout.setRefreshing(false);
                            Log.e("app", e.getMessage());
                        }
                    });
        } catch (Exception e) {
            swipeRefreshLayout.setRefreshing(false);
            txtStatus.setText(R.string.no_location);
            txtStatus.setVisibility(View.VISIBLE);
            Log.e("app", "getNearByPlace: ", e.fillInStackTrace());
        }
    }

    private String reverseGeoCoding(double lat, double lng) {
        String province = "";
        Geocoder geocoder = new Geocoder(getContext(), Locale.ENGLISH);
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                Log.d("app", "reverseGeoCoding: " + address.getAdminArea());
                province = address.getAdminArea();
            }
        } catch (Exception e) {
            Log.e("app", e.getMessage());
            province = null;
        }
        return province;
    }

    private void calculateDistance(int pos) {
        Place place = places.get(pos);
        double lat1 = 0;
        double lng1 = 0;
        double lat2 = 0;
        double lng2 = 0;
        try {
            lat1 = place.getGeoPoint().get("lat");
            lng1 = place.getGeoPoint().get("lng");
            lat2 = currentLocation.getLatitude();
            lng2 = currentLocation.getLongitude();
        } catch (Exception e) {
            e.printStackTrace();
        }
        double distance = Distance.getDistance(lat1, lng1, lat2, lng2);
        Log.d("app", "Distance " + distance);
        Toast.makeText(getContext(), "Distance " + distance, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(View view, int position) {
        calculateDistance(position);
    }

    @Override
    public void onRefresh() {
        getLocationPermission();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.near_me_fab) {
            startActivity(new Intent(getContext(), NearMeOnMapActivity.class)
                    .putExtra("location", currentLocation)
                    .putExtra("places", (Serializable) places));
        }
    }

}
