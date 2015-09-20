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
