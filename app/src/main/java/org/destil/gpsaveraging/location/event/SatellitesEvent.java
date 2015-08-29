package org.destil.gpsaveraging.location.event;

public class SatellitesEvent {

    private final int count;

    public SatellitesEvent(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
