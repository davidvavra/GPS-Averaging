/*
   Copyright 2010 Libor Tvrdik

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
package org.destil.gpsaveraging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Formatter;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Environment;

/** @author Libor Tvrdik (libor.tvrdik &lt;at&gt; gmail.com) */
public class Exporter {

	public static final SimpleDateFormat XSD_DATETIME = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); 
	public static final String ACCURACY_SYMBOL = "±"; 

	private final Context c;

	public Exporter(Context applicationContext) {
		this.c = applicationContext;
	}

	private Context getContext() {
		return c;
	}

	/**
	 * Exports averaged location via e-mail.
	 */
	public void toEmail(Measurements measurements) {
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, c.getString(R.string.EmailSubject));
		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, toEmailText(measurements));
		c.startActivity(shareIntent);
	}

	/**
	 * Exports measurements into KML and GPX files.
	 * 
	 * @param measurements
	 * @return message for the user
	 */
	public String toGpxAndKmlFiles(Measurements measurements) {
		try {
			final Calendar now = Calendar.getInstance();
			final File storeDirectory = new File(Environment.getExternalStorageDirectory(),
					c.getString(R.string.app_directory));

			if (!storeDirectory.exists()) {
				storeDirectory.mkdirs();
			}

			if (!storeDirectory.exists()) {
				return c.getString(R.string.CantCreateStorageDirectory, storeDirectory);
			}

			final String dateFormatPattern = "%1$td-%<tm-%<tY_%<tH-%<tM-%<tS.%2$s";

			final File gpxFile = new File(storeDirectory, String.format(dateFormatPattern, now, "gpx"));
			gpxFile.createNewFile();
			final BufferedWriter gpxOut = new BufferedWriter(new FileWriter(gpxFile));
			toGPXString(measurements, gpxOut);
			gpxOut.close();

			final File kmlFile = new File(storeDirectory, String.format(dateFormatPattern, now, "kml"));
			kmlFile.createNewFile();
			final BufferedWriter kmlOut = new BufferedWriter(new FileWriter(kmlFile));
			toKMLString(measurements, kmlOut);
			kmlOut.close();
			// all OK
			return c.getString(R.string.AverageLocationExportedToFiles, storeDirectory, gpxFile.getName(),
					kmlFile.getName());
		} catch (IOException e) {
			return c.getString(R.string.ApplicationCantWriteFile, e.getMessage());
		}
	}

	/**
	 * Export {@link Measurements} to GPX format.
	 * 
	 * @param measurements
	 *            list measured values for reading, if is empty output only GPX
	 *            header
	 * @param output
	 *            output stream
	 */
	private void toGPXString(Measurements measurements, Appendable output) throws IOException {

		final Formatter formatter = new Formatter(output);

		output.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		output.append("<gpx version=\"1.0\" creator=\"").append(getContext().getString(R.string.app_name))
				.append("\" \n");
		output.append("    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n");
		output.append("    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" \n");
		output.append("    xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd\" \n");
		output.append("    xmlns=\"http://www.topografix.com/GPX/1/0\">\n\n");

		if (measurements.size() > 0) {

			output.append("\t<name>Average Coordinates</name>\n");
			output.append("\t<desc>This is an average coordinates from ")
					.append(SimpleDateFormat.getDateInstance().format(measurements.getTime(0))).append(" ")
					.append(SimpleDateFormat.getTimeInstance().format(measurements.getTime(0))).append(" - ")
					.append(SimpleDateFormat.getTimeInstance().format(measurements.getTime(measurements.size() - 1)))
					.append("</desc>\n");
			output.append("\t<author>").append(getContext().getString(R.string.app_name)).append("</author>\n");
			output.append("\t<link>").append(getContext().getString(R.string.app_link)).append("</link>\n");
			output.append("\t<time>").append(XSD_DATETIME.format(measurements.getTime(0))).append("</time>\n\n");

			output.append("\t<wpt lat=\"").append(String.valueOf(measurements.getLatitude())).append("\" lon=\"")
					.append(String.valueOf(measurements.getLongitude())).append("\">\n");
			output.append("\t\t<name>Final Average Coordinates</name>\n");
			output.append("\t\t<desc>Computed as the average of the ").append(String.valueOf(measurements.size()))
					.append(" points. Coordinates: ");
			formatLatLonWithAccuracy(measurements.getAveragedLocation(), formatter, output);
			output.append("</desc>\n");
			output.append("\t\t<ele>").append(String.valueOf(measurements.getAltitude())).append("</ele>\n");
			output.append("\t</wpt>\n\n");

			output.append("\t<rte>\n");
			for (int i = 0; i < measurements.size(); i++) {
				output.append("\t\t<rtept lat=\"").append(String.valueOf(measurements.getLatitude(i)))
						.append("\" lon=\"").append(String.valueOf(measurements.getLongitude(i))).append("\">\n");
				output.append("\t\t\t<name>Source point #").append(String.valueOf(i + 1)).append("</name>\n");
				output.append("\t\t\t<desc>Coordinates: ");
				formatLatLonWithAccuracy(measurements.getLocation(i), formatter, output);
				output.append("</desc>\n");
				// gpx.append("\t\t\t<sat> xsd:nonNegativeInteger </sat>\n");
				output.append("\t\t\t<time>").append(XSD_DATETIME.format(measurements.getTime(i))).append("</time>\n");
				output.append("\t\t\t<ele>").append(String.valueOf(measurements.getAltitude(i))).append("</ele>\n");
				output.append("\t\t</rtept>\n");
			}
			output.append("\t</rte>\n");
		}

		output.append("</gpx>\n");
	}

	/**
	 * Export {@link Measurements} to KML format.
	 * 
	 * @param measurements
	 *            list measured values for reading, if is empty output only KML
	 *            header
	 * @param output
	 *            stream
	 */
	private void toKMLString(Measurements measurements, Appendable output) throws IOException {

		final Formatter formatter = new Formatter(output);

		output.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		output.append("<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\n");
		output.append("<Document>\n\n");

		output.append("\t<name>Average Coordinates</name>\n");
		output.append("\t<atom:author><atom:name>").append(getContext().getString(R.string.app_name))
				.append("</atom:name></atom:author>\n");
		output.append("\t<atom:link href=\"").append(getContext().getString(R.string.app_link)).append("\" />\n");

		if (measurements.size() > 0) {

			output.append("\t<description>This is an average coordinates from ")
					.append(SimpleDateFormat.getDateInstance().format(measurements.getTime(0))).append(" ")
					.append(SimpleDateFormat.getTimeInstance().format(measurements.getTime(0))).append(" - ")
					.append(SimpleDateFormat.getTimeInstance().format(measurements.getTime(measurements.size() - 1)))
					.append("</description>\n\n");

			output.append("\t<Style id=\"finalAvgPoint\">\n");
			output.append("\t\t<IconStyle>\n");
			output.append("\t\t\t<Icon>\n");
			output.append("\t\t\t\t<href>http://maps.google.com/mapfiles/kml/pal3/icon52.png</href>\n");
			output.append("\t\t\t</Icon>\n");
			output.append("\t\t</IconStyle>\n");
			output.append("\t</Style>\n");
			output.append("\t<Style id=\"partOfMeasurement_n\">\n");
			output.append("\t\t<IconStyle>\n");
			output.append("\t\t\t<Icon>\n");
			output.append("\t\t\t\t<href>http://maps.google.com/mapfiles/kml/pal4/icon57.png</href>\n");
			output.append("\t\t\t</Icon>\n");
			output.append("\t\t</IconStyle>\n");
			output.append("\t</Style>\n");
			output.append("\t<Style id=\"partOfMeasurement_h\">\n");
			output.append("\t\t<IconStyle>\n");
			output.append("\t\t\t<Icon>\n");
			output.append("\t\t\t\t<href>http://maps.google.com/mapfiles/kml/pal4/icon49.png</href>\n");
			output.append("\t\t\t</Icon>\n");
			output.append("\t\t</IconStyle>\n");
			output.append("\t</Style>\n\n");

			output.append("\t<StyleMap id=\"partPoint\">\n");
			output.append("\t\t<Pair>\n");
			output.append("\t\t\t<key>normal</key>\n");
			output.append("\t\t\t<styleUrl>#partOfMeasurement_n</styleUrl>\n");
			output.append("\t\t</Pair>\n");
			output.append("\t\t<Pair>\n");
			output.append("\t\t\t<key>highlight</key>\n");
			output.append("\t\t\t<styleUrl>#partOfMeasurement_h</styleUrl>\n");
			output.append("\t\t</Pair>\n");
			output.append("\t</StyleMap>\n\n");

			output.append("\t<Folder>\n");
			output.append("\t\t<name>Final Average Coordinates</name>\n");
			output.append("\t\t<Placemark>\n");
			output.append("\t\t\t<name>Final Average Coordinates</name>\n");
			output.append("\t\t\t<description>Computed as the average of the ")
					.append(String.valueOf(measurements.size())).append(" points. Coordinates: ");
			formatLatLonWithAccuracy(measurements.getAveragedLocation(), formatter, output);
			output.append("</description>\n");
			output.append("\t\t\t<styleUrl>#finalAvgPoint</styleUrl>\n");
			output.append("\t\t\t<Point>\n");
			output.append("\t\t\t\t<coordinates>").append(String.valueOf(measurements.getLongitude())).append(",")
					.append(String.valueOf(measurements.getLatitude())).append(",")
					.append(String.valueOf(measurements.getAltitude())).append("</coordinates>\n");
			output.append("\t\t\t</Point>\n");
			output.append("\t\t</Placemark>\n");
			output.append("\t</Folder>\n\n");

			output.append("\t<Folder>\n");
			output.append("\t\t<name>List of source points").append("</name>\n");
			for (int i = 0; i < measurements.size(); i++) {
				output.append("\t\t<Placemark>\n");
				output.append("\t\t\t<name>Source point #").append(String.valueOf(i + 1)).append("</name>\n");
				output.append("\t\t\t<description>Coordinates: ");
				formatLatLonWithAccuracy(measurements.getLocation(i), formatter, output);
				output.append("</description>\n");
				output.append("\t\t\t<styleUrl>#partPoint</styleUrl>\n");
				output.append("\t\t\t<Point>\n");
				output.append("\t\t\t\t<coordinates>").append(String.valueOf(measurements.getLongitude(i))).append(",")
						.append(String.valueOf(measurements.getLatitude(i))).append(",")
						.append(String.valueOf(measurements.getAltitude(i))).append("</coordinates>\n");
				output.append("\t\t\t</Point>\n");
				output.append("\t\t</Placemark>\n");
			}
			output.append("\t</Folder>\n");
		}

		output.append("</Document>\n");
		output.append("</kml>\n");
	}

	/**
	 * Export {@link Measurements} to text format.
	 * 
	 * @param measurements
	 *            list measured values for reading
	 */
	private String toEmailText(Measurements measurements) {

		final StringBuilder output = new StringBuilder();
		final Formatter formatter = new Formatter(output);

		output.append(getContext().getString(R.string.AverageCoordinates)).append("\n");
		formatLatLonWithAccuracy(measurements.getAveragedLocation(), formatter, output);
		output.append("\n\n");

		output.append(getContext().getString(R.string.GoogleMapsLink)).append("\n");
		output.append("https://maps.google.com/?q=").append(String.valueOf(measurements.getLatitude())).append(",")
				.append(String.valueOf(measurements.getLongitude())).append("\n\n");

		output.append(getContext().getString(R.string.AverageAltitude));
		formatLength(measurements.getAltitude(), formatter);
		output.append("\n\n");

		output.append(getContext().getString(R.string.EmailFooter)).append("\n");
		output.append(getContext().getString(R.string.app_link)).append("\n");

		return output.toString();
	}

	/** Coordinate format to a readable format (degrees - DDD MM.MMM) */
	public String formatLatLon(Location location) {

		final StringBuilder output = new StringBuilder();
		final Formatter formatter = new Formatter(output);

		try {
			formatLatLon(location, formatter, output);
		} catch (IOException ex) {
			// IOException on StringBuilder is impossible
			throw new IllegalStateException(ex);
		}

		return output.toString();
	}

	/** Coordinate format to a readable format (degrees - DDD MM.MMM) */
	public Appendable formatLatLon(Location location, Formatter formatter, Appendable output) throws IOException {

		output.append(location.getLatitude() > 0 ? " N" : " S");

		final double lat = Math.abs(location.getLatitude());
		final int latDegree = (int) lat;
		final double latMinute = (lat - latDegree) * 60;
		// Second format parameter, fixed zero padding mistake for floating
		// point number
		formatter.format("%02d° %s%.3f ", latDegree, (latMinute < 10 ? "0" : ""), latMinute);

		output.append(location.getLongitude() > 0 ? " E" : " W");

		final double lon = Math.abs(location.getLongitude());
		final int lonDegree = (int) lon;
		final double lonMinute = (lon - lonDegree) * 60;
		// Second format parameter, fixed zero padding mistake for floating
		// point number
		formatter.format("%03d° %s%.3f ", lonDegree, (lonMinute < 10 ? "0" : ""), lonMinute);

		return output;
	}

	/**
	 * Coordinate format to a readable format (degrees - DDD MM.MMM) � accuracy.
	 */
	public String formatLatLonWithAccuracy(Location location) {

		final StringBuilder output = new StringBuilder();
		final Formatter formatter = new Formatter(output);
			formatLatLonWithAccuracy(location, formatter, output);

		return output.toString();
	}

	/**
	 * Coordinate format to a readable format (degrees - DDD MM.MMM) � accuracy.
	 */
	public Appendable formatLatLonWithAccuracy(Location location, Formatter formatter, Appendable output) {
		try {
			formatLatLon(location, formatter, output);
			formatAccuracy(location, formatter, output);
			return output;
		} catch (IOException e) {
			return null;
		}
	}

	/** Format � accuracy in meter and feet. */
	public String formatAccuracy(Location location) {

		final StringBuilder output = new StringBuilder();
		final Formatter formatter = new Formatter(output);

		try {
			formatAccuracy(location, formatter, output);
		} catch (IOException ex) {
			// IOException on StringBuilder is impossible
			throw new IllegalStateException(ex);
		}

		return output.toString();
	}

	/** Format � accuracy in meter and feet. */
	public Appendable formatAccuracy(Location location, Formatter formatter, Appendable output) throws IOException {

		output.append(ACCURACY_SYMBOL);
		formatLength(location.getAccuracy(), formatter);

		return output;
	}

	/** Formats length for output in meter and feet. */
	public void formatLength(double length, Formatter formatter) {
		formatter.format(" %,.1f m (%,.1f %s)", length, length * 3.28132739, getContext().getString(R.string.Feet)); 
	}

	/**
	 * Formats height to view the users.
	 * 
	 * @param altitude
	 *            in meter
	 */
	public String formatAltitude(Location location) {

		final StringBuilder alt = new StringBuilder();
		final Formatter formatter = new Formatter(alt);

		alt.append(getContext().getString(R.string.Altitude));
		formatLength(location.getAltitude(), formatter);

		return alt.toString();
	}

}
