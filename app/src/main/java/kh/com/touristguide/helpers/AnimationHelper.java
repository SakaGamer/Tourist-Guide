package kh.com.touristguide.helpers;

import android.view.View;
import android.view.animation.TranslateAnimation;

public class AnimationHelper {

    public static void hideView(View view, int height) {
//        view.setVisibility(View.GONE);
        TranslateAnimation animation = new TranslateAnimation(
                0, 0, 0, -height);
        animation.setDuration(500);
        view.startAnimation(animation);
        view.setVisibility(View.GONE);
    }

    public static void showView(View view, int height) {
//        view.setVisibility(View.VISIBLE);
        TranslateAnimation animation = new TranslateAnimation(
                0, 0, -height, 0);
        animation.setDuration(500);
        view.startAnimation(animation);
        view.setVisibility(View.VISIBLE);
    }

    public static void pushDown(View view, int height) {
        TranslateAnimation animation = new TranslateAnimation(
                0, 0, -height, 0);
        animation.setDuration(500);
        view.startAnimation(animation);
    }

    public static void pushUp(View view, int height) {
        TranslateAnimation animation = new TranslateAnimation(
                0, 0, height, 0);
        animation.setDuration(500);
        view.startAnimation(animation);
    }

}
