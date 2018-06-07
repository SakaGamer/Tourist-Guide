package kh.com.touristguide.fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import kh.com.touristguide.R;
import kh.com.touristguide.activities.PlaceDetailActivity;
import kh.com.touristguide.adapters.PlaceAdapter;
import kh.com.touristguide.adapters.ResortAdapter;
import kh.com.touristguide.helpers.AnimationHelper;
import kh.com.touristguide.helpers.ConstantValue;
import kh.com.touristguide.models.Place;

public class RecommendFragment extends Fragment implements View.OnClickListener,
        PlaceAdapter.PlaceClickListener, ResortAdapter.ItemClickListener {

    private EditText edtDayOff;
    private EditText edtCategory;
    private EditText edtPeople;
    private TextView textStatus;
    private ImageView imgToggleForm;
    private CardView cardViewSearch;
    private ContentLoadingProgressBar progressBar;
    private FirebaseFirestore firestore;
    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager layoutManager;
    private RelativeLayout relativeLayout;

    private ResortAdapter placeAdapter;
    private Boolean toggleFormClicked = false;
    private List<Place> places = new ArrayList<>();
    private int selectedDayOff = 1;
    private String selectedPeople = "Unspecified";
    private String selectedCategory = "All";


    public RecommendFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recommend, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnSearch = view.findViewById(R.id.recommend_btn_search);
        recyclerView = view.findViewById(R.id.recommend_recycler_view);
        edtDayOff = view.findViewById(R.id.recommend_edt_day_off);
        edtCategory = view.findViewById(R.id.recommend_edt_category);
        edtPeople = view.findViewById(R.id.recommend_edt_people);
        textStatus = view.findViewById(R.id.recommend_txt_status);
        imgToggleForm = view.findViewById(R.id.recommend_img_toggle_search);
        ImageView imgToggleResult = view.findViewById(R.id.recommend_img_toggle_result);
        cardViewSearch = view.findViewById(R.id.recommend_card_search);
        relativeLayout = view.findViewById(R.id.recommend_text_result_layout);
        progressBar = view.findViewById(R.id.progress_bar);
        firestore = FirebaseFirestore.getInstance();

        layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        placeAdapter = new ResortAdapter(getContext(), places, 1);
        placeAdapter.setOnClickListener(this);
        recyclerView.setAdapter(placeAdapter);

        edtCategory.setOnClickListener(this);
        edtDayOff.setOnClickListener(this);
        edtPeople.setOnClickListener(this);
        imgToggleForm.setOnClickListener(this);
        imgToggleResult.setOnClickListener(this);
        btnSearch.setOnClickListener(this);

        initForm();
    }

    private void initForm() {
        // init field day off
        edtDayOff.setRawInputType(InputType.TYPE_NULL);
        edtDayOff.setFocusable(false);
        edtDayOff.setText("1");

        // init field categories
        edtCategory.setRawInputType(InputType.TYPE_NULL);
        edtCategory.setFocusable(false);
        edtCategory.setText(R.string.all);

        // init field people
        edtPeople.setRawInputType(InputType.TYPE_NULL);
        edtPeople.setFocusable(false);
        edtPeople.setText(R.string.unspecified);
    }

    @Override
    public void onClick(View v) {
        int clickId = v.getId();
        if (clickId == R.id.recommend_btn_search) {
            getResult();
            toggleForm();
        } else if (clickId == R.id.recommend_img_toggle_search) {
            toggleForm();
        } else if (clickId == R.id.recommend_img_toggle_result) {
            layoutManager.smoothScrollToPosition(recyclerView, null, 0);
        } else if (clickId == R.id.recommend_edt_day_off) {
            showDayOffDialog();
        } else if (clickId == R.id.recommend_edt_category) {
            showCategoriesDialog();
        } else if (clickId == R.id.recommend_edt_people) {
            showPeopleDialog();
        }
    }

    private void toggleForm() {
        toggleFormClicked = !toggleFormClicked;
        if (toggleFormClicked) {
            imgToggleForm.setImageResource(R.drawable.ic_arrow_down);
            AnimationHelper.hideView(cardViewSearch, cardViewSearch.getHeight() + getStatusBarHeight());
            AnimationHelper.pushUp(recyclerView, cardViewSearch.getHeight());
            AnimationHelper.pushUp(relativeLayout, cardViewSearch.getHeight());
        } else {
            imgToggleForm.setImageResource(R.drawable.ic_arrow_up);
            AnimationHelper.showView(cardViewSearch, cardViewSearch.getHeight());
            AnimationHelper.pushDown(recyclerView, cardViewSearch.getHeight());
            AnimationHelper.pushDown(relativeLayout, cardViewSearch.getHeight());
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void onItemClick(View view, int position) {
        // item place click
        Place place = places.get(position);
        startActivity(new Intent(getContext(), PlaceDetailActivity.class)
                .putExtra("place", place));
    }

    private void getResult() {
        progressBar.show();
        places.clear();
        String categories = edtCategory.getText().toString().trim().toLowerCase();
        String people = edtPeople.getText().toString().trim().toLowerCase();
        if (categories.contentEquals("all")) {
            if (people.contentEquals("unspecified")) {
                // query all
                queryPlaces();
            } else {
                // query people
                queryPlaceByPeople(people);
            }
        } else {
            if (people.contentEquals("unspecified")) {
                // query categories
                queryPlaceByCategories(categories);
            } else {
                // query categories and people
                queryPlaceByCategoryAndPeople(categories, people);
            }
        }
    }

    private void queryPlaceByCategories(String categories) {
        String condition = "categories." + categories;
        firestore.collection(ConstantValue.PLACES).whereEqualTo(condition, true).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.getDocuments().size() == 0) {
                            textStatus.setVisibility(View.VISIBLE);
                            progressBar.hide();
                        } else {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                try {
                                    Place place = documentSnapshot.toObject(Place.class);
                                    places.add(place);
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), "Fail to load document",
                                            Toast.LENGTH_SHORT).show();
                                    Log.e("app", e.getMessage());
                                }
                            }
                            textStatus.setVisibility(View.GONE);
                            placeAdapter.setPlaces(places);
                            progressBar.hide();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error loading document",
                                Toast.LENGTH_SHORT).show();
                        Log.e("app", e.getMessage());
                        progressBar.hide();
                    }
                });
    }

    private void queryPlaceByPeople(String people) {
        String condition = "people." + people;
        firestore.collection(ConstantValue.PLACES).whereEqualTo(condition, true).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.getDocuments().size() == 0) {
                            textStatus.setVisibility(View.VISIBLE);
                            progressBar.hide();
                        } else {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                try {
                                    Place place = documentSnapshot.toObject(Place.class);
                                    places.add(place);
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), "Fail to load document",
                                            Toast.LENGTH_SHORT).show();
                                    Log.e("app", e.getMessage());
                                }
                            }
                            textStatus.setVisibility(View.GONE);
                            placeAdapter.setPlaces(places);
                            progressBar.hide();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error loading document",
                                Toast.LENGTH_SHORT).show();
                        Log.e("app", e.getMessage());
                        progressBar.hide();
                    }
                });
    }

    private void queryPlaceByCategoryAndPeople(String category, String people) {
        String condition = "categories." + category;
        String condition2 = "people." + people;
        firestore.collection(ConstantValue.PLACES).whereEqualTo(condition, true)
                .whereEqualTo(condition2, true).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.getDocuments().size() == 0) {
                            textStatus.setVisibility(View.VISIBLE);
                            progressBar.hide();
                        } else {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                try {
                                    Place place = documentSnapshot.toObject(Place.class);
                                    places.add(place);
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), "Fail to load document",
                                            Toast.LENGTH_SHORT).show();
                                    Log.e("app", e.getMessage());
                                }
                            }
                            textStatus.setVisibility(View.GONE);
                            placeAdapter.setPlaces(places);
                            progressBar.hide();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error loading document",
                                Toast.LENGTH_SHORT).show();
                        Log.e("app", e.getMessage());
                        progressBar.hide();
                    }
                });
    }

    private void queryPlaces() {
        firestore.collection(ConstantValue.PLACES).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.getDocuments().size() == 0) {
                            textStatus.setVisibility(View.VISIBLE);
                            progressBar.hide();
                        } else {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                try {
                                    Place place = documentSnapshot.toObject(Place.class);
                                    places.add(place);
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), "Fail to load document",
                                            Toast.LENGTH_SHORT).show();
                                    Log.e("app", e.getMessage());
                                }
                            }
                            textStatus.setVisibility(View.GONE);
                            placeAdapter.setPlaces(places);
                            progressBar.hide();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error loading document",
                                Toast.LENGTH_SHORT).show();
                        Log.e("app", e.getMessage());
                        progressBar.hide();
                    }
                });
    }

    private void showDayOffDialog() {
        final int[] pickedNumber = {1};
        if (selectedDayOff != 1) {
            pickedNumber[0] = selectedDayOff;
        }
        // construct number inflated from view
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_number_picker, null);
        NumberPicker numberPicker = view.findViewById(R.id.number_picker);
        numberPicker.setMaxValue(15);
        numberPicker.setMinValue(1);
        numberPicker.setValue(selectedDayOff);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                pickedNumber[0] = newVal;
            }
        });
        if (getContext() != null) {
            // construct alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Choose Day Off");
            builder.setMessage("Pick Amount of Days");
            builder.setView(view);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    selectedDayOff = pickedNumber[0];
                    String pickedDay = String.valueOf(selectedDayOff);
                    edtDayOff.setText(pickedDay);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }

