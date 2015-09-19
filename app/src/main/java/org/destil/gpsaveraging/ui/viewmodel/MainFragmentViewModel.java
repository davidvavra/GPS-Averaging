package org.destil.gpsaveraging.ui.viewmodel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.location.Location;

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
}
