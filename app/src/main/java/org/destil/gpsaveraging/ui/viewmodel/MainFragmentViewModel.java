package org.destil.gpsaveraging.ui.viewmodel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.view.View;

import java.io.Serializable;

/**
 * ViewModel for Main Fragment data binding.
 *
 * @author David Vávra (vavra@avast.com)
 */

public class MainFragmentViewModel implements Serializable {

    public final ObservableBoolean hasFix = new ObservableBoolean();
    public final ObservableField<String> satelliteInfo = new ObservableField<>();
    public final ObservableBoolean isAveraging = new ObservableBoolean();
    public final ObservableBoolean isReadyForSharing = new ObservableBoolean();
    private transient FabListener mClickListener;
    public final transient View.OnClickListener onFabClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mClickListener.onFabClicked();
        }
    };

    public void setClickListener(FabListener clickListener) {
        mClickListener = clickListener;
    }

    public interface FabListener {
        void onFabClicked();
    }

    @Override
    public String toString() {
        return "MainFragmentViewModel{" +
                "isReadyForSharing=" + isReadyForSharing.get() +
                ", hasFix=" + hasFix.get() +
                ", satelliteInfo=" + satelliteInfo.get() +
                ", isAveraging=" + isAveraging.get() +
                '}';
    }
}
