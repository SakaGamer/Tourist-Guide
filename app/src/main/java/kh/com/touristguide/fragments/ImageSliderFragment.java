package kh.com.touristguide.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.NetworkImageView;

import kh.com.touristguide.R;


public class ImageSliderFragment extends Fragment {


    public ImageSliderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_image_slider, container, false);
//        NetworkImageView networkImageView = rootView.findViewById(R.id.image_slider_img);
        return rootView;
    }

}
