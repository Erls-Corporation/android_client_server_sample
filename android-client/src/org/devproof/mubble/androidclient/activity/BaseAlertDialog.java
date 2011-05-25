package org.devproof.mubble.androidclient.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import org.devproof.mubble.androidclient.R;

/**
 * Base activity for all alert dialogss
 *
 * @author Carsten Hufe
 */
public class BaseAlertDialog extends AlertDialog {
    private View view;

    protected BaseAlertDialog(Context context, int layoutId, int titleId) {
        super(context);
        setIcon(R.drawable.alert_dialog_icon);
        setTitle(titleId);
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(layoutId, null);
        setView(view);
    }

    public View getView() {
        return view;
    }
}
