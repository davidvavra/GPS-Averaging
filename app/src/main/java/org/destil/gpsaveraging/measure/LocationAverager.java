package org.destil.gpsaveraging.measure;

import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.squareup.otto.Bus;

import org.destil.gpsaveraging.location.GpsObserver;
import org.destil.gpsaveraging.measure.event.AveragedLocationEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Controls the measurements.
 */
@Singleton
public class LocationAverager {

    private final Context mContext;
    private final Measurements mMeasurements;
    private final Bus mBus;
    private final GpsObserver mGps;
    private boolean mRunning;

    @Inject
    public LocationAverager(Context context, Measurements measurements, Bus bus, GpsObserver gpsObserver) {
        mContext = context;
        mMeasurements = measurements;
        mBus = bus;
        mGps = gpsObserver;
    }

    public void start() {
        mRunning = true;
        mMeasurements.clean();
        measureLocation();
        mContext.startService(new Intent(mContext, PeriodicService.class));
    }

    public void stop() {
        mRunning = false;
        mContext.stopService(new Intent(mContext, PeriodicService.class));
    }

    @SuppressWarnings("ResourceType")
    void measureLocation() {
        Location location = mGps.getLastLocation();
        if (location != null) {
            mMeasurements.add(location);
            mBus.post(new AveragedLocationEvent(mMeasurements.getAveragedLocation()));
        }
    }

    public boolean isRunning() {
        return mRunning;
    }
}
