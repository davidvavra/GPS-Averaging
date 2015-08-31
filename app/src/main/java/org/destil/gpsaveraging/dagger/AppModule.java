package org.destil.gpsaveraging.dagger;

import android.content.Context;

import com.squareup.otto.Bus;

import org.destil.gpsaveraging.App;
import org.destil.gpsaveraging.util.MainThreadBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Main Dagger module.
 */
@Module
public class AppModule {

    App mApp;

    public AppModule(App app) {
        mApp = app;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return mApp;
    }

    @Provides
    @Singleton
    Bus providerBus() {
        return new MainThreadBus();
    }
}
