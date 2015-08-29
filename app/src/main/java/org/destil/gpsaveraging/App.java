/*
   Copyright 2012 David "Destil" Vavra

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.destil.gpsaveraging;

import android.app.Application;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.squareup.otto.Bus;

import org.destil.gpsaveraging.util.MainThreadBus;

import io.fabric.sdk.android.Fabric;

public class App extends Application {

    private static App sInstance;

    private static Bus sBus;

    @NonNull
    public static App get() {
        return sInstance;
    }

    public static Bus bus() {
        return sBus;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
        sInstance = this;
        sBus = new MainThreadBus();
    }
}
