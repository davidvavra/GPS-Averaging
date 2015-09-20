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

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

/**
 * Service which periodically launches averaging in the background.
 *
 * @author David Vávra (david@vavra.me)
 */
public class PeriodicService extends Service implements LocationListener {

    private static final int MEASUREMENT_DELAY = 2000; // delay between
    @Inject
    LocationAverager mAverager;
    private LocationManager mLocationManager;
    private Timer mTimer;

    @Override
    public void onCreate() {
        super.onCreate();
        App.component().injectToAveragingService(this);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // no binding
        return null;
    }

    @SuppressWarnings("ResourceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // start averaging
        startPeriodicMeasurements();
        // keep GPS running
        try {
            mLocationManager.requestLocationUpdates("gps", 0, 0, this);
        } catch (SecurityException e) {
            // User has disabled location permission in settings while averaging.
            stopSelf();
            return START_NOT_STICKY;
        }
        return START_STICKY;
    }

    @SuppressWarnings("ResourceType")
    @Override
    public void onDestroy() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        stopForeground(true);
        mLocationManager.removeUpdates(this);
        super.onDestroy();
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
                mAverager.measureLocation();
            }
        }, MEASUREMENT_DELAY, MEASUREMENT_DELAY);
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
