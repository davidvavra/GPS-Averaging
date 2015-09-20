package org.destil.gpsaveraging.util;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * TODO: add documentation
 *
 * @author David Vávra (david@vavra.me)
 */
public class PackageUtils {
    public static String getAppVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "Unknown";
        }
    }
}
