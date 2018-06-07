package kh.com.touristguide.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import kh.com.touristguide.R;
import kh.com.touristguide.activities.ProvinceDetailActivity;
import kh.com.touristguide.adapters.ProvinceAdapter;
import kh.com.touristguide.helpers.ConstantValue;
import kh.com.touristguide.models.Province;


public class AllProvinceFragment extends Fragment implements ProvinceAdapter.ProvinceClickListener {

    private ProvinceAdapter adapter;
    private List<Province> provinces = new ArrayList<>();
    private FirebaseFirestore firestore;

    public AllProvinceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_all_province, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.all_province_recycler_view);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));

        adapter = new ProvinceAdapter(getContext(), provinces);
        adapter.setOnClickListener(this);
        recyclerView.setAdapter(adapter);
        firestore = FirebaseFirestore.getInstance();
        getAllProvince();

        return root;
    }

    private void getAllProvince() {
        firestore.collection(ConstantValue.PROVINCES).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        provinces.clear();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            try {
                                Province province = documentSnapshot.toObject(Province.class);
                                provinces.add(province);
                            } catch (Exception e) {
                                Toast.makeText(getContext(), "Fail to load some document",
                                        Toast.LENGTH_SHORT).show();
                                Log.w("app", e.getMessage());
                            }
                        }
                        adapter.setProvinces(provinces);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("app", e.getMessage());
                    }
                });
    }

    @Override
    public void onItemClick(View view, int position) {
        Province province = provinces.get(position);
        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), view,
                getString(R.string.shared_image)).toBundle();
        startActivity(new Intent(getContext(), ProvinceDetailActivity.class)
                .putExtra("province", province), bundle);
    }
}
