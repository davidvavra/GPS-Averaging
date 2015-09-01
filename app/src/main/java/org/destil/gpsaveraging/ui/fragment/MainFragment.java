package org.destil.gpsaveraging.ui.fragment;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.destil.gpsaveraging.App;
import org.destil.gpsaveraging.R;
import org.destil.gpsaveraging.base.BaseFragment;
import org.destil.gpsaveraging.data.Intents;
import org.destil.gpsaveraging.location.GpsObserver;
import org.destil.gpsaveraging.location.event.CurrentLocationEvent;
import org.destil.gpsaveraging.location.event.FirstFixEvent;
import org.destil.gpsaveraging.location.event.GpsNotAvailableEvent;
import org.destil.gpsaveraging.location.event.SatellitesEvent;
import org.destil.gpsaveraging.measure.LocationAverager;
import org.destil.gpsaveraging.measure.Measurements;
import org.destil.gpsaveraging.measure.event.AveragedLocationEvent;
import org.destil.gpsaveraging.ui.AdManager;
import org.destil.gpsaveraging.ui.Animations;
import org.destil.gpsaveraging.ui.view.AverageLocationCardView;
import org.destil.gpsaveraging.ui.view.LocationCardView;
import org.destil.gpsaveraging.ui.view.Snackbar;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import permissions.dispatcher.ShowsRationale;

/**
 * Fragment doing main functions of the app.
 */
@RuntimePermissions
public class MainFragment extends BaseFragment {
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
    @Bind(R.id.coordinator)
    CoordinatorLayout vCoordinator;

    @Inject
    Bus mBus;
    @Inject
    GpsObserver mGps;
    @Inject
    Animations mAnimations;
    @Inject
    Measurements mMeasurements;
    @Inject
    LocationAverager mAverager;
    @Inject
    Intents mIntents;
    @Inject
    AdManager mAdManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        App.component().injetToMainFragment(this);
        mBus.register(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdManager.load(vAd);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGps.hasFix()) {
            showCurrentLocation();
        } else {
            showWaitingForGps();
        }
        changeFab();
        observeGps();
        MainFragmentPermissionsDispatcher.observeGpsWithCheck(this);
    }

    @Override
    public void onStop() {
        mGps.stop();
        super.onStop();
    }


    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        mBus.unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MainFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Subscribe
    public void onFirstFix(FirstFixEvent e) {
        if (vCards.getVisibility() == View.GONE) {
            vEmpty.setVisibility(View.GONE);
            vAverageLocation.setVisibility(View.GONE);
            mAnimations.showFromTop(vCards);
            mAnimations.showFromTop(vFab);
        }
    }

    @Subscribe
    public void onGpsNotAvailable(GpsNotAvailableEvent e) {
        Snackbar.show(vCoordinator, R.string.gps_not_available);
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
        if (mAverager.isRunning()) {
            stopAveraging();
        } else {
            startAveraging();
        }
        changeFab();
    }


    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    void observeGps() {
        mGps.start();
    }

    @ShowsRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    void showRationaleForLocation() {
        Snackbar.show(vCoordinator, R.string.location_permission_rationale);
    }


    /**
     * Restores state after rotation.
     */
    private void showCurrentLocation() {
        vEmpty.setVisibility(View.GONE);
        vCards.setVisibility(View.VISIBLE);
        vFab.setVisibility(View.VISIBLE);
        vCurrentLocation.updateLocation(mGps.getLastLocation());
        boolean hasMeasurements = mMeasurements.size() > 0;
        if (!hasMeasurements || mAverager.isRunning()) {
            vCurrentLocation.setVisibility(View.VISIBLE);
        } else {
            vCurrentLocation.setVisibility(View.GONE);
        }
        vAverageLocation.updateLocation(mMeasurements.getAveragedLocation());
        if (mAverager.isRunning()) {
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
            mAnimations.collapseAndMoveDown(vAverageLocation, vCurrentLocation);
        } else {
            vAverageLocation.setVisibility(View.VISIBLE); // animation doesn't work otherwise
            mAnimations.showFromTop(vAverageLocation);
        }
        mAverager.start();
    }

    private void changeFab() {
        if (mAverager.isRunning()) {
            vFab.setImageResource(R.drawable.ic_stop);
        } else {
            vFab.setImageResource(R.drawable.ic_record);
        }
    }

    private void stopAveraging() {
        mAnimations.hideToTop(vCurrentLocation);
        mAnimations.moveUpAndExpand(vAverageLocation);
        mAverager.stop();
        mIntents.answerToThirdParty(getActivity());
    }

    private void showEmpty() {
        vEmpty.setVisibility(View.VISIBLE);
        vCards.setVisibility(View.GONE);
        vFab.setVisibility(View.GONE);
    }
}
