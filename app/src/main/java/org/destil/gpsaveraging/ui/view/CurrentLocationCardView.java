package org.destil.gpsaveraging.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import org.destil.gpsaveraging.R;

/**
 * View displaying current location.
 */
public class CurrentLocationCardView extends LocationCardView {

    public CurrentLocationCardView(Context context) {
        super(context);
    }

    public CurrentLocationCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CurrentLocationCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    int getCardTitle() {
        return R.string.current_location;
    }

    @Override
    boolean addMeasurements() {
        return false;
    }
}
