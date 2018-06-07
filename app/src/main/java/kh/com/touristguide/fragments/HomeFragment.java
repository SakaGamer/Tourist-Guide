package kh.com.touristguide.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import kh.com.touristguide.R;
import kh.com.touristguide.activities.PlaceDetailActivity;
import kh.com.touristguide.adapters.ResortAdapter;
import kh.com.touristguide.helpers.ConstantValue;
import kh.com.touristguide.models.Place;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        ResortAdapter.ItemClickListener {

    private SwipeRefreshLayout swipeRefreshLayout;

    private ResortAdapter adapter;
    private FirebaseFirestore firestore;
    private List<Place> places = new ArrayList<>();


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        swipeRefreshLayout = root.findViewById(R.id.home_swipe_refresh_layout);
        RecyclerView recyclerView = root.findViewById(R.id.home_recycler_view);
        setUpRecyclerView(recyclerView);

        firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);
        swipeRefreshLayout.setOnRefreshListener(this);

        readDataFromServer();

        return root;
    }

    private void setUpRecyclerView(RecyclerView recyclerView){
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter = new ResortAdapter(getContext(), places, 5);
        adapter.setOnClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Place place = places.get(position);
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                view, getString(R.string.shared_image));
        startActivity(new Intent(getContext(), PlaceDetailActivity.class)
                .putExtra("place", place), optionsCompat.toBundle());
    }

    @Override
    public void onRefresh() {
        readDataFromServer();
    }

    private void readDataFromServer() {
        swipeRefreshLayout.setRefreshing(true);
        firestore.collection(ConstantValue.PLACES).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        places.clear();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            try {
                                Place place = documentSnapshot.toObject(Place.class);
                                places.add(place);
                            } catch (Exception e) {
                                Toast.makeText(getContext(), "Fail to load document",
                                        Toast.LENGTH_SHORT).show();
                                Log.w("app", e.getMessage());
                            }
                        }
                        adapter.setPlaces(places);
                        swipeRefreshLayout.setRefreshing(false);
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("app", e.getMessage());
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

//    private void loadAllPlaces(){
////        firestore.collection(ConstantValue.PLACES)
//    }

    // end main class
}
