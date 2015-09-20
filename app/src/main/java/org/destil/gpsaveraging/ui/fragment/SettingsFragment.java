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

package org.destil.gpsaveraging.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;

import org.destil.gpsaveraging.R;
import org.destil.gpsaveraging.data.Preferences;

/**
 * Fragment containing preferences.
 *
 * @author David Vávra (david@vavra.me)
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    // UI elements
    private ListPreference uiUnits;
    private ListPreference uiCoordinateFormat;

    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        addPreferencesFromResource(R.xml.fragment_preferences);
        uiUnits = (ListPreference) findPreference(Preferences.UNITS);
        uiCoordinateFormat = (ListPreference) findPreference(Preferences.COORDINATE_FORMAT);
        init();
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences preferences = getPreferenceScreen().getSharedPreferences();
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (key.equals(Preferences.UNITS)) {
            if (uiUnits.getEntry() != null) {
                uiUnits.setSummary(uiUnits.getEntry());
            }
        } else if (key.equals(Preferences.COORDINATE_FORMAT)) {
            if (uiCoordinateFormat.getEntry() != null) {
                uiCoordinateFormat.setSummary(uiCoordinateFormat.getEntry());
            }
        }
    }

    /**
     * Inits settings according to constants.
     */
    private void init() {
        // units
        uiUnits.setEntryValues(Preferences.UNITS_VALUES);
        String[] UNITS_OPTIONS = new String[]{getString(R.string.metric), getString(R.string.imperial)};
        uiUnits.setEntries(UNITS_OPTIONS);
        uiUnits.setDefaultValue(Preferences.UNITS_DEFAULT_VALUE);
        uiUnits.setSummary(UNITS_OPTIONS[0]);
        onSharedPreferenceChanged(null, Preferences.UNITS);
        // coordinate formats
        uiCoordinateFormat.setEntryValues(Preferences.COORDS_VALUES);
        uiCoordinateFormat.setEntries(Preferences.COORDS_OPTIONS);
        uiCoordinateFormat.setDefaultValue(Preferences.COORDS_DEFAULT_VALUE);
        uiCoordinateFormat.setSummary(Preferences.COORDS_OPTIONS[1]);
        onSharedPreferenceChanged(null, Preferences.COORDINATE_FORMAT);
    }
}
