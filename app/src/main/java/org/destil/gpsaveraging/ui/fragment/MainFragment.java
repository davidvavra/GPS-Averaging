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

package org.destil.gpsaveraging.ui.fragment;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.destil.gpsaveraging.App;
import org.destil.gpsaveraging.R;
import org.destil.gpsaveraging.base.BaseFragment;
import org.destil.gpsaveraging.billing.Billing;
import org.destil.gpsaveraging.billing.event.BecomePremiumEvent;
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
 * Fragment containing most of the UI. It listens to events and triggers related components.
 *
 * @author David Vávra (david@vavra.me)
 */
@RuntimePermissions
public class MainFragment extends BaseFragment implements MainFragmentViewModel.FabListener {

    @Inject
    Bus mBus;
    @Inject
    GpsObserver mGps;
    @Inject
    Measurements mMeasurements;
    @Inject
    LocationAverager mAverager;
    @Inject
    Intents mIntents;
    @Inject
    Billing mBilling;

    private AdManager mAdManager;
    private MainFragmentViewModel mViewModel;
    private FragmentMainBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        App.component().injectToMainFragment(this);
        if (savedInstanceState == null) {
            mViewModel = new MainFragmentViewModel();
        } else {
            mViewModel = (MainFragmentViewModel) savedInstanceState.getSerializable("VIEW_MODEL");
        }
        if (mViewModel != null) {
            mViewModel.setClickListener(this);
            Log.d("viewModel", mViewModel.toString());
        }
        mBinding = FragmentMainBinding.inflate(inflater, container, false);
        mBinding.setViewModel(mViewModel);
        mBus.register(this);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdManager = new AdManager(mBinding.ad);
        if (!mBilling.isFullVersion()) {
            mViewModel.showAd = true;
            Animations.showFromBottom(mBinding.ad);
            mAdManager.load();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
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
        mAdManager.destroy();
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("VIEW_MODEL", mViewModel);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MainFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Subscribe
    public void onFirstFix(FirstFixEvent e) {
        if (!mViewModel.hasFix) {
            mViewModel.hasFix = true;
            Animations.hide(mBinding.progress);
            Animations.showFromTop(mBinding.currentLocation);
            Animations.showFromBottom(mBinding.fab);
        }
    }

    @Subscribe
    public void onGpsNotAvailable(GpsNotAvailableEvent e) {
        mViewModel.hasFix = false;
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

    @Subscribe
    public void onBecomePremium(BecomePremiumEvent e) {
        if (mViewModel.showAd) {
            mViewModel.showAd = false;
            Animations.hideToBottom(mBinding.ad);
        }
        getActivity().invalidateOptionsMenu();
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

    private void startAveraging() {
        mAverager.start();
        mViewModel.isAveraging = true;
        mViewModel.stopIcon.set(true);
        if (mViewModel.isReadyForSharing) {
            mViewModel.isReadyForSharing = false;
            mBinding.averageLocation.collapse(new Animations.AnimationEndCallback() {
                @Override
                public void onAnimationEnd() {
                    Animations.showFromTop(mBinding.currentLocation);
                    Animations.moveToBottom(mBinding.averageLocation);
                }
            });
        } else {
            Animations.showFromBottom(mBinding.averageLocation);
        }
    }

    private void stopAveraging() {
        mAverager.stop();
        mViewModel.isAveraging = false;
        mViewModel.isReadyForSharing = true;
        mViewModel.stopIcon.set(false);
        mIntents.answerToThirdParty(getActivity());
        Animations.hideToTop(mBinding.currentLocation);
        Animations.moveToTop(mBinding.averageLocation, new Animations.AnimationEndCallback() {
            @Override
            public void onAnimationEnd() {
                mBinding.averageLocation.expand();
            }
        });
    }
}
