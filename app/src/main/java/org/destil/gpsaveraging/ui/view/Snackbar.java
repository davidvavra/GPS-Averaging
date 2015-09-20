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

package org.destil.gpsaveraging.ui.view;

import android.app.Activity;
import android.support.annotation.StringRes;
import android.view.View;

/**
 * Toast utils.
 */
public class Snackbar {
    public static void show(View layout, @StringRes int stringRes) {
        android.support.design.widget.Snackbar.make(layout, stringRes, android.support.design.widget.Snackbar.LENGTH_LONG)
                .show();
    }

    public static void show(Activity activity, @StringRes int stringRes) {
        show(activity.findViewById(android.R.id.content), stringRes);
    }
}
