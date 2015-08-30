package org.destil.gpsaveraging.util;

import android.widget.Toast;

import org.destil.gpsaveraging.App;

/**
 * Toast utils.
 */
public class Toas {
    public static void t(int stringRes) {
        Toast.makeText(App.get(), stringRes, Toast.LENGTH_LONG).show();
    }
}
