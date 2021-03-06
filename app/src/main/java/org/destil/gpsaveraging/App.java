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
package org.destil.gpsaveraging;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import org.destil.gpsaveraging.dagger.AppComponent;
import org.destil.gpsaveraging.dagger.AppModule;
import org.destil.gpsaveraging.dagger.DaggerAppComponent;

import io.fabric.sdk.android.Fabric;

/**
 * Main application class.
 *
 * @author David Vávra (vavra@avast.com)
 */
public class App extends Application {

    private static AppComponent sComponent;

    public static AppComponent component() {
        return sComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
        sComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    }
}
