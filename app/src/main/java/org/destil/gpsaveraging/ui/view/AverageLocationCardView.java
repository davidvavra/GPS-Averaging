package org.destil.gpsaveraging.ui.view;

import android.content.Context;
import android.location.Location;
import android.util.AttributeSet;

import org.destil.gpsaveraging.App;
import org.destil.gpsaveraging.R;
import org.destil.gpsaveraging.measure.Measurements;

import javax.inject.Inject;

/**
 * View displaying average location.
 */
public class AverageLocationCardView extends LocationCardView {

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
    int getCardTitle() {
        return R.string.averaged_location;
    }

    @Override
    boolean addMeasurements() {
        return true;
    }
}
