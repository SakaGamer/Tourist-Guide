package kh.com.touristguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import kh.com.touristguide.R;
import kh.com.touristguide.adapters.ResortAdapter;
import kh.com.touristguide.helpers.ConstantValue;
import kh.com.touristguide.helpers.VolleyRequestQueue;
import kh.com.touristguide.models.Place;
import kh.com.touristguide.models.Province;

public class ProvinceDetailActivity extends AppCompatActivity implements ResortAdapter.ItemClickListener {

    private NetworkImageView networkImageView;
    private FirebaseFirestore firestore;
    private ContentLoadingProgressBar progressBar;
    private TextView textProvince, textPopulation, textArea, textDensity;
    private ResortAdapter adapter;

    private List<Place> places = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_province_detail);

        Toolbar toolbar = findViewById(R.id.province_detail_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        networkImageView = findViewById(R.id.province_detail_image_view);
        textProvince = findViewById(R.id.province_detail_txt_province);
        textPopulation = findViewById(R.id.province_detail_txt_population);
        textArea = findViewById(R.id.province_detail_txt_area);
        textDensity = findViewById(R.id.province_detail_txt_density);
        progressBar = findViewById(R.id.progress_bar);

        RecyclerView recyclerView = findViewById(R.id.place_detail_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        adapter = new ResortAdapter(getApplicationContext(), places, 2);
        adapter.setOnClickListener(this);
        recyclerView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();
        Province province = (Province) getIntent().getSerializableExtra("province");
        loadImageProvince(province);

    }

    private void loadImageProvince(final Province province) {
        progressBar.show();
        if (province.getPhotoUrl() != null) {
            ImageLoader imageLoader = VolleyRequestQueue.getInstance(getApplicationContext()).getImageLoader();
            networkImageView.setImageUrl(province.getPhotoUrl(), imageLoader);
        }
        getProvinceDetail(province);
    }

    private void getProvinceDetail(Province province) {
        setTitle(province.getName());
        setProvinceDetail(province);
        loadResort(province.getName());
    }

    private void loadResort(String province) {
        firestore.collection(ConstantValue.PLACES).whereEqualTo("province", province).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d("app", "No document found");
                        } else {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                try {
                                    places.add(documentSnapshot.toObject(Place.class));
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), "Fail to load some documents",
                                            Toast.LENGTH_SHORT).show();
                                    Log.d("app", e.getMessage());
                                }
                            }
                        }
                        adapter.setPlaces(places);
                        progressBar.hide();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Fail to load document", Toast.LENGTH_SHORT).show();
                        Log.d("app", e.getMessage());
                        progressBar.hide();
                    }
                });
    }

    private void setProvinceDetail(Province province) {
        textProvince.setText(province.getName());
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        String population = numberFormat.format(province.getPopulation()) + " people";
        textPopulation.setText(population);
        String area = numberFormat.format(province.getArea()) + " km2";
        textArea.setText(area);
        String density = numberFormat.format(province.getDensity()) + " people/km2";
        textDensity.setText(density);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onItemClick(View view, int position) {
        Place place = places.get(position);
        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                view, getString(R.string.shared_image)).toBundle();
        startActivity(new Intent(getApplicationContext(), PlaceDetailActivity.class)
                .putExtra("place", place), bundle);
    }
}
