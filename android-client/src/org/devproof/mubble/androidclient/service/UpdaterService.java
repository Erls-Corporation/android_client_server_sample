package org.devproof.mubble.androidclient.service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;
import com.google.inject.Inject;
import org.devproof.mubble.androidclient.MubbleConstants;
import org.devproof.mubble.androidclient.MubblePreferences;
import org.devproof.mubble.androidclient.R;
import org.devproof.mubble.androidclient.bean.GeoMessageBean;
import org.devproof.mubble.androidclient.bean.LocationBean;
import org.springframework.web.client.HttpServerErrorException;
import roboguice.service.RoboService;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Synchronizes the messages to database to get a "smooth feeling" while displaying messages
 *
 * @author Carsten Hufe
 */
public class UpdaterService extends RoboService implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = UpdaterService.class.getSimpleName();
    @Inject
    private GeoMessageBean geoMessageBean;
    @Inject
    private LocationManager locationManager;
    @Inject
    private MubblePreferences prefs;
    @Inject
    private SharedPreferences sharedPreferences;
    @Inject
    private LocationBean locationBean;
    private Timer timer = new Timer("UpdateServiceThread");
    private UpdaterTask task;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        startPullingMessages();
        Log.d(TAG, "UpdaterService created");
    }

    private void startPullingMessages() {
        int pullTime = prefs.getPullTimeInSeconds() * 1000;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, pullTime / 3, 5, locationBean);
        task = new UpdaterTask();
        timer.schedule(task, 300, pullTime);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "UpdaterService destroyed");
        stopPullingMessages();
    }

    private void stopPullingMessages() {
        locationManager.removeUpdates(locationBean);
        task.cancel();
        timer.purge();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        stopPullingMessages();
        startPullingMessages();
    }

    private class UpdaterTask extends TimerTask {

        @Override
        public void run() {
            sendBroadcast(new Intent(MubbleConstants.BROADCAST_LOADING_MESSAGES));
            Log.d(TAG, "Calling UpdaterTask.run()");
            if(locationBean.isQualityAcceptableForReading()) {
                try {
                    geoMessageBean.syncGeoMessages();
                    sendBroadcast(new Intent(MubbleConstants.BROADCAST_NEW_MESSAGES));
                }
                catch(HttpServerErrorException ex) {
                    handleException(ex, R.string.syncErrorServerDown);
                }
                catch (RuntimeException ex) {
                    handleException(ex, R.string.syncErrorNetwork);
                }
            }
            else {
                sendBroadcastSyncError(R.string.syncErrorBadGpsSignal);
            }
        }

        private void sendBroadcastSyncError(int errorResource) {
            Intent intent = new Intent(MubbleConstants.BROADCAST_SYNCERROR);
            intent.putExtra("msgResource", errorResource);
            sendBroadcast(intent);
        }

        private void handleException(RuntimeException e, int errorResource) {
            Log.e(TAG, "error while fetching messages", e);
            sendBroadcastSyncError(errorResource);
            throw e;
        }
    }
}
