package org.destil.gpsaveraging.ui;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.destil.gpsaveraging.R;
import org.destil.gpsaveraging.data.Exporter;
import org.destil.gpsaveraging.data.IntentUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Card showing a location.
 */
public class LocationCardView extends CardView {

    @Bind(R.id.card_title)
    TextView vCardTitle;
    @Bind(R.id.card_content)
    TextView vCardContent;
    @Bind(R.id.actions)
    LinearLayout vActions;

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
        vActions.setVisibility(View.GONE);
    }

    public void updateLocation(Location location) {
        String locationText = Exporter.formatLatLon(location) + "\n" + Exporter.formatAccuracy(location) + "\n" + Exporter.formatAltitude(location);
        vCardContent.setText(locationText);
    }

    @OnClick(R.id.share)
    public void onShare(View view) {
        IntentUtils.share((Activity) getContext());
    }

    @OnClick(R.id.map)
    public void onMap(View view) {
        IntentUtils.showOnMap((Activity) getContext());
    }

    @OnClick(R.id.gpx)
    public void onGpx(View view) {
        IntentUtils.exportToGpx((Activity) getContext());
    }

    @OnClick(R.id.kml)
    public void onKml(View view) {
        IntentUtils.exportToKml((Activity) getContext());
    }
}
