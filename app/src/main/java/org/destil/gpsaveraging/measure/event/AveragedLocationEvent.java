package org.destil.gpsaveraging.measure.event;

import android.location.Location;

public class AveragedLocationEvent {
    private final Location location;

    public AveragedLocationEvent(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
