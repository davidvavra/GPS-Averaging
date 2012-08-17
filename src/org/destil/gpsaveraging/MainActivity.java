/*
 * Copyright 2010 David "Destil" Vavra, Libor Tvrdik
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

import net.robotmedia.billing.BillingController;
import net.robotmedia.billing.BillingRequest.ResponseCode;
import net.robotmedia.billing.helper.AbstractBillingObserver;
import net.robotmedia.billing.model.Transaction.PurchaseState;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class MainActivity extends SherlockActivity implements LocationListener, OnClickListener, Listener {

	private static final String GPS = "gps"; // type of location
	private static final int MEASUREMENT_DELAY = 2000; // delay between
														// measurements
	private static final String BILLING_ITEMID = "cz.destil.gpsaveraging.full";
	public static boolean isFullVersion = false;

	private LocationManager locationManager;
	private Exporter exporter;
	private boolean averaging = false;
	private Timer timer;
	private Measurements measurements;
	private AbstractBillingObserver billingObserver;
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
	private AdView uiAd;

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
		uiAd = (AdView) this.findViewById(R.id.adView);
		uiStartStop.setOnClickListener(this);
		initBilling();
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
	protected void onDestroy() {
		BillingController.unregisterObserver(billingObserver);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.findItem(R.id.menu_remove_ads).setVisible(!isFullVersion);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_email:
			shareViaEmail();
			break;
		case R.id.menu_export:
			if (isFullVersion) {
				showAlert(exporter.toGpxAndKmlFiles(measurements));
			} else {
				BillingController.requestPurchase(this, BILLING_ITEMID);
			}
			break;
		case R.id.menu_remove_ads:
			BillingController.requestPurchase(this, BILLING_ITEMID);
			break;
		case R.id.menu_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			break;
		case R.id.menu_about:
			startActivity(new Intent(this, AboutActivity.class));
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
		showError(R.string.you_dont_have_gps_enabled);
	}

	@Override
	public void onProviderEnabled(String provider) {
		showError(R.string.waiting_for_gps);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		if (status == LocationProvider.AVAILABLE) {
			showMainUi();
		} else {
			showError(R.string.gps_not_available);
		}
	}

	@SuppressWarnings("unused")
	@Override
	public void onGpsStatusChanged(int status) {
		if (status == GpsStatus.GPS_EVENT_FIRST_FIX) {
			showMainUi();
		} else if (status == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
			GpsStatus gpsStatus = locationManager.getGpsStatus(null);
			int all = 0;
			Iterable<GpsSatellite> satellites = gpsStatus.getSatellites();
			for (GpsSatellite satellite : satellites) {
				all++;
			}
			uiSatellites.setText(getString(R.string.satellites_info, all));
		} else if (status == GpsStatus.GPS_EVENT_STOPPED) {
			showError(R.string.gps_not_available);
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

	public static Intent getIntent(Context context) {
		Intent intent = new Intent(context, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return intent;
	}

	/**
	 * Loads AdMob ad.
	 */
	private void loadAd() {
		AdRequest adrequest = new AdRequest();
		adrequest.addKeyword("geocaching");
		adrequest.addKeyword("gps");
		adrequest.addKeyword("location");
		adrequest.addKeyword("measurements");
		adrequest.addKeyword("places");
		adrequest.addKeyword("check in");
		adrequest.addTestDevice("246237F4C0DB8A19F6F0DB3925B49300"); // My GN
		adrequest.addTestDevice("FDFCCBA0DA6971C00B0DE1227525504B"); // My N1
		uiAd.loadAd(adrequest);
	}

	/**
	 * Hides error screen and shows main UI.
	 */
	private void showMainUi() {
		if (uiGpsOn.getVisibility() != View.VISIBLE) {
			uiGpsOn.setVisibility(View.VISIBLE);
			uiGpsOff.setVisibility(View.GONE);
			uiAveraging.setVisibility(View.GONE);
			uiStartStop.setEnabled(true);
		}
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
		uiStartStop.setText(R.string.new_averaging);
		if (timer != null) {
			timer.cancel();
		}
		// Intent API & Locus integration
		if (measurements != null
				&& measurements.size() > 0
				&& getIntent().getAction() != null
				&& (getIntent().getAction().equals("menion.android.locus.GET_POINT") || getIntent().getAction().equals(
						"cz.destil.gpsaveraging.AVERAGED_LOCATION"))) {
			Intent intent = new Intent();
			intent.putExtra("name", getString(R.string.average_coordinates));
			intent.putExtra("latitude", measurements.getLatitude());
			intent.putExtra("longitude", measurements.getLongitude());
			intent.putExtra("altitude", measurements.getAltitude());
			intent.putExtra("accuracy", (double) measurements.getAccuracy());
			setResult(RESULT_OK, intent);
			finish();
		}
	}

	/**
	 * Starts measuring.
	 */
	private void startAveraging() {
		averaging = true;
		uiStartStop.setText(R.string.stop_averaging);
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
						uiNoOfMeasurements.setText(getString(R.string.measurements, measurements.size()));
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
		alertDialog.setPositiveButton(R.string.ok, null);
		alertDialog.show();
	}

	/**
	 * Exports averaged location via e-mail.
	 */
	private void shareViaEmail() {
		if (measurements.size() == 0) {
			Toast.makeText(this, R.string.start_averaging_first, Toast.LENGTH_LONG).show();
		} else {
			Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
			shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, exporter.toEmailText(measurements));
			startActivity(shareIntent);
		}
	}

	/**
	 * Initializes in-app billing.
	 */
	private void initBilling() {
		billingObserver = new AbstractBillingObserver(this) {

			@Override
			public void onPurchaseStateChanged(String itemId, PurchaseState state) {
				if (itemId.equals(BILLING_ITEMID)) {
					if (state == PurchaseState.PURCHASED) {
						isFullVersion = true;
						// hide ad
						uiAd.setVisibility(View.GONE);
					} else {
						isFullVersion = false;
					}
				}
			}

			@Override
			public void onBillingChecked(boolean supported) {
				if (supported && !billingObserver.isTransactionsRestored()) {
					BillingController.restoreTransactions(MainActivity.this);
				}
			}

			@Override
			public void onSubscriptionChecked(boolean supported) {
				// ignore
			}

			@Override
			public void onRequestPurchaseResponse(String itemId, ResponseCode response) {
				// ignore
			}
		};
		BillingController.registerObserver(billingObserver);
		BillingController.checkBillingSupported(this);
		isFullVersion = verifyFullVersion();
		if (!isFullVersion) {
			loadAd();
		}
	}

	/**
	 * Verifies if user has bought full version.
	 */
	private boolean verifyFullVersion() {
		if (BillingController.isPurchased(this, BILLING_ITEMID)) {
			return true;
		} else {
			// check for previous ad-free version
			if (getPackageManager().checkSignatures("org.destil.gpsaveraging", "cz.destil.gpsaveraging") == PackageManager.SIGNATURE_MATCH) {
				return true;
			}
			return false;
		}
	}

}
