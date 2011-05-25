package org.devproof.mubble.util;

/**
 * Calculate the distance in meter betweeen to points
 *
 * @author Carsten Hufe
 */
public class DistanceUtil {

    public static double distanceInMeter(double fromLat, double fromLong, double toLat, double toLong) {
      double theta = fromLong - toLong;
      double dist = Math.sin(deg2rad(fromLat)) * Math.sin(deg2rad(toLat)) + Math.cos(deg2rad(fromLat)) * Math.cos(deg2rad(toLat)) * Math.cos(deg2rad(theta));
      dist = Math.acos(dist);
      dist = rad2deg(dist);
      dist = dist * 60 * 1.1515;
      dist = dist * 1.609344;
      dist = dist * 1000;
      // for nautical miles  dist = dist * 0.8684;
      return dist;
    }

    private static double deg2rad(double deg) {
      return deg * Math.PI / 180.0;
    }

    private static double rad2deg(double rad) {
      return rad * 180.0 / Math.PI;
    }
}
