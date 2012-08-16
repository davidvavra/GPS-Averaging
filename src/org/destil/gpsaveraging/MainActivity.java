/*
 * Copyright 2010 Destil, Libor Tvrdik
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.destil.gpsaveraging;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class MainActivity extends Activity implements LocationListener, OnClickListener, Listener {

	private static final String GPS = "gps"; // type of location
	private static final int MEASUREMENT_DELAY = 2000; // delay between
														// measurements

	private LocationManager locationManager;
	private Exporter exporter;
	private boolean averaging = false;
	private Timer timer;
	private Measurements measurements;
	// UI elements
	private LinearLayout uiGpsOff;
	private TextView uiGpsOffText;
	private ScrollView uiGpsOn;
	private Button uiStartStop;
	private TextView uiCurrLatLon;
	private TextView uiCurrAcc;
	private TextView uiCurrAlt;
	private LinearLayout uiAveraging;
	private TextView uiAvgLatLon;
	private TextView uiAvgAcc;
	private TextView uiAvgAlt;
	private TextView uiNoOfMeasurements;
	private TextView uiSatellites;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		exporter = new Exporter(this);
		measurements = new Measurements();
		// elements from XML
		uiGpsOff = (LinearLayout) findViewById(R.id.gps_off);
		uiGpsOn = (ScrollView) findViewById(R.id.gps_on);
		uiStartStop = (Button) findViewById(R.id.start_stop);
		uiGpsOffText = (TextView) findViewById(R.id.gps_off_text);
		uiSatellites = (TextView) findViewById(R.id.satellites);
		uiCurrLatLon = (TextView) findViewById(R.id.curr_lat_lon);
		uiCurrAcc = (TextView) findViewById(R.id.curr_acc);
		uiCurrAlt = (TextView) findViewById(R.id.curr_alt);
		uiAvgLatLon = (TextView) findViewById(R.id.avg_lat_lon);
		uiAvgAcc = (TextView) findViewById(R.id.avg_acc);
		uiAvgAlt = (TextView) findViewById(R.id.avg_alt);
		uiNoOfMeasurements = (TextView) findViewById(R.id.no_of_measurements);
		uiAveraging = (LinearLayout) findViewById(R.id.avg_ui);
		uiStartStop.setOnClickListener(this);
		loadAd();
	}

	@Override
	protected void onStart() {
		super.onStart();
		locationManager.requestLocationUpdates(GPS, 0, 0, this);
		locationManager.addGpsStatusListener(this);
		if (locationManager.isProviderEnabled(GPS)) {
			onProviderEnabled(GPS);
		} else {
			onProviderDisabled(GPS);
		}
	}

	protected void onStop() {
		super.onStop();
		locationManager.removeUpdates(this);
		locationManager.removeGpsStatusListener(this);
		stopAveraging();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_email:
			exporter.toEmail(measurements);
			break;
		case R.id.menu_export:
			showAlert(exporter.toGpxAndKmlFiles(measurements));
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onLocationChanged(Location location) {
		uiCurrLatLon.setText(exporter.formatLatLon(location));
		uiCurrAcc.setText(exporter.formatAccuracy(location));
		uiCurrAlt.setText(exporter.formatAltitude(location));
	}

	@Override
	public void onProviderDisabled(String provider) {
		showError(R.string.YouDontHaveGPSEnabled);
	}

	@Override
	public void onProviderEnabled(String provider) {
		showError(R.string.WaitingForGPS);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		if (status == LocationProvider.AVAILABLE) {
			showMainUi();
		} else {
			showError(R.string.GPSNotAvailable);
		}
	}

	@Override
	public void onGpsStatusChanged(int status) {
		if (status == GpsStatus.GPS_EVENT_FIRST_FIX) {
			showMainUi();
		} else if (status == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
			GpsStatus gpsStatus = locationManager.getGpsStatus(null);
			int all = 0;
			Iterable<GpsSatellite> satellites = gpsStatus.getSatellites();
			for (@SuppressWarnings("unused") GpsSatellite satellite : satellites) {
				all++;
			}
			uiSatellites.setText(getString(R.string.SatellitesInfo, all));
		}
	}

	@Override
	public void onClick(View v) {
		// start/stop button click
		if (averaging) {
			stopAveraging();
		} else {
			startAveraging();
		}
	}

	/**
	 * Loads AdMob ad.
	 */
	private void loadAd() {
		AdView adView = (AdView) this.findViewById(R.id.adView);
		AdRequest adrequest = new AdRequest();
		adrequest.addKeyword("geocaching");
		adrequest.addKeyword("gps");
		adrequest.addKeyword("location");
		adrequest.addKeyword("measurements");
		adrequest.addKeyword("places");
		adrequest.addKeyword("check in");
		adView.loadAd(adrequest);
	}

	/**
	 * Hides error screen and shows main UI.
	 */
	private void showMainUi() {
		uiGpsOn.setVisibility(View.VISIBLE);
		uiGpsOff.setVisibility(View.GONE);
		uiAveraging.setVisibility(View.GONE);
		uiStartStop.setEnabled(true);
	}

	/**
	 * Hides main UI and shows error screen.
	 */
	private void showError(int errorResource) {
		uiGpsOn.setVisibility(View.GONE);
		uiGpsOff.setVisibility(View.VISIBLE);
		uiGpsOffText.setText(errorResource);
		uiStartStop.setEnabled(false);
	}

	/**
	 * Starts measuring.
	 */
	private void stopAveraging() {
		averaging = false;
		uiStartStop.setText(R.string.NewAveraging);
		if (timer != null) {
			timer.cancel();
		}
	}

	/**
	 * Starts measuring.
	 */
	private void startAveraging() {
		averaging = true;
		uiStartStop.setText(R.string.StopAveraging);
		measurements.clean();
		uiAveraging.setVisibility(View.VISIBLE);
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				measurements.add(locationManager.getLastKnownLocation(GPS));
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Location averagedLocation = measurements.getAveragedLocation();
						uiAvgLatLon.setText(exporter.formatLatLon(averagedLocation));
						uiAvgAcc.setText(exporter.formatAccuracy(averagedLocation));
						uiAvgAlt.setText(exporter.formatAltitude(averagedLocation));
						uiNoOfMeasurements.setText(getString(R.string.Measurements, measurements.size()));
					}
				});
			}
		}, 0, MEASUREMENT_DELAY);
	}

	/**
	 * Shows simple alert.
	 */
	private void showAlert(String text) {
		final Builder alertDialog = new Builder(this);
		alertDialog.setMessage(text);
		alertDialog.setPositiveButton(R.string.OK, null);
		alertDialog.show();
	}

}
