package org.destil.gpsaveraging.data;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;

import org.destil.gpsaveraging.R;
import org.destil.gpsaveraging.measure.Measurements;
import org.destil.gpsaveraging.ui.view.Snackbar;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Collection of external Intents.
 */
@Singleton
public class Intents {

    private final Exporter mExporter;
    private final Measurements mMeasurements;

    @Inject
    public Intents(Exporter exporter, Measurements measurements) {
        mExporter = exporter;
        mMeasurements = measurements;
    }

    public void share(Activity activity) {
        try {
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, activity.getString(R.string.email_subject));
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, mExporter.toShareText());
            activity.startActivity(shareIntent);
        } catch (ActivityNotFoundException e) {
            Snackbar.show(activity, R.string.no_app_to_handle);
        }
    }

    public void showOnMap(Activity activity) {
        try {
            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:" + mMeasurements.getLatitude() +
                    ',' + mMeasurements.getLongitude() + "?q=" + mMeasurements.getLatitude() +
                    "," + mMeasurements.getLongitude() + "(" + activity.getString(R.string.email_subject) + ")"));
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Snackbar.show(activity, R.string.no_app_to_handle);
        }
    }

    public void exportToGpx(Activity activity) {
        exportToFile(activity, true /* gpx */);
    }

    public void exportToKml(Activity activity) {
        exportToFile(activity, false /* kml */);
    }

    private void exportToFile(Activity activity, boolean gpx) {
        try {
            mExporter.saveToCache(gpx);
            Uri uri = Uri.parse("content://org.destil.gpsaveraging/" + (gpx ? Exporter.GPX_FILE_NAME : Exporter.KML_FILE_NAME));

            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType(gpx ? "application/gpx+xml" : "application/vnd.google-earth.kml+xml");
            intent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.email_subject));
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Snackbar.show(activity, R.string.no_app_to_handle);
        }
    }

    /**
     * Defines Intent API & Locus integration.
     *
     * @param activity calling activity
     */
    public void answerToThirdParty(Activity activity) {
        Intent intent = activity.getIntent();
        // Intent API & Locus integration
        if (mMeasurements.size() > 0
                && intent.getAction() != null
                && (intent.getAction().equals("menion.android.locus.GET_POINT") || intent.getAction().equals(
                "cz.destil.gpsaveraging.AVERAGED_LOCATION"))) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("name", activity.getString(R.string.averaged_location));
            resultIntent.putExtra("latitude", mMeasurements.getLatitude());
            resultIntent.putExtra("longitude", mMeasurements.getLongitude());
            resultIntent.putExtra("altitude", mMeasurements.getAltitude());
            resultIntent.putExtra("accuracy", (double) mMeasurements.getAccuracy());
            activity.setResult(Activity.RESULT_OK, resultIntent);
            activity.finish();
        }
    }
}
