/*
 * Copyright 2015 David Vávra
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    public final ObservableField<String> version = new ObservableField<>();
    public final ObservableBoolean showThankYou = new ObservableBoolean();
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
