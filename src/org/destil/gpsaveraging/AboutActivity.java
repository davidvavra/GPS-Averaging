package org.destil.gpsaveraging;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Shows about information, support etc.
 * 
 * @author Destil
 */
public class AboutActivity extends Activity {

	private String version;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		//TODO: getSupportActionBar().setDisplayHomeAsUpEnabled(true);.
		try {
			version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			version = "Unknown";
		}
		((TextView) findViewById(R.id.version)).setText(version);
		if (!MainActivity.isFullVersion) {
			findViewById(R.id.thank_you).setVisibility(View.GONE);
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
		String usersPhone = Build.MANUFACTURER + " "
				+ Build.MODEL + " (Android " + Build.VERSION.RELEASE + ") " + "v" + version + "-"
				+ Locale.getDefault();
		i.putExtra(Intent.EXTRA_TEXT, getString(R.string.problem_report_body, usersPhone));
		startActivity(i);
	}

	/**
	 * Rates app on Play.
	 */
	public void rateButtonClicked(View view) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=org.destil.gpsaveraging"));
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		startActivity(intent);
	}

	/**
	 * Visits app's web
	 */
	public void webButtonClicked(View view) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/destil/GPS-Averaging"));
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		startActivity(intent);
	}
}
