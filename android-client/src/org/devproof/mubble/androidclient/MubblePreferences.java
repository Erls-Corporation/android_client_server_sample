package org.devproof.mubble.androidclient;

import android.content.SharedPreferences;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.lang.StringUtils;
import roboguice.inject.StringResourceFactory;

import java.util.UUID;

/**
 * Created At: 28.04.11 17:44
 *
 * @author Carsten Hufe
 */
@Singleton
public class MubblePreferences {
    @Inject
    private SharedPreferences sharedPreferences;
    @Inject
    private StringResourceFactory resource;

    public String getUsername() {
        return sharedPreferences.getString("username", "");
    }

    public void setUsername(String username) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("username", username);
        edit.commit();
    }

    public boolean hasValidUsername() {
        String username = getUsername();
        return StringUtils.isNotBlank(username)
                && username.length() >= MubbleConstants.USERNAME_MIN_LENGTH
                && username.length() <= MubbleConstants.USERNAME_MAX_LENGTH;
    }

    public String getApiUrl() {
        return sharedPreferences.getString("apiUrl", resource.get(R.string.prefsApiUrlDefault));
    }

    public String getIdentificationToken() {
        return sharedPreferences.getString("identificationToken", "");
    }

    public int getPullTimeInSeconds() {
        String pullTime = sharedPreferences.getString("pullTimeInSeconds", resource.get(R.integer.prefsPullTimeDefault));
        return Integer.parseInt(pullTime);
    }

    public int getPullLimit() {
        String pullLimit = sharedPreferences.getString("pullLimit", resource.get(R.integer.prefsPullLimitDefault));
        return Integer.parseInt(pullLimit);
    }

    public boolean isFullscreen() {
        String defaultVal = resource.get(R.bool.prefsFullscreenDefault);
        return sharedPreferences.getBoolean("fullscreen", Boolean.parseBoolean(defaultVal));
    }

    public boolean hasRulesAccepted() {
        return sharedPreferences.getBoolean("rulesAccepted", false);
    }

    public void acceptRules() {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean("rulesAccepted", true);
        edit.commit();
    }

    public void validateAndGenerateIdentificationToken() {
        String identificationToken = getIdentificationToken();
        if(StringUtils.isBlank(identificationToken)) {
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString("identificationToken", UUID.randomUUID().toString());
            edit.commit();
        }
    }
}
