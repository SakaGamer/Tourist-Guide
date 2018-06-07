package kh.com.touristguide.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextSwitcher;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

import kh.com.touristguide.R;
import kh.com.touristguide.helpers.ConstantValue;
import kh.com.touristguide.helpers.KeyboardHelper;
import kh.com.touristguide.models.Feedback;

public class FeedBackActivity extends AppCompatActivity implements View.OnClickListener {

    TextSwitcher textSwitcher;
    EditText edtFeedback;
    Button btnSubmit;
    FirebaseFirestore firestore;
    TextSwitcher textResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        setTitle(R.string.feedback);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnSubmit = findViewById(R.id.feedback_btn_submit);
        edtFeedback = findViewById(R.id.feedback_edt_feedback);
        textSwitcher = findViewById(R.id.feedback_txt_switcher);
        textResponse = findViewById(R.id.feedback_txt_switcher_response);
        firestore = FirebaseFirestore.getInstance();

        btnSubmit.setOnClickListener(this);

        // Animation slide in left
        Animation textAnimationIn = AnimationUtils.
                loadAnimation(this, android.R.anim.slide_in_left);
        textAnimationIn.setDuration(1000);
        // Animation slide out right
        Animation textAnimationOut = AnimationUtils.
                loadAnimation(this, android.R.anim.slide_out_right);
        textAnimationOut.setDuration(1000);
        // Set animation to text switcher
        textSwitcher.setInAnimation(textAnimationIn);
        textSwitcher.setOutAnimation(textAnimationOut);

        // Animation slide in up
        Animation animationIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
        animationIn.setDuration(1000);
        // Set animation to text switcher response
        textResponse.setInAnimation(animationIn);
    }

    @Override
    public void onClick(View v) {
        // Hide input method
        KeyboardHelper.hide(getApplicationContext(), getCurrentFocus());
        // Get text from edit text field
        if (edtFeedback.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Write your feedback",
                    Toast.LENGTH_SHORT).show();
        } else {
            edtFeedback.clearFocus();
            btnSubmit.setEnabled(false);
            edtFeedback.setAlpha(0.4f);
            btnSubmit.setAlpha(0.4f);
            textSwitcher.setCurrentText("Sending...");
            textResponse.setCurrentText("");

            // Create Feedback object to write to db
            Feedback feedback = new Feedback();
            String id = firestore.collection(ConstantValue.FEEDBACK).document().getId();
            String commentFeedback = edtFeedback.getText().toString();
            Timestamp timestamp = new Timestamp(new Date());

            feedback.setId(id);
            feedback.setCommentFeedback(commentFeedback);
            feedback.setTimestamp(timestamp);

            // add Feedback object to db
            firestore.collection(ConstantValue.FEEDBACK).document(id).set(feedback)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            textSwitcher.setText("Feedback Sent");
                            textResponse.setText("Thanks for your feedback to our organization. " +
                                    "We will try to review your feedback carefully.");
                            edtFeedback.setAlpha(1);
                            edtFeedback.setText("");
                            btnSubmit.setAlpha(1);
                            btnSubmit.setEnabled(true);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("app", "onFailure: " + e.getMessage());
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    // end - finished
}
