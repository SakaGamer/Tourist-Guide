package kh.com.touristguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import kh.com.touristguide.R;
import kh.com.touristguide.helpers.ConstantValue;
import kh.com.touristguide.helpers.KeyboardHelper;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private EditText edtUsername, edtEmail, edtPassword;
    private Button btnCreateAccount;
    private TextView txtGoSignIn;
    private ImageView imgEye;
    private ContentLoadingProgressBar progressBar;
    private ContentLoadingProgressBar progressBarCircle;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private boolean showPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        txtGoSignIn = findViewById(R.id.sign_up_txt_go_sign_in);
        btnCreateAccount = findViewById(R.id.sign_up_btn_create_account);
        edtUsername = findViewById(R.id.sign_up_edt_username);
        edtEmail = findViewById(R.id.sign_up_edt_email);
        edtPassword = findViewById(R.id.sign_up_edt_password);
        imgEye = findViewById(R.id.sign_up_img_eye);
        progressBar = findViewById(R.id.sign_up_progress_bar);
        progressBarCircle = findViewById(R.id.progress_bar);
        RelativeLayout relativeLayout = findViewById(R.id.sign_up_layout);

        btnCreateAccount.setOnClickListener(this);
        txtGoSignIn.setOnClickListener(this);
        relativeLayout.setOnClickListener(this);
        imgEye.setOnClickListener(this);
        edtPassword.addTextChangedListener(this);

        toggleEye();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            onBackPressed();
//            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//            finish();
        }
    }

    @Override
    public void onClick(View v) {
        int clickId = v.getId();
        if (clickId == R.id.sign_up_btn_create_account) {
            // Hide keyboard
            KeyboardHelper.hide(getApplicationContext(), getCurrentFocus());
            // Validate input
            if (!validateForm()) {
                imgEye.setVisibility(View.GONE);
                return;
            }
            // Disable form from double submit
            disableForm();
            // Create user
            final String username = edtUsername.getText().toString();
            String email = edtEmail.getText().toString();
            String password = edtPassword.getText().toString();
            signUp(username, email, password);
        } else if (clickId == R.id.sign_up_txt_go_sign_in) {
            // Go to sign in activity and close this activity
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else if (clickId == R.id.sign_up_layout) {
            KeyboardHelper.hide(getApplicationContext(), getCurrentFocus());
        } else if (clickId == R.id.sign_up_img_eye) {
            togglePassword();
        }
    }

    private void togglePassword() {
        showPassword = !showPassword;
        if (showPassword) {
            edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        edtPassword.setSelection(edtPassword.length());
    }

    private void signUp(final String username, String email, String password) {
        progressBar.show();
        progressBarCircle.show();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        updateUsername(authResult.getUser(), username);
                        writeUserToDb(authResult.getUser());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Create Account Error",
                                Toast.LENGTH_LONG).show();
                        progressBar.hide();
                        progressBarCircle.hide();
                        enableForm();
                        Log.w("app", e.getMessage());
                    }
                });
    }

    private void toggleEye() {
        if (edtPassword.getText().toString().trim().isEmpty()) {
            imgEye.setVisibility(View.GONE);
        } else {
            imgEye.setVisibility(View.VISIBLE);
        }
    }

    private void updateUsername(FirebaseUser user, String username) {
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(username).build();
        user.updateProfile(profileChangeRequest)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("app", "User profile updated");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.hide();
                        progressBarCircle.hide();
                        Log.w("app", e.getMessage());
                    }
                });
    }

    private void writeUserToDb(FirebaseUser user) {
        firestore.collection(ConstantValue.USERS).document(user.getUid()).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.hide();
                        progressBarCircle.hide();
                        Toast.makeText(getApplicationContext(), "Error Write User To Database",
                                Toast.LENGTH_LONG).show();
                        Log.w("app", e.getMessage());
                    }
                });
    }

    private void disableForm() {
        edtUsername.setEnabled(false);
        edtUsername.setAlpha(0.4f);
        edtEmail.setEnabled(false);
        edtEmail.setAlpha(0.4f);
        edtPassword.setEnabled(false);
        edtPassword.setAlpha(0.4f);
        btnCreateAccount.setEnabled(false);
        btnCreateAccount.setAlpha(0.4f);
        txtGoSignIn.setEnabled(false);
        txtGoSignIn.setAlpha(0.4f);
        imgEye.setEnabled(false);
        imgEye.setAlpha(0.4f);
    }

    private void enableForm() {
        edtUsername.setEnabled(true);
        edtUsername.setAlpha(1);
        edtEmail.setEnabled(true);
        edtEmail.setAlpha(1);
        edtPassword.setEnabled(true);
        edtPassword.setAlpha(1);
        btnCreateAccount.setEnabled(true);
        btnCreateAccount.setAlpha(1);
        txtGoSignIn.setEnabled(true);
        txtGoSignIn.setAlpha(1);
        imgEye.setEnabled(true);
        imgEye.setAlpha(1f);
    }


    private boolean validateForm() {
        boolean valid = true;
        String error = getString(R.string.require);
        if (edtUsername.getText().toString().trim().isEmpty()) {
            edtUsername.setError(error);
            valid = false;
        } else {
            edtUsername.setError(null);
        }
        if (edtEmail.getText().toString().trim().isEmpty()) {
            edtEmail.setError(error);
            valid = false;
        } else {
            edtEmail.setError(null);
        }
        if (edtPassword.getText().toString().trim().isEmpty()) {
            edtPassword.setError(error);
            valid = false;
        } else if (edtPassword.getText().toString().trim().length() < 6) {
            edtPassword.setError("Password must at least 6 character");
            valid = false;
        } else {
            edtPassword.setError(null);
        }
        return valid;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        toggleEye();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        toggleEye();
    }

    @Override
    public void afterTextChanged(Editable s) {
        toggleEye();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    // end main class
}
