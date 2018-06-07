package kh.com.touristguide.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import kh.com.touristguide.R;
import kh.com.touristguide.adapters.ResortAdapter;
import kh.com.touristguide.helpers.ConstantValue;
import kh.com.touristguide.helpers.VolleyRequestQueue;
import kh.com.touristguide.models.Place;

import static kh.com.touristguide.helpers.ConstantValue.PREF_UNIQUE_ID;

public class PlaceDetailActivity extends AppCompatActivity implements View.OnClickListener,
        OnMapReadyCallback, GoogleMap.InfoWindowAdapter, ResortAdapter.ItemClickListener {

    private FloatingActionButton floatingActionButton;
    private ContentLoadingProgressBar progressBar;
    private TextView textName, textProvince, textCategory, textDescription;
    private ImageView imgExpand;
    private CardView cardViewDescription;
    private FirebaseFirestore firestore;

    private Place place;
    private boolean descriptionClick = false;
    private List<Place> places = new ArrayList<>();
    private ResortAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        // add toolbar
        Toolbar toolbar = findViewById(R.id.place_detail_toolbar);
        setSupportActionBar(toolbar);

        // add back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // initialize all view
        progressBar = findViewById(R.id.progress_bar);
        floatingActionButton = findViewById(R.id.place_detail_fab);
        textName = findViewById(R.id.place_detail_txt_name);
        textProvince = findViewById(R.id.place_detail_txt_province);
        textCategory = findViewById(R.id.place_detail_txt_categories);
        textDescription = findViewById(R.id.place_detail_txt_description);
        imgExpand = findViewById(R.id.place_detail_img_up_down_description);
        cardViewDescription = findViewById(R.id.place_detail_card_detail);

        RecyclerView recyclerView = findViewById(R.id.place_detail_recycler_view);
        setUpRecyclerView(recyclerView);

        imgExpand.setOnClickListener(this);
        floatingActionButton.setOnClickListener(this);
        firestore = FirebaseFirestore.getInstance();

        // Construct map fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.province_detail_map);
        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(this);
        }
        // get place object from intent
        place = (Place) getIntent().getSerializableExtra("place");
        // load place image
        loadImage(place);
    }

    private void setUpRecyclerView(RecyclerView recyclerView){
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));
        adapter = new ResortAdapter(getApplicationContext(), places, 1);
        adapter.setOnClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void loadImage(final Place place) {
        progressBar.show();
        if (place.getPhotoUrl() != null) {
            ImageView imageBackdrop = findViewById(R.id.place_detail_image_view);
            Glide.with(getApplicationContext()).load(place.getPhotoUrl()).into(imageBackdrop);
        }
        setPlaceDetail(place);
        loadRelatedPlace(place);
        listenPlaceChange();
        progressBar.hide();
    }

    private void listenPlaceChange() {
        firestore.collection(ConstantValue.PLACES).document(place.getName())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("app", "Listen failed.", e.fillInStackTrace());
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            try {
                                place = documentSnapshot.toObject(Place.class);
                                if (place != null) {
                                    setPlaceDetail(place);
                                }
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            Log.d("app", "Current data: null");
                        }
                    }
                });
    }

    private void setPlaceDetail(Place place) {
        try {
            setTitle(place.getName());
            textName.setText(place.getName());
            textProvince.setText(place.getProvince());
            textCategory.setText(place.getCategories().keySet().toString()
                    .substring(1, place.getCategories().keySet().toString().length() - 1));
            textDescription.setText(place.getDescription());
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                if (place.getSavedBy().get(firebaseUser.getUid()) != null) {
                    floatingActionButton.setImageResource(R.drawable.ic_heart_filled);
                } else {
                    floatingActionButton.setImageResource(R.drawable.ic_heart);
                }
            }
        } catch (Exception e) {
            Log.e("app", "Exception: ", e.fillInStackTrace());
        }
    }

    private void loadRelatedPlace(final Place place) {
        String[] categories = getPlaceCategories(place.getCategories());
        try {
            firestore.collection(ConstantValue.PLACES).whereEqualTo("categories." + categories[0], true).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            places.clear();
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                if (documentSnapshot.toObject(Place.class).getId().contentEquals(place.getId())) {
                                    continue;
                                }
                                places.add(documentSnapshot.toObject(Place.class));
                            }
                            adapter.setPlaces(places);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("app", e.getMessage());
                        }
                    });
        } catch (Exception e) {
            Log.e("app", "Exception: ", e.fillInStackTrace());
        }
    }

    @Override
    public void onClick(View v) {
        int clickId = v.getId();
        if (clickId == R.id.place_detail_fab) {
            toggleHeartSave();
        } else if (clickId == R.id.place_detail_img_up_down_description) {
            toggleDescription();
        }
    }

    private void toggleHeartSave() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            try {
               if(place.getSavedBy().get(firebaseUser.getUid()) != null){
                   removeSaveFromDb(place, firebaseUser);
               } else {
                   addSaveToDb(place, firebaseUser);
               }
            } catch (Exception e){
                Toast.makeText(getApplicationContext(), "Fail to load some data",
                        Toast.LENGTH_SHORT).show();
                Log.e("app", "Exception: ", e.fillInStackTrace());
            }
        } else {
            // No user is signed in
            Snackbar.make(findViewById(R.id.place_detail_layout),
                    "Sign In or Sign up to saved tourist place to your favourite list",
                    Snackbar.LENGTH_LONG).show();
        }
    }

    private void toggleDescription() {
        descriptionClick = !descriptionClick;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(cardViewDescription);
        }
        if (descriptionClick) {
            textDescription.setMaxLines(Integer.MAX_VALUE);
            imgExpand.setImageResource(R.drawable.ic_arrow_up);
        } else {
            textDescription.setMaxLines(2);
            imgExpand.setImageResource(R.drawable.ic_arrow_down);
        }
    }

    private void addSaveToDb(Place place, FirebaseUser firebaseUser) {
        // create firestore write batch
        WriteBatch writeBatch = firestore.batch();
        // add uid to selected place
        DocumentReference placeRef = firestore.collection(ConstantValue.PLACES).document(place.getName());
        Map<String, Long> map = new HashMap<>();
        map.put(firebaseUser.getUid(), System.currentTimeMillis());
        writeBatch.update(placeRef, "savedBy", map);
        // add current place to user saved place collection
        DocumentReference userSavedPlaceRef = firestore.collection(ConstantValue.USERS)
                .document(firebaseUser.getUid()).collection(ConstantValue.SAVE_PLACES).document(place.getName());
        writeBatch.set(userSavedPlaceRef, place);
        // add uid to user saved place collection
        DocumentReference newUserSavedPlaceRef= firestore.collection(ConstantValue.USERS)
                .document(firebaseUser.getUid()).collection(ConstantValue.SAVE_PLACES).document(place.getName());
        Map<String, Long> map2 = new HashMap<>();
        map2.put(firebaseUser.getUid(), System.currentTimeMillis());
        writeBatch.update(newUserSavedPlaceRef, "savedBy", map2);
        // commit write batch
        writeBatch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                floatingActionButton.setImageResource(R.drawable.ic_heart_filled);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Fail to save place",
                        Toast.LENGTH_SHORT).show();
                Log.e("app", "addSaveToDb: ", e.fillInStackTrace());
            }
        });
    }

    private void removeSaveFromDb(Place place, FirebaseUser firebaseUser) {
        // create firestore write batch
        WriteBatch writeBatch = firestore.batch();
        // add uid to selected place document
        DocumentReference placeRef = firestore.collection(ConstantValue.PLACES).document(place.getName());
        writeBatch.update(placeRef, "savedBy." + firebaseUser.getUid(), null);
        // add current place to user saved place collection
        DocumentReference userSavedPlaceRef = firestore.collection(ConstantValue.USERS)
                .document(firebaseUser.getUid()).collection(ConstantValue.SAVE_PLACES).document(place.getName());
        writeBatch.delete(userSavedPlaceRef);
        writeBatch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                floatingActionButton.setImageResource(R.drawable.ic_heart);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Fail to remove place",
                        Toast.LENGTH_SHORT).show();
                Log.e("app", "removeSaveFromDb: ", e.fillInStackTrace());
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setInfoWindowAdapter(this);
        if (place != null) {
            try {
                LatLng latLng = new LatLng(place.getGeoPoint().get("lat"), place.getGeoPoint().get("lng"));
                googleMap.addMarker(new MarkerOptions().position(latLng).title(place.getName()).snippet(place.getProvince()));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Fail to load map location",
                        Toast.LENGTH_SHORT).show();
                Log.e("app", "onMapReady: ", e.fillInStackTrace());
            }
        }
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        // google map marker info window
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        // Inflate the layouts for the info window, title and snippet to marker.
        View infoWindow = getLayoutInflater().inflate(R.layout.dialog_info_contents,
                (FrameLayout) findViewById(R.id.google_map), false);
        TextView title = (infoWindow.findViewById(R.id.title));
        title.setText(marker.getTitle());
        TextView snippet = (infoWindow.findViewById(R.id.snippet));
        snippet.setText(marker.getSnippet());
        return infoWindow;
    }

    @Override
    public void onItemClick(View view, int position) {
        Place place = places.get(position);
        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                view, getString(R.string.shared_image)).toBundle();
        startActivity(new Intent(getApplicationContext(), PlaceDetailActivity.class)
                .putExtra("place", place), bundle);
    }

    private String[] getPlaceCategories(Map<String, Boolean> categories) {
        String values = categories.keySet().toString().substring(1,
                categories.keySet().toString().length() - 1);
        return values.split(",");
    }


    private static String uniqueId = null;
    public synchronized static String id(Context context) {
        if (uniqueId == null) {
            SharedPreferences sharedPref = context.getSharedPreferences(PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueId = sharedPref.getString(PREF_UNIQUE_ID, null);
            if (uniqueId == null) {
                uniqueId = UUID.randomUUID().toString();
                sharedPref.edit().putString(PREF_UNIQUE_ID, uniqueId).apply();
            }
        }
        return uniqueId;
    }

}
