package org.destil.gpsaveraging.ui.view;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import org.destil.gpsaveraging.App;
import org.destil.gpsaveraging.R;
import org.destil.gpsaveraging.data.Exporter;
import org.destil.gpsaveraging.data.Intents;
import org.destil.gpsaveraging.databinding.ViewCardBinding;
import org.destil.gpsaveraging.ui.viewmodel.CardViewModel;

import javax.inject.Inject;

/**
 * Card showing a location.
 */
public class LocationCardView extends FrameLayout implements CardViewModel.ClickListener {

    @Inject
    Exporter mExporter;
    @Inject
    Intents mIntents;

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
        mViewModel = new CardViewModel(this);
        mBinding.setViewModel(mViewModel);
        addView(mBinding.getRoot());
        mViewModel.title.set(getContext().getString(R.string.current_coordinates));
    }

    public void updateLocation(Location location) {
        String locationText = mExporter.formatLatLon(location) + "\n" + mExporter.formatAccuracy(location) + "\n" + mExporter.formatAltitude(location);
        mViewModel.location.set(locationText);
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

    public View getActionsView() {
        return mBinding.actions;
    }

}
