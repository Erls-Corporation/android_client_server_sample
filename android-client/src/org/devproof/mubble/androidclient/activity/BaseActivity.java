package org.devproof.mubble.androidclient.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import com.google.inject.Inject;
import org.devproof.mubble.androidclient.MubblePreferences;
import org.devproof.mubble.androidclient.R;
import roboguice.activity.RoboActivity;


/**
 * Base activity for menu and dialog stuff
 *
 * @author Carsten Hufe
 */
public class BaseActivity extends RoboActivity {
    public final static int USERNAME_DIALOG = 1;
    public final static int GPS_DIALOG = 2;
    public final static int ABOUT_DIALOG = 3;
    public final static int VERSION_DIALOG = 4;
    public final static int INTRO_DIALOG = 5;

    @Inject
    private MubblePreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(prefs.isFullscreen()) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.generalMenuPreferences:
                startActivity(new Intent(this, PrefsActivity.class));
                break;
            case R.id.generalMenuAbout:
                showDialog(ABOUT_DIALOG);
                break;
            case R.id.generalMenuExit:
                finish();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.general, menu);
        return true;
    }

    public class IntroDialog extends AlertDialog {
        protected IntroDialog() {
            super(BaseActivity.this);
            LayoutInflater inflater = LayoutInflater.from(BaseActivity.this);
            View view = inflater.inflate(R.layout.intro, null);
            setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.alertIntroAccept), acceptOnClickListener());
            setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.alertIntroDecline), exitApplicationListener());
            setView(view);
            setCancelable(false);
        }

        private DialogInterface.OnClickListener acceptOnClickListener() {
            return new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    prefs.acceptRules();
                }
            };
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // set layout width to maximum
            ViewGroup.LayoutParams params = getWindow().getAttributes();
//            params.height = ViewGroup.LayoutParams.FILL_PARENT;
            params.width = ViewGroup.LayoutParams.FILL_PARENT;
            getWindow().setAttributes((WindowManager.LayoutParams) params);
        }
    }

    public class AboutDialog extends AlertDialog {
        protected AboutDialog() {
            super(BaseActivity.this);
            LayoutInflater inflater = LayoutInflater.from(BaseActivity.this);
            View view = inflater.inflate(R.layout.about, null);
            setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.alertAboutOk), emptyOnClickListener());
            setView(view);
            setCancelable(false);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // set layout width to maximum
            ViewGroup.LayoutParams params = getWindow().getAttributes();
//            params.height = ViewGroup.LayoutParams.FILL_PARENT;
            params.width = ViewGroup.LayoutParams.FILL_PARENT;
            getWindow().setAttributes((WindowManager.LayoutParams) params);
        }
    }

    public class VersionAlertDialog extends BaseAlertDialog {
        protected VersionAlertDialog() {
            super(BaseActivity.this, R.layout.alert_version, R.string.alertVersionTitle);
            setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.alertVersionOk), exitListener());
        }

        private DialogInterface.OnClickListener exitListener() {
            return new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            };
        }
    }

    public class GpsAlertDialog extends BaseAlertDialog {
        protected GpsAlertDialog() {
            super(BaseActivity.this, R.layout.alert_gps, R.string.alertGpsTitle);
            setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.alertGpsOk), gotoGpsSettingsListener());
            setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.alertGpsCancel), exitApplicationListener());
        }

        private DialogInterface.OnClickListener gotoGpsSettingsListener() {
            return new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                }
            };
        }
    }

    public DialogInterface.OnClickListener exitApplicationListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };
    }

    public DialogInterface.OnClickListener emptyOnClickListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        };
    }
}
