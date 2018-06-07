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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import kh.com.touristguide.R;
import kh.com.touristguide.helpers.KeyboardHelper;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private EditText edtEmail, edtPassword;
    private TextView textCreateAccount;
    private Button btnSignIn;
    private ImageView imgEye;
    private FirebaseAuth auth;
    private ContentLoadingProgressBar progressBar;
    private ContentLoadingProgressBar progressBarCircle;

    private boolean showPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        auth = FirebaseAuth.getInstance();

        textCreateAccount = findViewById(R.id.sign_in_txt_create_account);
        btnSignIn = findViewById(R.id.sign_in_btn_sign_in);
        edtEmail = findViewById(R.id.sign_in_edt_email);
        edtPassword = findViewById(R.id.sign_in_edt_password);
        imgEye = findViewById(R.id.sign_in_img_eye);
        progressBar = findViewById(R.id.sign_in_progress_bar);
        progressBarCircle = findViewById(R.id.progress_bar);
        RelativeLayout relativeLayout = findViewById(R.id.sign_in_layout);

        textCreateAccount.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
        relativeLayout.setOnClickListener(this);
        imgEye.setOnClickListener(this);
        edtPassword.addTextChangedListener(this);

        ScrollView scrollView = findViewById(R.id.sign_in_scroll_layout);
        scrollView.setSmoothScrollingEnabled(true);

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
        if (clickId == R.id.sign_in_btn_sign_in) {
            // Hide keyboard
            KeyboardHelper.hide(getApplicationContext(), getCurrentFocus());
            // validate input
            if (!validateForm()) {
                imgEye.setVisibility(View.GONE);
                return;
            }
            // Disable form
            disableForm();
            // Sign user in
            String email = edtEmail.getText().toString();
            String password = edtPassword.getText().toString();
            signIn(email, password);
        } else if (clickId == R.id.sign_in_txt_create_account) {
            // Go to sign up activity and close this activity
            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else if (clickId == R.id.sign_in_layout) {
            Log.d("app", "onClick: ");
            KeyboardHelper.hide(getApplicationContext(), getCurrentFocus());
        } else if (clickId == R.id.sign_in_img_eye) {
            togglePassword();
        }
    }

    private void togglePassword(){
        showPassword = !showPassword;
        if (showPassword) {
            edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        edtPassword.setSelection(edtPassword.length());
    }

    private void signIn(String email, String password) {
        progressBar.show();
        progressBarCircle.show();
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                        progressBar.hide();
                        progressBarCircle.hide();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Sign in error, Incorrect email or password",
                                Toast.LENGTH_LONG).show();
                        progressBar.hide();
                        progressBarCircle.hide();
                        Log.e("app", e.getMessage());
                        enableForm();
                    }
                });
    }

    private void toggleEye(){
        if(edtPassword.getText().toString().trim().isEmpty()){
            imgEye.setVisibility(View.GONE);
        } else {
            imgEye.setVisibility(View.VISIBLE);
        }
    }

    private void disableForm() {
        edtEmail.setEnabled(false);
        edtEmail.setAlpha(0.4f);
        edtPassword.setEnabled(false);
        edtPassword.setAlpha(0.4f);
        btnSignIn.setEnabled(false);
        btnSignIn.setAlpha(0.4f);
        textCreateAccount.setEnabled(false);
        textCreateAccount.setAlpha(0.4f);
        imgEye.setEnabled(false);
        imgEye.setAlpha(0.4f);
    }

    private void enableForm() {
        edtEmail.setEnabled(true);
        edtEmail.setAlpha(1);
        edtPassword.setEnabled(true);
        edtPassword.setAlpha(1);
        btnSignIn.setEnabled(true);
        btnSignIn.setAlpha(1);
        textCreateAccount.setEnabled(true);
        textCreateAccount.setAlpha(1);
        imgEye.setEnabled(true);
        imgEye.setAlpha(1f);
    }

    private boolean validateForm() {
        boolean valid = true;
        String error = getString(R.string.require);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}

