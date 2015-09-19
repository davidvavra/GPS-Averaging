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
public class AboutViewModel implements Serializable {

    public ObservableField<String> version = new ObservableField<>();
    public ObservableBoolean showThankYou = new ObservableBoolean();
    private transient ClickListener mClickListener;
    public transient View.OnClickListener onMailClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mClickListener.onMailClicked();
        }
    };
    public transient View.OnClickListener onRateClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mClickListener.onRateClicked();
        }
    };
    public transient View.OnClickListener onGithubClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mClickListener.onGithubClicked();
        }
    };

    public void setClickListener(ClickListener clickListener) {
        mClickListener = clickListener;
    }

    public interface ClickListener {
        void onMailClicked();

        void onRateClicked();

        void onGithubClicked();
    }
}
