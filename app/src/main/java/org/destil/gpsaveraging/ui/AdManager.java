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

package org.destil.gpsaveraging.ui;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * Manages loading ads.
 *
 * @author David Vávra (vavra@avast.com)
 */
public class AdManager {

    private final AdView mAdView;

    public AdManager(AdView adView) {
        mAdView = adView;
    }

    public void load() {
        AdRequest.Builder builder = new AdRequest.Builder();
        builder.addKeyword("geocaching");
        builder.addKeyword("gps");
        builder.addKeyword("location");
        builder.addKeyword("measurements");
        builder.addKeyword("places");
        builder.addKeyword("check in");
        builder.addTestDevice("6E70B945F7D166EA14779C899463B8BC"); // My N7
        builder.addTestDevice("197CB241DBFB335DD54A6D050DE58792"); // My N5
        builder.addTestDevice("996EE7E77D7181208AF916072F5FFE4C"); // My N5#2
        builder.addTestDevice("622AFF2BE01381DB65A2ACAE09D77ABD"); // Genymotion
        mAdView.loadAd(builder.build());
    }

    public void destroy() {
        mAdView.destroy();
    }
}
