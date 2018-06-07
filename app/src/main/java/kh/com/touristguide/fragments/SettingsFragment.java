package kh.com.touristguide.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import kh.com.touristguide.R;
import kh.com.touristguide.helpers.ConstantValue;


public class SettingsFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings_preference);

//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.contentEquals(ConstantValue.PREF_KEY_LANGUAGE)){
            Preference languagePref = findPreference(key);
            languagePref.setSummary(sharedPreferences.getString(ConstantValue.PREF_KEY_LANGUAGE, ""));
        }
        if(key.contentEquals(ConstantValue.PREF_KEY_NIGHT_MODE)){
            sharedPreferences.edit().putString(ConstantValue.PREF_KEY_NIGHT_MODE, "").apply();
            Preference languagePref = findPreference(key);
            languagePref.setSummary(sharedPreferences.getString(ConstantValue.PREF_KEY_LANGUAGE, ""));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

//    @Override
//    protected void attachBaseContext(Context newBase) {
//        final Resources res = newBase.getResources();
//        final Configuration config = res.getConfiguration();
//        config.setLocale(getLocale()); // getLocale() should return a Locale
//        final Context newContext = newBase.createConfigurationContext(config);
//        super.attachBaseContext(newContext);
//    }
//
//switch language
//    attachBaseContext(this);
//    recreate();

    // end
}
