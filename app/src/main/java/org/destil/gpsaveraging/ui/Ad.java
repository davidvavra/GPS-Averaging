package org.destil.gpsaveraging.ui;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Ad {

    @Inject
    public Ad() {
    }

    public void load(AdView adView) {
        AdRequest.Builder builder = new AdRequest.Builder();
        builder.addKeyword("geocaching");
        builder.addKeyword("gps");
        builder.addKeyword("location");
        builder.addKeyword("measurements");
        builder.addKeyword("places");
        builder.addKeyword("check in");
        builder.addTestDevice("6E70B945F7D166EA14779C899463B8BC"); // My N7
        builder.addTestDevice("197CB241DBFB335DD54A6D050DE58792"); // My N5
        adView.loadAd(builder.build());
    }
}
