package org.destil.gpsaveraging.ui;

import android.content.Context;
import android.location.Location;
import android.util.AttributeSet;
import android.view.View;

import org.destil.gpsaveraging.App;
import org.destil.gpsaveraging.R;
import org.destil.gpsaveraging.measure.Measurements;

import javax.inject.Inject;

/**
 * View displaying average location.
 */
public class AverageLocationCardView extends LocationCardView {

    @Inject
    Measurements mMeasurements;

    public AverageLocationCardView(Context context) {
        super(context);
    }

    public AverageLocationCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AverageLocationCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        super.init();
        vCardTitle.setText(R.string.average_coordinates);
        App.component().injectToAverageLocationCardView(this);
    }

    @Override
    public void updateLocation(Location location) {
        super.updateLocation(location);
        String noMeasurements = getContext().getString(R.string.measurements, mMeasurements.size());
        vMeasurements.setText(noMeasurements);
    }

    public View getActionsView() {
        return vActions;
    }
}
