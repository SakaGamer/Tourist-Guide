package kh.com.touristguide.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;

import java.util.Locale;

public class LocaleHelper {

    public static void setLocale(Context context, String language) {
        updateResources(context, language);
    }

    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale);
            context = context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
        persistLanguage(context, language);
        return context;
    }

    public static String getPersistedLanguage(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(ConstantValue.SELECTED_LANGUAGE, "");
    }

    public static void persistLanguage(Context context, String language) {
        SharedPreferences languagePref = PreferenceManager.getDefaultSharedPreferences(context);
        languagePref.edit().putString(ConstantValue.SELECTED_LANGUAGE, language).apply();
    }

    public static void changeLanguage(Context context, String language){
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        LocaleHelper.persistLanguage(context, language);
    }

}
