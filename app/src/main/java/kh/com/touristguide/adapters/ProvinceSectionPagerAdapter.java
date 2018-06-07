package kh.com.touristguide.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import kh.com.touristguide.fragments.ImageSliderFragment;

public class ProvinceSectionPagerAdapter extends FragmentPagerAdapter {

    private Fragment[] fragments = {
            new ImageSliderFragment(), new ImageSliderFragment()
    };

    public ProvinceSectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "INFO";
            case 1:
                return "PLACES";
            default:
                return null;
        }
    }
}
