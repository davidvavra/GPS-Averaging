package org.destil.gpsaveraging.ui.viewmodel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.view.View;

import java.io.Serializable;

/**
 * TODO: add documentation
 *
 * @author David Vávra (david@vavra.me)
 */
public class CardViewModel implements Serializable {

    public final ObservableField<String> title = new ObservableField<>();
    public final ObservableField<String> location = new ObservableField<>();
    public final ObservableField<String> measurements = new ObservableField<>();
    public final ObservableBoolean showActions = new ObservableBoolean();
    private transient ClickListener mClickListener;
    public transient View.OnClickListener onShareClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mClickListener.onShareClicked();
        }
    };
    public transient View.OnClickListener onMapClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mClickListener.onMapClicked();
        }
    };
    public transient View.OnClickListener onGpxClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mClickListener.onGpxClicked();
        }
    };
    public transient View.OnClickListener onKmlClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mClickListener.onKmlClicked();
        }
    };

    public void setClickListener(ClickListener clickListener) {
        mClickListener = clickListener;
    }

    public interface ClickListener {
        void onShareClicked();

        void onMapClicked();

        void onGpxClicked();

        void onKmlClicked();
    }

    @Override
    public String toString() {
        return title.get();
    }
}
