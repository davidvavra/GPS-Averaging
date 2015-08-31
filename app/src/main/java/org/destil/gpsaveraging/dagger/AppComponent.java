package org.destil.gpsaveraging.dagger;

import org.destil.gpsaveraging.MainActivity;
import org.destil.gpsaveraging.measure.PeriodicService;
import org.destil.gpsaveraging.ui.AverageLocationCardView;
import org.destil.gpsaveraging.ui.LocationCardView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Main Dagger component.
 */
@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void injectToMainActivity(MainActivity mainActivity);

    void injectToAveragingService(PeriodicService periodicService);

    void injectToLocationCardView(LocationCardView locationCardView);

    void injectToAverageLocationCardView(AverageLocationCardView averageLocationCardView);
}
