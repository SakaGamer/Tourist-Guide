package kh.com.touristguide.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import kh.com.touristguide.fragments.ImageSliderFragment;

public class ImageSlidePagerAdapter extends FragmentStatePagerAdapter {

    public ImageSlidePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new ImageSliderFragment();
    }

    @Override
    public int getCount() {
        return 5;
    }
}
