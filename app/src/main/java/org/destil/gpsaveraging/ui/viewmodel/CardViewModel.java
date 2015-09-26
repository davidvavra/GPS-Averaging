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
 * ViewModel for LocationCardView, it's used in data binding.
 * Can be serialized to survive device rotation.
 *
 * @author David Vávra (david@vavra.me)
 */
public class CardViewModel implements Serializable {

    public final ObservableField<String> title = new ObservableField<>();
    public final ObservableField<String> location = new ObservableField<>();
    public final ObservableField<String> measurements = new ObservableField<>();
    public boolean showActions;
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
