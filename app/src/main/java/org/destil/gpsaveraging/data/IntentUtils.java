package org.destil.gpsaveraging.data;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;

import org.destil.gpsaveraging.App;
import org.destil.gpsaveraging.R;
import org.destil.gpsaveraging.measure.Measurements;
import org.destil.gpsaveraging.util.Snackbar;

/**
 * Collection of external Intents.
 */
public class IntentUtils {
    public static void share(Activity activity) {
        try {
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, App.get().getString(R.string.email_subject));
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, Exporter.getInstance().toShareText(Measurements.getInstance()));
            activity.startActivity(shareIntent);
        } catch (ActivityNotFoundException e) {
            Snackbar.show(activity, R.string.no_app_to_handle);
        }
    }


    public static void showOnMap(Activity activity) {
        try {
            final StringBuilder uri = new StringBuilder("geo:");
            Measurements measurements = Measurements.getInstance();
            uri.append(measurements.getLatitude())
                    .append(',')
                    .append(measurements.getLongitude())
                    .append("?q=")
                    .append(measurements.getLatitude())
                    .append(",")
                    .append(measurements.getLongitude())
                    .append("(")
                    .append(App.get().getString(R.string.email_subject))
                    .append(")");
            final Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri.toString()));
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Snackbar.show(activity, R.string.no_app_to_handle);
        }
    }

    public static void exportToGpx(Activity activity) {
        exportToFile(activity, true /* gpx */);
    }

    public static void exportToKml(Activity activity) {
        exportToFile(activity, false /* kml */);
    }

    private static void exportToFile(Activity activity, boolean gpx) {
        try {
            Exporter.getInstance().saveToCache(gpx);
            Uri uri = Uri.parse("content://org.destil.gpsaveraging/" + (gpx ? Exporter.GPX_FILE_NAME : Exporter.KML_FILE_NAME));

            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType(gpx ? "application/gpx+xml" : "application/vnd.google-earth.kml+xml");
            intent.putExtra(Intent.EXTRA_SUBJECT, App.get().getString(R.string.email_subject));
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Snackbar.show(activity, R.string.no_app_to_handle);
        }
    }
}
