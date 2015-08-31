package org.destil.gpsaveraging.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * App preferences.
 */
@Singleton
public class Preferences {

    public static final String UNITS_METRIC = "metric";
    public static final String UNITS_IMPERIAL = "imperial";
    public static final String COORDS_DECIMAL = "decimal";
    public static final String COORDS_MINUTES = "minutes";
    public static final String COORDS_SECONDS = "seconds";
    public static final String UNITS = "UNITS";
    public static final String COORDINATE_FORMAT = "COORDINATE_FORMAT";
    public static final String UNITS_DEFAULT_VALUE = UNITS_METRIC;
    public static final String[] UNITS_VALUES = {UNITS_METRIC, UNITS_IMPERIAL};
    public static final String COORDS_DEFAULT_VALUE = COORDS_MINUTES;
    public static final String[] COORDS_VALUES = {COORDS_DECIMAL, COORDS_MINUTES, COORDS_SECONDS};
    public static final String[] COORDS_OPTIONS = {"dd.ddddd", "N dd° mm.mmm'", "N dd° mm' ss.sss''"};

    private final Context mContext;

    @Inject
    public Preferences(Context context) {
        mContext = context;
    }

    /**
     * Returns preferred unit format.
     */
    public String getUnitsFormat() {
        return getString(UNITS, UNITS_DEFAULT_VALUE);
    }

    /**
     * Returns preferred coordinate format.
     */
    public String getCoordinateFormat() {
        return getString(COORDINATE_FORMAT, COORDS_DEFAULT_VALUE);
    }

    private String getString(String key, String defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return prefs.getString(key, defaultValue);
    }
}
