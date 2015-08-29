package org.destil.gpsaveraging.ui;

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

import butterknife.Bind;
import butterknife.ButterKnife;

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
}
