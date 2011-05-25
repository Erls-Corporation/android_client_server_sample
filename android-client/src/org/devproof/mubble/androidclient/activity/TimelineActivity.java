package org.devproof.mubble.androidclient.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import com.google.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.devproof.mubble.androidclient.MubbleConstants;
import org.devproof.mubble.androidclient.MubblePreferences;
import org.devproof.mubble.androidclient.R;
import org.devproof.mubble.androidclient.bean.LocationBean;
import org.devproof.mubble.androidclient.client.GeoMessageRestClient;
import org.devproof.mubble.androidclient.service.UpdaterService;
import roboguice.inject.InjectView;


/**
 * Main page of app
 * Shows timeline and submit field
 * @author Carsten Hufe
 */
public class TimelineActivity extends BaseActivity implements View.OnClickListener, TextWatcher {
    private final static String TAG = TimelineActivity.class.getSimpleName();

    @Inject
    private GeoMessageAdapter geoMessageAdapter;
    @InjectView(R.id.composeField)
    private EditText composeField;
    @InjectView(R.id.errorMessage)
    private TextView errorMessage;
    @InjectView(R.id.composeButton)
    private Button composeButton;
    @InjectView(R.id.timelineList)
    private ListView listView;
    @InjectView(R.id.timelineProgress)
    private ProgressBar progressBar;
    @Inject
    private LocationBean locationBean;
    @Inject
    private MubblePreferences prefs;
    @Inject
    private ComposePostTask composePostTask;
    @Inject
    private ConnectivityManager connectivityManager;
    @Inject
    private GeoMessageRestClient geoMessageRestClient;
    private RefreshReceiver refreshReceiver = new RefreshReceiver();
    private LoadingReceiver loadingReceiver = new LoadingReceiver();
    private SyncErrorReceiver syncErrorReceiver = new SyncErrorReceiver();
    private Boolean compatibleWithServer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline);
        composeField.addTextChangedListener(this);
        composeButton.setOnClickListener(this);
        listView.setAdapter(geoMessageAdapter);
        if(!prefs.hasRulesAccepted()) {
            showDialog(INTRO_DIALOG);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        verifyStuffRequiredForWorking();
        startService(new Intent(this, UpdaterService.class));
        registerReceiver(refreshReceiver, new IntentFilter(MubbleConstants.BROADCAST_NEW_MESSAGES));
        registerReceiver(loadingReceiver, new IntentFilter(MubbleConstants.BROADCAST_LOADING_MESSAGES));
        registerReceiver(syncErrorReceiver, new IntentFilter(MubbleConstants.BROADCAST_SYNCERROR));
    }

    private void verifyStuffRequiredForWorking() {
        if(!locationBean.isGpsEnabled()) {
            showDialog(GPS_DIALOG);
        }
        else if(compatibleWithServer == null) {
            compatibleWithServer = geoMessageRestClient.isCompatibleWithServer();
            if(!compatibleWithServer) {
                showDialog(VERSION_DIALOG);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, UpdaterService.class));
        unregisterReceiver(refreshReceiver);
        unregisterReceiver(loadingReceiver);
        unregisterReceiver(syncErrorReceiver);

    }

    @Override
    public void onClick(View view) {
        prefs.validateAndGenerateIdentificationToken();
        if(!prefs.hasValidUsername()) {
            showDialog(USERNAME_DIALOG);
        }
        else {
            composePostTask.execute();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        composeButton.setEnabled(StringUtils.isNotBlank(charSequence != null ? charSequence.toString() : ""));
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch(id) {
            case USERNAME_DIALOG:
                return new UsernameAlertDialog();
            case GPS_DIALOG:
                return new GpsAlertDialog();
            case VERSION_DIALOG:
                return new VersionAlertDialog();
            case ABOUT_DIALOG:
                return new AboutDialog();
            case INTRO_DIALOG:
                return new IntroDialog();
            default:
                return null;
        }
    }

    private class SyncErrorReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int msgResource = intent.getIntExtra("msgResource", 0);
            errorMessage.setText(getString(msgResource));
            errorMessage.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            geoMessageAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.INVISIBLE);
            errorMessage.setVisibility(View.GONE);
        }
    }

    private class LoadingReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private class UsernameAlertDialog extends BaseAlertDialog {

        protected UsernameAlertDialog() {
            super(TimelineActivity.this, R.layout.alert_username, R.string.alertUsernameTitle);
            setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.alertUsernameOk), emptyOnClickListener());
            setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.alertUsernameCancel), emptyOnClickListener());
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            final EditText usernameEdit = (EditText) getView().findViewById(R.id.usernameEdit);
            usernameEdit.setText(prefs.getUsername());
            getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String username = usernameEdit.getText().toString();
                    prefs.setUsername(username);
                    if (prefs.hasValidUsername()) {
                        Toast.makeText(getApplicationContext(), R.string.alertUsernameSaved, Toast.LENGTH_LONG).show();
                        composePostTask.execute();
                        dismiss();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), R.string.alertUsernameIllegal, Toast.LENGTH_LONG).show();
                    }
                }
            });
            getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(), getString(R.string.alertUsernameNotSaved), Toast.LENGTH_LONG).show();
                    cancel();
                }
            });
        }
    }
}