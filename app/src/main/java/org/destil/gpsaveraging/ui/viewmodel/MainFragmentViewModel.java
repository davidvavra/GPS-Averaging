package org.destil.gpsaveraging.ui.viewmodel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.location.Location;
import android.view.View;

/**
 * ViewModel for Main Fragment data binding.
 *
 * @author David Vávra (vavra@avast.com)
 */

public class MainFragmentViewModel {

    public final ObservableBoolean hasFix = new ObservableBoolean();
    public final ObservableField<String> satelliteInfo = new ObservableField<>();
    public final ObservableBoolean isAveraging = new ObservableBoolean();
    public final ObservableBoolean isReadyForSharing = new ObservableBoolean();
    public final View.OnClickListener fabOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mFabListener.onFabClicked();
        }
    };
    private final FabListener mFabListener;

    public MainFragmentViewModel(FabListener fabListener) {
        mFabListener = fabListener;
    }

    public interface FabListener {
        void onFabClicked();
    }
}
