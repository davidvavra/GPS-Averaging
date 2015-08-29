/*
   Copyright 2012 David "Destil" Vavra

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.destil.gpsaveraging.ui;

import java.util.Locale;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.destil.gpsaveraging.R;

/**
 * Shows about information, support etc.
 * 
 * @author Destil
 */
public class AboutActivity extends AppCompatActivity {

	private String version;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		try {
			version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			version = "Unknown";
		}
		((TextView) findViewById(R.id.version)).setText(version);
		//if (!OldActivity.isFullVersion) {
		//	findViewById(R.id.thank_you).setVisibility(View.GONE);
		//}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon in action bar clicked; go home
			//startActivity(new Intent(this, OldActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Sends mail to the author.
	 */
	public void mailButtonClicked(View view) {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("text/plain");
		i.putExtra(Intent.EXTRA_EMAIL, new String[] { "gps-averaging-app@googlegroups.com" });
		i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.problem_report));
		String usersPhone = Build.MANUFACTURER + " " + Build.MODEL + " (Android " + Build.VERSION.RELEASE + ") " + "v"
				+ version + "-" + Locale.getDefault();
		i.putExtra(Intent.EXTRA_TEXT, getString(R.string.problem_report_body, usersPhone));
		startActivity(i);
	}

	/**
	 * Rates app on Play.
	 */
	public void rateButtonClicked(View view) {
		Intent intent = new Intent(Intent.ACTION_VIEW,
				Uri.parse("https://play.google.com/store/apps/details?id=org.destil.gpsaveraging"));
		startActivity(intent);
	}

	/**
	 * Visits app's web
	 */
	public void webButtonClicked(View view) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/destil/GPS-Averaging"));
		startActivity(intent);
	}
}
