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
package org.destil.gpsaveraging.data;

import android.content.Context;
import android.location.Location;

import org.destil.gpsaveraging.R;
import org.destil.gpsaveraging.measure.Measurements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Formatter;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Performs exporting location into various formats (human-readable, GPX, KML)
 *
 * @author David Vávra (david@vavra.me), Libor Tvrdik (libor.tvrdik@gmail.com)
 */
@Singleton
public class Exporter {

    public static final SimpleDateFormat XSD_DATETIME = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);
    public static final String ACCURACY_SYMBOL = "±";
    public static final String GPX_FILE_NAME = "averaging.gpx";
    public static final String KML_FILE_NAME = "averaging.kml";
    public static final String APP_LINK = "https://play.google.com/store/apps/details?id=org.destil.gpsaveraging";
    private final Context mContext;
    private final Measurements mMeasurements;
    private final Preferences mPreferences;

    @Inject
    public Exporter(Context context, Measurements measurements, Preferences preferences) {
        mContext = context;
        mMeasurements = measurements;
        mPreferences = preferences;
    }

    /**
     * Coordinate format to a readable format (degrees - DDD MM.MMM)
     */
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

    /**
     * Coordinate format to a readable format (degrees - DDD MM.MMM)
     */
    private Appendable formatLatLon(Location location, Formatter formatter, Appendable output)
            throws IOException {
        formatCoordinate(location.getLatitude(), true, formatter, output);
        output.append("\n");
        formatCoordinate(location.getLongitude(), false, formatter, output);
        return output;
    }

    /**
     * Formats single lat/lon coordinate according to format in settings.
     */
    private Appendable formatCoordinate(double coordinate, boolean lat, Formatter formatter, Appendable output) throws IOException {
        String format = mPreferences.getCoordinateFormat();
        if (format.equals(Preferences.COORDS_DECIMAL)) {
            formatter.format("%.5f", coordinate);
        } else {
            if (lat) {
                output.append(coordinate > 0 ? "N " : "S ");
            } else {
                // lon
                output.append(coordinate > 0 ? "E " : "W ");
            }
            // calculations for formats
            double decimalDegrees = Math.abs(coordinate);
            int onlyDegrees = (int) decimalDegrees;
            double decimalMinutes = (decimalDegrees - onlyDegrees) * 60;

            if (format.equals(Preferences.COORDS_MINUTES)) {
                formatter.format("%d° %s%.3f'", onlyDegrees, (decimalMinutes < 10 ? "0" : ""), decimalMinutes);
            } else {
                // seconds format
                int onlyMinutes = (int) decimalMinutes;
                double decimalSeconds = (decimalMinutes - onlyMinutes) * 60;
                formatter.format("%d° %d' %.3f''", onlyDegrees, onlyMinutes, decimalSeconds);
            }
        }
        return output;
    }

    /**
     * Coordinate format to a readable format (degrees - DDD MM.MMM) accuracy.
     */
    public Appendable formatLatLonWithAccuracy(Location location, Formatter formatter, Appendable output) {
        try {
            formatLatLon(location, formatter, output);
            output.append("\n");
            formatAccuracy(location, formatter, output);
            return output;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Format accuracy in meter and feet.
     */
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

    /**
     * Format accuracy in meter and feet.
     */
    public Appendable formatAccuracy(Location location, Formatter formatter, Appendable output)
            throws IOException {
        output.append(ACCURACY_SYMBOL);
        formatLength(location.getAccuracy(), formatter);
        return output;
    }

    /**
     * Formats length for output in meter and feet.
     */
    public void formatLength(double length, Formatter formatter) {
        if (mPreferences.getUnitsFormat().equals(Preferences.UNITS_METRIC)) {
            formatter.format(" %,.1f m", length);
        } else {
            // imperial units
            formatter.format(" %,.1f %s", length * 3.28132739, mContext.getString(R.string.feet));
        }
    }

    /**
     * Formats height to view the users.
     */
    public String formatAltitude(Location location) {

        final StringBuilder alt = new StringBuilder();
        final Formatter formatter = new Formatter(alt);

        alt.append(mContext.getString(R.string.altitude));
        formatLength(location.getAltitude(), formatter);

        return alt.toString();
    }

    /**
     * Exports measurements into KML and GPX files.
     */
    public void saveToCache(boolean gpx) {
        final File storeDirectory = mContext.getCacheDir();
        final File file = new File(storeDirectory, gpx ? GPX_FILE_NAME : KML_FILE_NAME);
        BufferedWriter bw = null;
        try {
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
            bw = new BufferedWriter(new FileWriter(file));
            if (gpx) {
                toGPXString(mMeasurements, bw);
            } else {
                toKMLString(mMeasurements, bw);
            }
        } catch (IOException ignored) {
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ignored) {

                }
            }
        }
    }

    /**
     * Export {@link Measurements} to GPX format.
     *
     * @param measurements list measured values for reading, if is empty output only GPX
     *                     header
     * @param output       output stream
     */
    private void toGPXString(Measurements measurements, Appendable output) throws IOException {

        final Formatter formatter = new Formatter(output);

        output.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        output.append("<gpx version=\"1.0\" creator=\"").append(mContext.getString(R.string.app_name))
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
            output.append("\t<author>").append(mContext.getString(R.string.app_name)).append("</author>\n");
            output.append("\t<link>").append(APP_LINK).append("</link>\n");
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
     * @param measurements list measured values for reading, if is empty output only KML
     *                     header
     * @param output       stream
     */
    private void toKMLString(Measurements measurements, Appendable output) throws IOException {

        final Formatter formatter = new Formatter(output);

        output.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        output.append("<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\n");
        output.append("<Document>\n\n");

        output.append("\t<name>Average Coordinates</name>\n");
        output.append("\t<atom:author><atom:name>").append(mContext.getString(R.string.app_name))
                .append("</atom:name></atom:author>\n");
        output.append("\t<atom:link href=\"").append(APP_LINK).append("\" />\n");

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
     */
    String toShareText() {

        final StringBuilder output = new StringBuilder();
        final Formatter formatter = new Formatter(output);

        output.append(mContext.getString(R.string.averaged_location)).append("\n");
        formatLatLonWithAccuracy(mMeasurements.getAveragedLocation(), formatter, output);
        output.append("\n\n");

        output.append(mContext.getString(R.string.google_maps_link)).append("\n");
        output.append("https://maps.google.com/?q=");
        formatter.format(Locale.US, "%.5f,%.5f", mMeasurements.getLatitude(), mMeasurements.getLongitude());
        output.append("\n\n");

        output.append(mContext.getString(R.string.average_altitude)).append("\n");
        formatLength(mMeasurements.getAltitude(), formatter);
        output.append("\n\n");

        output.append(mContext.getString(R.string.email_footer)).append("\n");
        output.append(APP_LINK).append("\n");

        return output.toString();
    }

}
