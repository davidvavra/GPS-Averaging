GPS Averaging
=============

Android app for precise GPS measurements, particularly useful for placing new geocaches.

 * [Install from Google Play](https://play.google.com/store/apps/details?id=org.destil.gpsaveraging)
 * Problems? [Report them here](https://groups.google.com/forum/?fromgroups#!forum/gps-averaging-app)
 * Would you like the app in your language? [Contribute to translations](http://www.getlocalization.com/GPSAveraging)

API
---

The app offers Intent-based API for 3rd party apps. You can use precise location from the app in your app.

### How to integrate it

Start new activity using this code:

	try
	{
		Intent intent = new Intent(Intent.ACTION_DEFAULT, Uri.parse("cz.destil.gpsaveraging.AVERAGED_LOCATION"));
		startActivityForResult(intent, 0);
	} catch (ActivityNotFoundException e) {
		//GPS Averaging is not installed, you can redirect user to Play like this:
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=org.destil.gpsaveraging"));
		startActivity(intent);
	}
In your activity override function onActivityResult for receiving data:

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == RESULT_OK) {
			Bundle bundle = intent.getExtras();
			double latitude = bundle.getDouble("latitude"); //decimal averaged latitude in WGS84 format
			double longitude = bundle.getDouble("longitude"); //decimal averaged longitude in WGS84 format
			double altitude = bundle.getDouble("altitude"); //averaged altitude in meters
			double accuracy = bundle.getDouble("accuracy"); //final accuracy in meters
			String name = bundle.getString("name"); //name of the waypoint
			//do whatever you need with this information
		}
		else
		{
			//handle cancel
		}
	}