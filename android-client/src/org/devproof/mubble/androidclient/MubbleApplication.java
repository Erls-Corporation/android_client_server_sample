package org.devproof.mubble.androidclient;

import android.util.Log;
import com.google.inject.Module;
import roboguice.application.RoboApplication;

import java.util.List;

/**
 * Main Application
 * Adds the main guice module
 *
 * @author Carsten Hufe
 */
public class MubbleApplication extends RoboApplication {
    private static final String TAG = MubbleApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Mubble created");
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "Mubble terminated");
    }

    @Override
    protected void addApplicationModules(List<Module> modules) {
        modules.add(new MubbleModule(this));
    }
}
