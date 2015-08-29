package org.destil.gpsaveraging.location.event;

import android.location.Location;

public class CurrentLocationEvent {
    private final Location location;

    public CurrentLocationEvent(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
