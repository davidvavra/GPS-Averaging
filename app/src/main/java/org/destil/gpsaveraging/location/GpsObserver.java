package org.destil.gpsaveraging.location;

import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

import org.destil.gpsaveraging.App;
import org.destil.gpsaveraging.location.event.CurrentLocationEvent;
import org.destil.gpsaveraging.location.event.FirstFixEvent;
import org.destil.gpsaveraging.location.event.GpsNotAvailableEvent;
import org.destil.gpsaveraging.location.event.SatellitesEvent;

/**
 * Helper for accessing GPS.
 */
public class GpsObserver implements GpsStatus.Listener, LocationListener {

    private static GpsObserver sInstance;

    private LocationManager locationManager;

    private boolean hasFix = false;

    private GpsObserver() {
    }

    public static GpsObserver getInstance() {
        if (sInstance == null) {
            sInstance = new GpsObserver();
        }
        return sInstance;
    }

    @SuppressWarnings("ResourceType")
    public void start() {
        if (locationManager == null) {
            hasFix = false;
            locationManager = (LocationManager) App.get().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates("gps", 0, 0, this);
            locationManager.addGpsStatusListener(this);
        }
    }

    @SuppressWarnings("ResourceType")
    public void stop() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
            locationManager.removeGpsStatusListener(this);
        }
    }

    public boolean hasFix() {
        return hasFix;
    }

    @Override
    public void onGpsStatusChanged(int event) {
        if (locationManager == null) {
            return;
        }
        if (event == GpsStatus.GPS_EVENT_FIRST_FIX) {
            hasFix = true;
            App.bus().post(new FirstFixEvent());
        } else if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
            GpsStatus gpsStatus = locationManager.getGpsStatus(null);
            int all = 0;
            Iterable<GpsSatellite> satellites = gpsStatus.getSatellites();
            for (GpsSatellite satellite : satellites) {
                all++;
            }
            App.bus().post(new SatellitesEvent(all));
        } else if (event == GpsStatus.GPS_EVENT_STOPPED) {
            hasFix = false;
            App.bus().post(new GpsNotAvailableEvent());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (!hasFix) {
            hasFix = true;
            App.bus().post(new FirstFixEvent());
        }
        App.bus().post(new CurrentLocationEvent(location));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (status == LocationProvider.AVAILABLE) {
            hasFix = true;
            App.bus().post(new FirstFixEvent());
        } else {
            hasFix = false;
            App.bus().post(new GpsNotAvailableEvent());
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        // ignore
    }

    @Override
    public void onProviderDisabled(String provider) {
        hasFix = false;
        App.bus().post(new GpsNotAvailableEvent());
    }

    public Location getLastLocation() {
        if (locationManager != null) {
            return locationManager.getLastKnownLocation("gps");
        }
        return null;
    }
}
