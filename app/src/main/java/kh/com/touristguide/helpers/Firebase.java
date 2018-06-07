package kh.com.touristguide.helpers;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import kh.com.touristguide.models.Place;

public class Firebase {

    private static Firebase instance;
    private FirebaseFirestore firestore;
    private List<Place> placeList = new ArrayList<>();

    public static Firebase firestore() {
        if (instance == null) {
            instance = new Firebase();
        }
        return instance;
    }

    private Firebase() {
        firestore = FirebaseFirestore.getInstance();
    }

    public List<Place> getPlace() {
        placeList.clear();
        firestore.collection(ConstantValue.PLACES).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.getDocuments().size() == 0) {
                            placeList.clear();
                        } else {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                placeList.add(documentSnapshot.toObject(Place.class));
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("app", e.getMessage());
                    }
                });
        return placeList;
    }

    public List<Place> getPlaceByCategory(String category) {
        placeList.clear();
        String condition = "categories." + category;
        firestore.collection(ConstantValue.PLACES).whereEqualTo(condition, true).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.getDocuments().size() == 0) {
                            placeList.clear();
                        } else {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                placeList.add(documentSnapshot.toObject(Place.class));
                            }
                            Log.d("app", "list " +placeList.size());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("app", e.getMessage());
                    }
                });
        return placeList;
    }

    public List<Place> getPlaceByPeople(String people) {
        placeList.clear();
//        String condition = "categories." + category;
        firestore.collection(ConstantValue.PLACES).whereEqualTo("people", people).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.getDocuments().equals(0)) {
                            placeList.clear();
                        } else {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                placeList.add(documentSnapshot.toObject(Place.class));
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("app", e.getMessage());
                    }
                });
        return placeList;
    }

}
