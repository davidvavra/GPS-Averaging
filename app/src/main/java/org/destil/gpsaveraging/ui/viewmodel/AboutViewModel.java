package org.destil.gpsaveraging.ui.viewmodel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.view.View;

/**
 * TODO: add documentation
 *
 * @author David Vávra (david@vavra.me)
 */
public class AboutViewModel {

    private final ClickListener mClickListener;
    public ObservableField<String> version = new ObservableField<>();
    public ObservableBoolean showThankYou = new ObservableBoolean();
    public View.OnClickListener onMailClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mClickListener.onMailClicked();
        }
    };
    public View.OnClickListener onRateClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mClickListener.onRateClicked();
        }
    };
    public View.OnClickListener onGithubClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mClickListener.onGithubClicked();
        }
    };

    public AboutViewModel(ClickListener clickListener) {
        mClickListener = clickListener;
    }

    public interface ClickListener {
        void onMailClicked();

        void onRateClicked();

        void onGithubClicked();
    }
}
