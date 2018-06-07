package kh.com.touristguide.models;

import com.google.firebase.Timestamp;

public class Rating extends Feedback {

    private int star;

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }
}
