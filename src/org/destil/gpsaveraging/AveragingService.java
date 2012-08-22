package org.destil.gpsaveraging;

import java.util.Timer;
import java.util.TimerTask;

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
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

public class AveragingService extends Service implements LocationListener {

	private static final int MEASUREMENT_DELAY = 2000; // delay between
														// measurements
	public static final String INTENT_ACTION = "org.destil.gpsaveraging.LocationUpdate";
	public static final String EXTRA_LOCATION = "org.destil.gpsaveraging.LOCATION";
	public static boolean isRunning = false;
	private Measurements measurements;
	private LocationManager locationManager;
	private Timer timer;

	@Override
	public void onCreate() {
		super.onCreate();
		measurements = Measurements.getInstance();
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// no binding
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// start averaging
		startAveraging();
		// keep GPS running
		locationManager.requestLocationUpdates(MainActivity.GPS, 0, 0, this);
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		if (timer != null) {
			timer.cancel();
		}
		stopForeground(true);
		locationManager.removeUpdates(this);
		super.onDestroy();
	}

	/**
	 * Starts active averaging.
	 */
	public void startAveraging() {
		isRunning = true;
		showNotification();
		measurements.clean();
		timer = new Timer();
		final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
		final Intent intent = new Intent(INTENT_ACTION);
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				Location location = locationManager.getLastKnownLocation("gps");
				if (location != null) {
					measurements.add(locationManager.getLastKnownLocation("gps"));
					intent.putExtra(EXTRA_LOCATION, measurements.getAveragedLocation());
					broadcastManager.sendBroadcast(intent);
				}
			}
		}, 0, MEASUREMENT_DELAY);
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

}
