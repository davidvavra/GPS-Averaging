/*
 * Copyright 2015 David Vávra
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.destil.gpsaveraging.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Manages persistent storage.
 *
 * @author David Vávra (david@vavra.me)
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
    public static final String FULL_VERSION = "full_version";

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

    public void setFullVersion(boolean isFullVersion) {
        set(FULL_VERSION, isFullVersion);
    }

    public boolean isFullVersion() {
        return getBoolean(FULL_VERSION);
    }

    private String getString(String key, String defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return prefs.getString(key, defaultValue);
    }

    private boolean getBoolean(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return prefs.getBoolean(key, false);
    }

    private void set(String key, boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        prefs.edit().putBoolean(key, value).apply();
    }
}
