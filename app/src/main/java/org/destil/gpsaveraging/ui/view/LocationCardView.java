package org.destil.gpsaveraging.ui.view;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.destil.gpsaveraging.App;
import org.destil.gpsaveraging.R;
import org.destil.gpsaveraging.data.Exporter;
import org.destil.gpsaveraging.data.Intents;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Card showing a location.
 */
public class LocationCardView extends FrameLayout {

    @Bind(R.id.card_title)
    TextView vCardTitle;
    @Bind(R.id.card_content)
    TextView vCardContent;
    @Bind(R.id.actions)
    LinearLayout vActions;
    @Bind(R.id.measurements)
    TextView vMeasurements;

    @Inject
    Exporter mExporter;
    @Inject
    Intents mIntents;

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
        addView(LayoutInflater.from(getContext()).inflate(R.layout.view_card, this, false));
        ButterKnife.bind(this);
        App.component().injectToLocationCardView(this);
        vActions.setVisibility(View.GONE);
    }

    public void updateLocation(Location location) {
        String locationText = mExporter.formatLatLon(location) + "\n" + mExporter.formatAccuracy(location) + "\n" + mExporter.formatAltitude(location);
        vCardContent.setText(locationText);
    }

    @OnClick(R.id.share)
    public void onShare(View view) {
        mIntents.share((Activity) getContext());
    }

    @OnClick(R.id.map)
    public void onMap(View view) {
        mIntents.showOnMap((Activity) getContext());
    }

    @OnClick(R.id.gpx)
    public void onGpx(View view) {
        mIntents.exportToGpx((Activity) getContext());
    }

    @OnClick(R.id.kml)
    public void onKml(View view) {
        mIntents.exportToKml((Activity) getContext());
    }
}
