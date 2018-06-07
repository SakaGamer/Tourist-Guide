package kh.com.touristguide.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import kh.com.touristguide.R;
import kh.com.touristguide.adapters.ResortAdapter;
import kh.com.touristguide.helpers.ConstantValue;
import kh.com.touristguide.helpers.PlaceSuggestion;
import kh.com.touristguide.models.Place;

public class SearchResultActivity extends AppCompatActivity implements
        SearchView.OnQueryTextListener, ResortAdapter.ItemClickListener {

    private ContentLoadingProgressBar progressBar;
    private FirebaseFirestore firestore;
    private TextView textView;
    private SearchView searchView;

    private ResortAdapter resortAdapter;
    private List<Place> placeList = new ArrayList<>();
    private List<Place> tempPlaceList = new ArrayList<>();
    private CursorAdapter placeSuggestionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        setTitle("");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        firestore = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = findViewById(R.id.search_result_recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        textView = findViewById(R.id.search_result_text_view);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));
        resortAdapter = new ResortAdapter(getApplicationContext(), placeList, 1);
        resortAdapter.setOnClickListener(this);
        recyclerView.setAdapter(resortAdapter);

        placeSuggestionAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.view_holder_search_suggestion,
                null, new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_TEXT_2},
                new int[]{R.id.vh_search_suggestion_txt_title, R.id.vh_search_suggestion_txt_subtitle}, 0);

        loadAllPlace();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate option menu from XML
        getMenuInflater().inflate(R.menu.search_option_menu, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search_option_search).getActionView();
        // Assumes current activity is the searchable activity
        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.setSuggestionsAdapter(placeSuggestionAdapter);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryRefinementEnabled(true);
        searchView.setOnQueryTextListener(this);
        searchView.setIconified(false);
        onNewIntent(getIntent());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onItemClick(View view, int position) {
        Place place = tempPlaceList.get(position);
        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view,
                getString(R.string.shared_image)).toBundle();
        startActivity(new Intent(getApplicationContext(), PlaceDetailActivity.class)
                .putExtra("place", place), bundle);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
//            SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(this,
//                    MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
//            searchRecentSuggestions.saveRecentQuery(query, null);

            setTitle("Result for: " + query);
            searchView.setQuery(query, false);
            searchView.clearFocus();
            resortAdapter.setPlaces(filterPlace(query));
            Log.d("app", "ACTION_SEARCH " + query);
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            // Handle a suggestions click (because the suggestions all use ACTION_VIEW)
            if (intent.getData() != null) {
                String query = intent.getData().getLastPathSegment().toLowerCase();
                searchView.setQuery(query, false);
                searchView.clearFocus();
                resortAdapter.setPlaces(filterPlace(query));
                Log.d("app", "ACTION_VIEW " + query);
            }

        }
    }

    private void loadAllPlace() {
        firestore.collection(ConstantValue.PLACES).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        placeList.clear();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            try {
                                placeList.add(documentSnapshot.toObject(Place.class));
                            } catch (Exception e) {
                                Log.w("app", e.getMessage());
                            }
                        } // for
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("app", e.getMessage());
                    }
                });
    }

    private List<Place> filterPlace(String query) {
        progressBar.show();
        textView.setVisibility(View.VISIBLE);
        textView.setText(R.string.loading);
        tempPlaceList.clear();
        for (Place place : placeList) {
            if (place.getName().toLowerCase().contains(query.toLowerCase())) {
                tempPlaceList.add(place);
            }
        }
        textView.setVisibility(View.GONE);
        progressBar.hide();
        return tempPlaceList;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchView.setQuery(query, false);
        searchView.clearFocus();
        Log.d("app", "onQueryTextSubmit " + query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        MatrixCursor matrixCursor = PlaceSuggestion.getAdapter().getSuggest(newText, placeList);
        placeSuggestionAdapter.swapCursor(matrixCursor);
        Log.d("app", "onQueryTextChange " + newText);
        return false;
    }

    // End activity
}
