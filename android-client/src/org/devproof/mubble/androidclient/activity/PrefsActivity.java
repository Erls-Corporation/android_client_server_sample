package org.devproof.mubble.androidclient.activity;

import android.os.Bundle;
import org.devproof.mubble.androidclient.R;
import roboguice.activity.RoboPreferenceActivity;

/**
 * Default page for preferences
 *
 * @author Carsten Hufe
 */
public class PrefsActivity extends RoboPreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        addPreferencesFromResource(R.xml.prefs);
    }
}
