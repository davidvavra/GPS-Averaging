package org.destil.gpsaveraging.measure;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;

import org.destil.gpsaveraging.App;
import org.destil.gpsaveraging.MainActivity;
import org.destil.gpsaveraging.R;
import org.destil.gpsaveraging.measure.event.AveragedLocationEvent;

import java.util.Timer;
import java.util.TimerTask;

public class AveragingService extends Service implements LocationListener {

    // measurements
    private static final int MEASUREMENT_DELAY = 2000; // delay between

    private static boolean isRunning = false;
    private LocationManager mLocationManager;
    private Timer mTimer;

    @Override
    public void onCreate() {
        super.onCreate();
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // no binding
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // start averaging
        startPeriodicMeasurements();
        // keep GPS running
        mLocationManager.requestLocationUpdates("gps", 0, 0, this);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        stopForeground(true);
        mLocationManager.removeUpdates(this);
        super.onDestroy();
    }

    public static void start() {
        isRunning = true;
        Measurements.getInstance().clean();
        measureLocation();
        App.get().startService(new Intent(App.get(), AveragingService.class));
    }

    public static void stop() {
        isRunning = false;
        App.get().stopService(new Intent(App.get(), AveragingService.class));
    }

    /**
     * Starts active averaging.
     */
    private void startPeriodicMeasurements() {
        showNotification();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                measureLocation();
            }
        }, MEASUREMENT_DELAY, MEASUREMENT_DELAY);
    }

    private static void measureLocation() {
        LocationManager locationManager = (LocationManager) App.get().getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation("gps");
        if (location != null) {
            Measurements measurements = Measurements.getInstance();
            measurements.add(location);
            App.bus().post(new AveragedLocationEvent(measurements.getAveragedLocation()));
        }
    }

    /**
     * Shows notification and launches ONGOING mode.
     */
    private void showNotification() {
        PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        Notification notification = new NotificationCompat.Builder(this).setOngoing(true)
                .setContentTitle(getString(R.string.averaging_running)).setSmallIcon(R.drawable.ic_stat_notification)
                .setContentIntent(intent).setContentText(getString(R.string.tap_to_control_it)).build();
        startForeground(42, notification);
    }

    @Override
    public void onLocationChanged(Location location) {
        // ignore
    }

    @Override
    public void onProviderDisabled(String provider) {
        // ignore
    }

    @Override
    public void onProviderEnabled(String provider) {
        // ignore
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // ignore
    }

    public static boolean isRunning() {
        return isRunning;
    }

}
