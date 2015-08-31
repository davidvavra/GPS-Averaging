package org.destil.gpsaveraging.util;

import android.app.Activity;
import android.support.annotation.StringRes;
import android.view.View;

/**
 * Toast utils.
 */
public class Snackbar {
    public static void show(View layout, @StringRes int stringRes) {
        android.support.design.widget.Snackbar.make(layout, stringRes, android.support.design.widget.Snackbar.LENGTH_LONG)
                .show();
    }

    public static void show(Activity activity, @StringRes int stringRes) {
        show(activity.findViewById(android.R.id.content), stringRes);
    }
}
