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

package org.destil.gpsaveraging.location;

import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import com.squareup.otto.Bus;

import org.destil.gpsaveraging.location.event.CurrentLocationEvent;
import org.destil.gpsaveraging.location.event.FirstFixEvent;
import org.destil.gpsaveraging.location.event.GpsNotAvailableEvent;
import org.destil.gpsaveraging.location.event.SatellitesEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Manages observing GPS location and reporting status.
 *
 * @author David Vávra (david@vavra.me)
 */
@Singleton
public class GpsObserver implements GpsStatus.Listener, LocationListener {

    private final Context mContext;
    private final Bus mBus;
    private LocationManager locationManager;

    private boolean hasFix = false;

    @Inject
    public GpsObserver(Context context, Bus bus) {
        mContext = context;
        mBus = bus;
    }

    @SuppressWarnings("ResourceType")
    public void start() {
        hasFix = false;
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates("gps", 0, 0, this);
        locationManager.addGpsStatusListener(this);
    }

    @SuppressWarnings("ResourceType")
    public void stop() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
            locationManager.removeGpsStatusListener(this);
        }
    }

    @Override
    public void onGpsStatusChanged(int event) {
        if (locationManager == null) {
            return;
        }
        if (event == GpsStatus.GPS_EVENT_FIRST_FIX) {
            if (!hasFix) {
                hasFix = true;
                mBus.post(new FirstFixEvent());
            }
        } else if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
            GpsStatus gpsStatus = locationManager.getGpsStatus(null);
            int all = 0;
            Iterable<GpsSatellite> satellites = gpsStatus.getSatellites();
            for (GpsSatellite ignored : satellites) {
                all++;
            }
            mBus.post(new SatellitesEvent(all));
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (!hasFix) {
            hasFix = true;
            mBus.post(new FirstFixEvent());
        }
        mBus.post(new CurrentLocationEvent(location));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (status == LocationProvider.AVAILABLE) {
            if (!hasFix) {
                hasFix = true;
                mBus.post(new FirstFixEvent());
            }
        } else {
            hasFix = false;
            if (status == LocationProvider.OUT_OF_SERVICE) {
                Log.d("GPS Observer", "1");
                mBus.post(new GpsNotAvailableEvent());
            }
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        // ignore
    }

    @Override
    public void onProviderDisabled(String provider) {
        hasFix = false;
        Log.d("GPS Observer", "2");
        mBus.post(new GpsNotAvailableEvent());
    }

    @SuppressWarnings("ResourceType")
    public Location getLastLocation() {
        if (locationManager != null) {
            return locationManager.getLastKnownLocation("gps");
        }
        return null;
    }
}
