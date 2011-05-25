package org.devproof.mubble.androidclient.bean;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Carsten Hufe
 */
@Singleton
public class LocationBean implements LocationListener {
    @Inject
    private LocationManager locationManager;
    private Location location;

    @Override
    public void onLocationChanged(Location location) {
        setCurrentLocation(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

    public synchronized Location getCurrentLocation() {
        if(location == null) {
//            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        return location;
    }

    private synchronized void setCurrentLocation(Location location) {
        this.location = location;
    }

    public boolean isQualityAcceptableForComposing() {
        Location location = getCurrentLocation();
        if(location == null) {
            return false;
        }
        // min. 100m accuracy und gps daten nicht älter als 2 min
//        Date locationDate = new Date(location.getTime());
//        locationDate = DateUtils.addMinutes(locationDate, 2);
        return location.getAccuracy() <= 100 && isGpsEnabled();
//                && locationDate.after(new Date());
    }

    public boolean isGpsEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public boolean isQualityAcceptableForReading() {
        return getCurrentLocation() != null && isGpsEnabled();
    }

}
