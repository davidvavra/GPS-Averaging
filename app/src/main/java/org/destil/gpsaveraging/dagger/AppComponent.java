package org.destil.gpsaveraging.dagger;

import org.destil.gpsaveraging.MainActivity;
import org.destil.gpsaveraging.measure.PeriodicService;
import org.destil.gpsaveraging.ui.view.AverageLocationCardView;
import org.destil.gpsaveraging.ui.view.LocationCardView;
import org.destil.gpsaveraging.ui.fragment.MainFragment;

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

    void injectToMainFragment(MainFragment mainFragment);
}
