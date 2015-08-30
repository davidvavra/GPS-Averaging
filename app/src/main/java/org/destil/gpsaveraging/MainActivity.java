package org.destil.gpsaveraging;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.otto.Subscribe;

import org.destil.gpsaveraging.location.GpsObserver;
import org.destil.gpsaveraging.location.event.CurrentLocationEvent;
import org.destil.gpsaveraging.location.event.FirstFixEvent;
import org.destil.gpsaveraging.location.event.GpsNotAvailableEvent;
import org.destil.gpsaveraging.location.event.SatellitesEvent;
import org.destil.gpsaveraging.measure.AveragingService;
import org.destil.gpsaveraging.measure.Measurements;
import org.destil.gpsaveraging.measure.event.AveragedLocationEvent;
import org.destil.gpsaveraging.ui.Animations;
import org.destil.gpsaveraging.ui.AverageLocationCardView;
import org.destil.gpsaveraging.ui.LocationCardView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Activity which displays current and averaged location.
 *
 * @author David Vávra (david@vavra.me)
 */
public class MainActivity extends AppCompatActivity {

    @Bind(R.id.empty)
    LinearLayout vEmpty;
    @Bind(R.id.cards)
    ScrollView vCards;
    @Bind(R.id.fab)
    FloatingActionButton vFab;
    @Bind(R.id.ad)
    AdView vAd;
    @Bind(R.id.progress)
    ProgressBar vProgress;
    @Bind(R.id.status)
    TextView vStatus;
    @Bind(R.id.satellites)
    TextView vSatellites;
    @Bind(R.id.current_location)
    LocationCardView vCurrentLocation;
    @Bind(R.id.average_location)
    AverageLocationCardView vAverageLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        App.bus().register(this);
        loadAd();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (GpsObserver.getInstance().hasFix()) {
            showCurrentLocation();
        } else {
            showWaitingForGps();
        }
        changeFab();
        GpsObserver.getInstance().start();
    }

    @Override
    protected void onStop() {
        GpsObserver.getInstance().stop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        App.bus().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onFirstFix(FirstFixEvent e) {
        if (vCards.getVisibility() == View.GONE) {
            vEmpty.setVisibility(View.GONE);
            vAverageLocation.setVisibility(View.GONE);
            Animations.showFromTop(vCards);
            Animations.showFromTop(vFab);
        }
    }

    @Subscribe
    public void onGpsNotAvailable(GpsNotAvailableEvent e) {
        showEmpty();
        vStatus.setText(R.string.gps_not_available);
        vProgress.setVisibility(View.GONE);
        vSatellites.setVisibility(View.GONE);
    }

    @Subscribe
    public void onSatellites(SatellitesEvent e) {
        vSatellites.setText(getString(R.string.satellites_info, e.getCount()));
    }

    @Subscribe
    public void onCurrentLocation(CurrentLocationEvent e) {
        vCurrentLocation.updateLocation(e.getLocation());
    }

    @Subscribe
    public void onAverageLocation(AveragedLocationEvent e) {
        vAverageLocation.updateLocation(e.getLocation());
    }

    @OnClick(R.id.fab)
    public void onFabClicked(View view) {
        if (AveragingService.isRunning()) {
            stopAveraging();
        } else {
            startAveraging();
        }
        changeFab();
    }

    /**
     * Restores state after rotation.
     */
    private void showCurrentLocation() {
        vEmpty.setVisibility(View.GONE);
        vCards.setVisibility(View.VISIBLE);
        vFab.setVisibility(View.VISIBLE);
        vCurrentLocation.updateLocation(GpsObserver.getInstance().getLastLocation());
        boolean hasMeasurements = Measurements.getInstance().size() > 0;
        if (!hasMeasurements || AveragingService.isRunning()) {
            vCurrentLocation.setVisibility(View.VISIBLE);
        } else {
            vCurrentLocation.setVisibility(View.GONE);
        }
        vAverageLocation.updateLocation(Measurements.getInstance().getAveragedLocation());
        if (AveragingService.isRunning()) {
            vAverageLocation.setVisibility(View.VISIBLE);
            vAverageLocation.getActionsView().setVisibility(View.GONE);
        } else {
            if (hasMeasurements) {
                vAverageLocation.setVisibility(View.VISIBLE);
                vAverageLocation.getActionsView().setVisibility(View.VISIBLE);
            } else {
                vAverageLocation.setVisibility(View.GONE);
            }
        }
    }

    private void showWaitingForGps() {
        showEmpty();
        vProgress.setVisibility(View.VISIBLE);
        vStatus.setText(R.string.waiting_for_gps);
        vSatellites.setVisibility(View.VISIBLE);
    }

    private void startAveraging() {
        if (vCurrentLocation.getVisibility() == View.GONE) {
            Animations.collapseAndMoveDown(vAverageLocation, vCurrentLocation);
        } else {
            vAverageLocation.setVisibility(View.VISIBLE); // animation doesn't work otherwise
            Animations.showFromTop(vAverageLocation);
        }
        AveragingService.start();
    }

    private void changeFab() {
        if (AveragingService.isRunning()) {
            vFab.setImageResource(R.drawable.ic_stop);
        } else {
            vFab.setImageResource(R.drawable.ic_record);
        }
    }

    private void stopAveraging() {
        Animations.hideToTop(vCurrentLocation);
        Animations.moveUpAndExpand(vAverageLocation);
        AveragingService.stop();
        Measurements measurements = Measurements.getInstance();
        // Intent API & Locus integration
        if (measurements.size() > 0
                && getIntent().getAction() != null
                && (getIntent().getAction().equals("menion.android.locus.GET_POINT") || getIntent().getAction().equals(
                "cz.destil.gpsaveraging.AVERAGED_LOCATION"))) {
            Intent intent = new Intent();
            intent.putExtra("name", getString(R.string.average_coordinates));
            intent.putExtra("latitude", measurements.getLatitude());
            intent.putExtra("longitude", measurements.getLongitude());
            intent.putExtra("altitude", measurements.getAltitude());
            intent.putExtra("accuracy", (double) measurements.getAccuracy());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void showEmpty() {
        vEmpty.setVisibility(View.VISIBLE);
        vCards.setVisibility(View.GONE);
        vFab.setVisibility(View.GONE);
    }

    private void loadAd() {
        AdRequest.Builder builder = new AdRequest.Builder();
        builder.addKeyword("geocaching");
        builder.addKeyword("gps");
        builder.addKeyword("location");
        builder.addKeyword("measurements");
        builder.addKeyword("places");
        builder.addKeyword("check in");
        builder.addTestDevice("6E70B945F7D166EA14779C899463B8BC"); // My N7
        vAd.loadAd(builder.build());
    }
}
