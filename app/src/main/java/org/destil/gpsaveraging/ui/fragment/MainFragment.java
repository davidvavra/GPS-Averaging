package org.destil.gpsaveraging.ui.fragment;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.destil.gpsaveraging.App;
import org.destil.gpsaveraging.R;
import org.destil.gpsaveraging.base.BaseFragment;
import org.destil.gpsaveraging.data.Intents;
import org.destil.gpsaveraging.databinding.FragmentMainBinding;
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
import org.destil.gpsaveraging.ui.view.Snackbar;
import org.destil.gpsaveraging.ui.viewmodel.MainFragmentViewModel;

import javax.inject.Inject;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import permissions.dispatcher.ShowsRationale;

/**
 * Fragment doing main functions of the app.
 */
@RuntimePermissions
public class MainFragment extends BaseFragment implements MainFragmentViewModel.FabListener {

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

    private MainFragmentViewModel mViewModel;
    private FragmentMainBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentMainBinding.inflate(inflater, container, false);
        mViewModel = new MainFragmentViewModel(this);
        mBinding.setViewModel(mViewModel);
        App.component().injectToMainFragment(this);
        mBus.register(this);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdManager.load(mBinding.ad);
    }

    @Override
    public void onStart() {
        super.onStart();
        mViewModel.hasFix.set(mGps.hasFix());
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
        mBus.unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MainFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Subscribe
    public void onFirstFix(FirstFixEvent e) {
        mViewModel.hasFix.set(true);
        if (mBinding.cards.getVisibility() == View.GONE) {
            mBinding.averageLocation.setVisibility(View.GONE);
            mAnimations.showFromTop(mBinding.cards);
            mAnimations.showFromTop(mBinding.fab);
        }
    }

    @Subscribe
    public void onGpsNotAvailable(GpsNotAvailableEvent e) {
        Snackbar.show(mBinding.coordinator, R.string.gps_not_available);
    }

    @Subscribe
    public void onSatellites(SatellitesEvent e) {
        mViewModel.satelliteInfo.set(getString(R.string.satellites_info, e.getCount()));
    }

    @Subscribe
    public void onCurrentLocation(CurrentLocationEvent e) {
        mBinding.currentLocation.updateLocation(e.getLocation());
    }

    @Subscribe
    public void onAverageLocation(AveragedLocationEvent e) {
        mBinding.averageLocation.updateLocation(e.getLocation());
    }

    @Override
    public void onFabClicked() {
        if (mAverager.isRunning()) {
            stopAveraging();
        } else {
            startAveraging();
        }
    }


    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    void observeGps() {
        mGps.start();
    }

    @ShowsRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    void showRationaleForLocation() {
        Snackbar.show(mBinding.coordinator, R.string.location_permission_rationale);
    }


    /**
     * Restores state after rotation.
     */
    private void showCurrentLocation() {
        mBinding.currentLocation.updateLocation(mGps.getLastLocation());
        boolean hasMeasurements = mMeasurements.size() > 0;
        if (!hasMeasurements || mAverager.isRunning()) {
            mBinding.currentLocation.setVisibility(View.VISIBLE);
        } else {
            mBinding.currentLocation.setVisibility(View.GONE);
        }
        mBinding.averageLocation.updateLocation(mMeasurements.getAveragedLocation());
        if (mAverager.isRunning()) {
            mBinding.averageLocation.setVisibility(View.VISIBLE);
            mBinding.averageLocation.getActionsView().setVisibility(View.GONE);
        } else {
            if (hasMeasurements) {
                mBinding.averageLocation.setVisibility(View.VISIBLE);
                mBinding.averageLocation.getActionsView().setVisibility(View.VISIBLE);
            } else {
                mBinding.averageLocation.setVisibility(View.GONE);
            }
        }
    }

    private void startAveraging() {
        mViewModel.isAveraging.set(true);
        if (mBinding.currentLocation.getVisibility() == View.GONE) {
            mAnimations.collapseAndMoveDown(mBinding.averageLocation, mBinding.currentLocation);
        } else {
            mBinding.averageLocation.setVisibility(View.VISIBLE); // animation doesn't work otherwise
            mAnimations.showFromTop(mBinding.averageLocation);
        }
        mAverager.start();
    }

    private void stopAveraging() {
        mViewModel.isAveraging.set(false);
        mAnimations.hideToTop(mBinding.currentLocation);
        mAnimations.moveUpAndExpand(mBinding.averageLocation);
        mAverager.stop();
        mIntents.answerToThirdParty(getActivity());
    }
}
