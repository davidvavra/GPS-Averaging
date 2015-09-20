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

package org.destil.gpsaveraging.dagger;

import android.content.Context;

import com.squareup.otto.Bus;

import org.destil.gpsaveraging.App;
import org.destil.gpsaveraging.util.MainThreadBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Main Dagger module providing basic dependencies. Other dependencies are created automatically through constructor injection.
 *
 * @author David Vávra (david@vavra.me)
 */
@Module
public class AppModule {

    final App mApp;

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
