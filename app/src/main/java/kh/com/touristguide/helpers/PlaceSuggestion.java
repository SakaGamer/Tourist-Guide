package kh.com.touristguide.helpers;

import android.app.SearchManager;
import android.database.MatrixCursor;
import android.provider.BaseColumns;

import java.util.List;

import kh.com.touristguide.models.Place;

public class PlaceSuggestion {

    private static final PlaceSuggestion placeSuggestion = new PlaceSuggestion();

    public static PlaceSuggestion getAdapter() {
        return placeSuggestion;
    }

    private String[] matrixCursor = {
            BaseColumns._ID,
            SearchManager.SUGGEST_COLUMN_TEXT_1,
            SearchManager.SUGGEST_COLUMN_TEXT_2,
            SearchManager.SUGGEST_COLUMN_INTENT_DATA
    };

    public MatrixCursor getSuggest(String query, List<Place> placeList) {
        MatrixCursor searchResultMatrixCursor = new MatrixCursor(matrixCursor);
        int count = 0;
        for (Place place : placeList) {
            if (place.getName().toLowerCase().contains(query.toLowerCase())) {
                count ++;
                String[] temp = {
                        String.valueOf(count),
                        place.getName(),
                        place.getProvince(),
                        place.getName()
                };
                searchResultMatrixCursor.addRow(temp);
                if(count == 5) return searchResultMatrixCursor;
            }
        }
        return searchResultMatrixCursor;
    }


}
