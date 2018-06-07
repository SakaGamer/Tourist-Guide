package kh.com.touristguide.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import kh.com.touristguide.R;
import kh.com.touristguide.activities.PlaceDetailActivity;
import kh.com.touristguide.adapters.ResortAdapter;
import kh.com.touristguide.helpers.ConstantValue;
import kh.com.touristguide.models.Place;

public class SavePlaceFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener, ResortAdapter.ItemClickListener {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    TextView txtStatus;
    TextView txtStatusBelow;
    FirebaseFirestore firestore;
    ResortAdapter placeAdapter;
    List<Place> places = new ArrayList<>();
    BottomSheetBehavior bottomSheetBehavior;
    LinearLayout bottomSheet;
    RelativeLayout relativeLayout;


    public SavePlaceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_save_place, container, false);

        firestore = FirebaseFirestore.getInstance();
        bottomSheet = root.findViewById(R.id.bottom_sheet_layout);
        txtStatus = root.findViewById(R.id.save_place_txt_status);
        txtStatusBelow = root.findViewById(R.id.save_place_txt_status_below);
        relativeLayout = root.findViewById(R.id.save_place_layout);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        swipeRefreshLayout = root.findViewById(R.id.save_place_swipe_refresh_layout);
        recyclerView = root.findViewById(R.id.save_place_recycler_view);

        swipeRefreshLayout.setOnRefreshListener(this);


        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            loadSavePlaceFromServer();
            txtStatus.setVisibility(View.GONE);
            txtStatusBelow.setVisibility(View.GONE);
        } else {
            Toast.makeText(getContext(), "Please Sign In or Sign Up to save tourist place",
                    Toast.LENGTH_SHORT).show();
            txtStatus.setVisibility(View.VISIBLE);
            txtStatusBelow.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Place place = places.get(position);
        startActivity(new Intent(getContext(), PlaceDetailActivity.class)
                .putExtra("place", place));
    }

    @Override
    public void onRefresh() {
        loadSavePlaceFromServer();
    }

    private void setRecyclerView(RecyclerView recyclerView){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        placeAdapter = new ResortAdapter(getContext(), places, 4);
        placeAdapter.setOnClickListener(this);
        recyclerView.setAdapter(placeAdapter);
    }

    private void loadSavePlaceFromServer() {
        swipeRefreshLayout.setRefreshing(true);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String order = "savedBy." + firebaseUser.getUid();
            firestore.collection(ConstantValue.USERS).document(firebaseUser.getUid())
                    .collection(ConstantValue.SAVE_PLACES).orderBy(order).get()
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
                                    if (documentSnapshot != null && documentSnapshot.exists()) {
                                        try {
                                            Place place = documentSnapshot.toObject(Place.class);
                                            places.add(place);
                                        } catch (Exception e) {
                                            Toast.makeText(getContext(), "Error getting some document",
                                                    Toast.LENGTH_SHORT).show();
                                            Log.w("app", e.getMessage());
                                        }
                                    }
                                }
                                txtStatus.setVisibility(View.GONE);
                                swipeRefreshLayout.setRefreshing(false);
                            }
                            placeAdapter.setPlaces(places);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("app", e.getMessage());
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Please Sign In or Sign Up to save tourist place",
                    Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onClick(View v) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }
    }
}
