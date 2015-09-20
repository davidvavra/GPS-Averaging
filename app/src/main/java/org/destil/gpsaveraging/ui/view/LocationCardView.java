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

package org.destil.gpsaveraging.ui.view;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import org.destil.gpsaveraging.App;
import org.destil.gpsaveraging.R;
import org.destil.gpsaveraging.data.Exporter;
import org.destil.gpsaveraging.data.Intents;
import org.destil.gpsaveraging.databinding.ViewCardBinding;
import org.destil.gpsaveraging.measure.Measurements;
import org.destil.gpsaveraging.ui.viewmodel.CardViewModel;

import javax.inject.Inject;

/**
 * Card showing a location.
 */
public abstract class LocationCardView extends FrameLayout implements CardViewModel.ClickListener {

    @Inject
    Exporter mExporter;
    @Inject
    Intents mIntents;
    @Inject
    Measurements mMeasurements;

    ViewCardBinding mBinding;
    CardViewModel mViewModel;

    public LocationCardView(Context context) {
        super(context);
        init();
    }

    public LocationCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LocationCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        App.component().injectToLocationCardView(this);
        mBinding = ViewCardBinding.inflate(LayoutInflater.from(getContext()), this, false);
        mViewModel = new CardViewModel();
        initViewModel();
        addView(mBinding.getRoot());
    }

    private void initViewModel() {
        mViewModel.setClickListener(this);
        mBinding.setViewModel(mViewModel);
        mViewModel.title.set(getContext().getString(getCardTitle()));
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("PARENT", super.onSaveInstanceState());
        bundle.putSerializable("VIEW_MODEL", mViewModel);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mViewModel = (CardViewModel) bundle.getSerializable("VIEW_MODEL");
            initViewModel();
            state = bundle.getParcelable("PARENT");
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    public void onShareClicked() {
        mIntents.share((Activity) getContext());
    }

    @Override
    public void onMapClicked() {
        mIntents.showOnMap((Activity) getContext());
    }

    @Override
    public void onGpxClicked() {
        mIntents.exportToGpx((Activity) getContext());
    }

    @Override
    public void onKmlClicked() {
        mIntents.exportToKml((Activity) getContext());
    }

    abstract int getCardTitle();

    abstract boolean addMeasurements();

    public void updateLocation(Location location) {
        String locationText = mExporter.formatLatLon(location) + "\n" + mExporter.formatAccuracy(location) + "\n" + mExporter.formatAltitude(location);
        mViewModel.location.set(locationText);
        if (addMeasurements()) {
            String noMeasurements = getContext().getString(R.string.measurements, mMeasurements.size());
            mViewModel.measurements.set(noMeasurements);
        }
    }

    public View getActionsView() {
        return mBinding.actions;
    }

    public void setExpanded() {
        mViewModel.showActions.set(true);
    }

    public void setCollapsed() {
        mViewModel.showActions.set(false);
    }
}