//    private void showCategoriesDialog() {
//        final String[] items = getResources().getStringArray(R.array.place_categories);
//        final boolean[] checkedItems = new boolean[items.length];
//        if (selectedCategory.size() != 0) {
//            for (String selectedCat : selectedCategory) {
//                for (int jj = 0; jj < items.length; jj++) {
//                    if (selectedCat.equals(items[jj])) {
//                        checkedItems[jj] = true;
//                    }
//                }
//            }
//        }
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setTitle("Choose Category");
//        builder.setMultiChoiceItems(items, checkedItems,
//                new DialogInterface.OnMultiChoiceClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//                        checkedItems[which] = isChecked;
//                    }
//                });
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                selectedCategory.clear();
//                for (int ii = 0; ii < checkedItems.length; ii++) {
//                    if (checkedItems[ii]) {
//                        selectedCategory.add(items[ii]);
//                    }
//                }
//                if (selectedCategory.size() == 0) {
//                    edtCategory.setText(R.string.all);
//                } else {
//                    edtCategory.setText(selectedCategory.toString().substring(1,
//                            selectedCategory.toString().length() - 1));
//                }
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        builder.show();
//    }

    private void showCategoriesDialog() {
        final String[] items = getResources().getStringArray(R.array.place_categories);
        final int[] checkedItem = {0};
        for (int ii = 0; ii < items.length; ii++) {
            if (items[ii].equals(selectedCategory)) {
                checkedItem[0] = ii;
                break;
            }
        }
        if (getContext() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Choose People Go With");
            builder.setSingleChoiceItems(items, checkedItem[0], new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    checkedItem[0] = which;
                }
            });
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    selectedCategory = items[checkedItem[0]];
                    edtCategory.setText(selectedCategory);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }

    private void showPeopleDialog() {
        final String[] items = getResources().getStringArray(R.array.people);
        final int[] checkedItem = {2};
        for (int ii = 0; ii < items.length; ii++) {
            if (items[ii].equals(selectedPeople)) {
                checkedItem[0] = ii;
                break;
            }
        }
        if (getContext() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Choose People Go With");
            builder.setSingleChoiceItems(items, checkedItem[0], new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    checkedItem[0] = which;
                }
            });
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    selectedPeople = items[checkedItem[0]];
                    edtPeople.setText(selectedPeople);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }


    // end main class
}
