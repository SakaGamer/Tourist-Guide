package kh.com.touristguide.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import kh.com.touristguide.R;
import kh.com.touristguide.helpers.ConstantValue;
import kh.com.touristguide.models.Place;

public class SuggestNewPlaceActivity extends AppCompatActivity implements View.OnClickListener {

    private ContentLoadingProgressBar progressBar;
    private EditText edtPlaceName, edtCategories, edtProvince, edtDescription;
    private LinearLayout formLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest_new_place);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        edtPlaceName = findViewById(R.id.suggest_new_edt_place_name);
        edtCategories = findViewById(R.id.suggest_new_edt_category);
        edtProvince = findViewById(R.id.suggest_new_edt_province);
        edtDescription = findViewById(R.id.suggest_new_edt_description);
        progressBar = findViewById(R.id.progress_bar);
        formLayout = findViewById(R.id.suggest_new_field_layout);
        Button btnSubmit = findViewById(R.id.suggest_new_btn_submit);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.suggest_new_btn_submit) {
            submitPlace();
        }
    }

    private void submitPlace() {
        progressBar.show();
        if (!validForm()) {
            progressBar.hide();
            return;
        }
        disableEnableView(false, formLayout);
        String name = edtPlaceName.getText().toString().trim();
        String province = edtProvince.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String id = firestore.collection(ConstantValue.SUGGESTED_BY_USER).document().getId();
        Place place = new Place(id, name, description, province, getCategories());
        firestore.collection(ConstantValue.SUGGESTED_BY_USER).document(id).set(place)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        disableEnableView(true, formLayout);
                        clearForm();
                        progressBar.hide();
                        Toast.makeText(getApplicationContext(), "Submitted, Thanks",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.hide();
                        disableEnableView(true, formLayout);
                        Toast.makeText(getApplicationContext(), "Error submitting place",
                                Toast.LENGTH_SHORT).show();
                        Log.e("app", "submitPlace: ", e.fillInStackTrace());
                    }
                });
    }

    private Map<String, Boolean> getCategories() {
        String category = edtCategories.getText().toString().trim();
        Map<String, Boolean> cats = new HashMap<>();
        if (!category.isEmpty()) {
            String[] categories = category.split(",");
            cats = new HashMap<>();
            for (String string : categories) {
                cats.put(string, true);
            }
        }
        return cats;
    }

    private void disableEnableView(boolean enable, ViewGroup viewGroup) {
        for (int ii = 0; ii < viewGroup.getChildCount(); ii++) {
            View child = viewGroup.getChildAt(ii);
            child.setEnabled(enable);
            if (enable) {
                child.setAlpha(1);
            } else {
                child.setAlpha(0.3f);
            }
            if (child instanceof ViewGroup) {
                disableEnableView(enable, (ViewGroup) child);
            }
        }
    }

    private void clearForm() {
        edtPlaceName.setText("");
        edtCategories.setText("");
        edtProvince.setText("");
        edtDescription.setText("");
    }

    private boolean validForm() {
        boolean valid = false;
        String name = edtPlaceName.getText().toString().trim();
        if (name.isEmpty()) {
            edtPlaceName.setError(getString(R.string.require));
        } else {
            edtPlaceName.setError(null);
            valid = true;
        }
        String categories = edtCategories.getText().toString().trim();
        if (categories.isEmpty()) {
            edtCategories.setError(getString(R.string.require));
        } else {
            edtCategories.setError(null);
            valid = true;
        }
        String province = edtProvince.getText().toString().trim();
        if (province.isEmpty()) {
            edtProvince.setError(getString(R.string.require));
        } else {
            edtProvince.setError(null);
            valid = true;
        }
        return valid;
    }
}
