package org.devproof.mubble.androidclient.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;
import com.google.inject.Inject;
import org.devproof.mubble.androidclient.MubbleConstants;
import org.devproof.mubble.androidclient.MubblePreferences;
import org.devproof.mubble.androidclient.R;
import org.devproof.mubble.androidclient.bean.BadQualityLocationException;
import org.devproof.mubble.androidclient.bean.GeoMessageBean;
import org.devproof.mubble.androidclient.bean.LocationBean;
import org.devproof.mubble.dto.message.Result;
import roboguice.inject.InjectView;
import roboguice.util.RoboAsyncTask;


/**
 * Background Task to post a message
 *
 * @author Carsten Hufe
 */
public class ComposePostTask extends RoboAsyncTask<Result.Status> {
    private static final String TAG = ComposePostTask.class.getSimpleName();

    @Inject
    private GeoMessageBean geoMessageBean;
    @Inject
    private ProgressDialog dialog;
    @InjectView(R.id.composeField)
    private EditText composeField;
    @InjectView(R.id.composeButton)
    private Button composeButton;
    @InjectView(R.id.timelineScroll)
    private ScrollView timelineScroll;
    @Inject
    private LocationBean locationBean;
    @Inject
    private MubblePreferences prefs;
    @Inject
    private Context context;

    @Override
    public Result.Status call() throws Exception {
        return geoMessageBean.submitMessage(composeField.getText().toString());
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage(context.getString(R.string.composeWait));
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.show();
    }

    @Override
    protected void onSuccess(Result.Status status) throws Exception {
        if(status == Result.Status.ACCEPTED) {
            Log.d(TAG, "Message posted");
            composeField.setText(null);
            composeButton.setEnabled(false);
            context.sendBroadcast(new Intent(MubbleConstants.BROADCAST_NEW_MESSAGES));
            Toast.makeText(context, R.string.composeSuccessful, Toast.LENGTH_LONG).show();
        }
        else {
            int resId = resolveMessageResourceByStatus(status);
            Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
        }
    }

    private int resolveMessageResourceByStatus(Result.Status status) {
        int resId;
        switch(status) {
            case MESSAGE_TO_LONG:
                resId = R.string.composeErrorCodeMessageTooLong;
                break;
            case TOO_MANY_REQUESTS:
                resId = R.string.composeErrorCodeTooManyRequests;
                break;
            case DUPLICATE_MESSAGE:
                resId = R.string.composeErrorCodeDuplicateMessage;
                break;
            case MESSAGE_TO_SHORT:
                resId = R.string.composeErrorCodeMessageTooShort;
                break;
            case MISSING_IDENTIFIER:
                resId = R.string.composeErrorCodeMissingIdentifier;
                break;
            case USERNAME_TOO_SHORT:
                resId = R.string.composeErrorCodeUsernameTooShort;
                break;
            case USERNAME_TO_LONG:
                resId = R.string.composeErrorCodeUsernameTooLong;
                break;
            default:
                resId = R.string.composeErrorCodeUnknown;
                break;
        }
        return resId;
    }

    @Override
    protected void onException(Exception e) {
        Log.e(TAG, "Failed", e);
        if(e instanceof BadQualityLocationException) {
            Toast.makeText(context, R.string.composeBadGpsSignal, Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(context, R.string.composeServerNotReachable, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onFinally() {
        // always do this in the UI thread after calling call()
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}