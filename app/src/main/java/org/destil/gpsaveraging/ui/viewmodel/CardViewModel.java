package org.destil.gpsaveraging.ui.viewmodel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.view.View;

/**
 * TODO: add documentation
 *
 * @author David Vávra (david@vavra.me)
 */
public class CardViewModel {

    private final ClickListener mClickListener;
    public ObservableField<String> title = new ObservableField<>();
    public ObservableField<String> location = new ObservableField<>();
    public ObservableField<String> measurements = new ObservableField<>();
    public ObservableBoolean showActions = new ObservableBoolean();
    public View.OnClickListener onShareClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mClickListener.onShareClicked();
        }
    };
    public View.OnClickListener onMapClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mClickListener.onMapClicked();
        }
    };
    public View.OnClickListener onGpxClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mClickListener.onGpxClicked();
        }
    };
    public View.OnClickListener onKmlClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mClickListener.onKmlClicked();
        }
    };

    public CardViewModel(ClickListener clickListener) {
        mClickListener = clickListener;
    }

    public interface ClickListener {
        void onShareClicked();

        void onMapClicked();

        void onGpxClicked();

        void onKmlClicked();
    }
}
