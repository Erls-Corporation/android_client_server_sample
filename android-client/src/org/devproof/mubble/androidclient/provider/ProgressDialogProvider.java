package org.devproof.mubble.androidclient.provider;

import android.app.ProgressDialog;
import android.content.Context;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Constructs the Waiting dialog for guice injection
 */
public class ProgressDialogProvider implements Provider<ProgressDialog> {

    @Inject
    private Context context;

    public ProgressDialog get() {
        return new ProgressDialog(context);
    }

} 