package org.devproof.mubble.androidclient.client;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import com.google.inject.Inject;
import org.devproof.mubble.androidclient.MubbleConstants;
import org.devproof.mubble.androidclient.MubblePreferences;
import org.devproof.mubble.dto.message.ComposeGeoMessage;
import org.devproof.mubble.dto.message.DisplayGeoMessage;
import org.devproof.mubble.dto.message.Result;
import org.devproof.mubble.dto.version.MubbleVersion;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;
import roboguice.inject.ContextScoped;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default rest client
 *
 * @author Carsten Hufe
 */
@ContextScoped
public class GeoMessageRestClient {
    private static final String TAG = GeoMessageRestClient.class.getSimpleName();
    @Inject
    private RestTemplate restTemplate;
    @Inject
    private MubblePreferences prefs;
    @Inject
    private Context context;

    private UriTemplate uriTemplate= new UriTemplate("{apiUrl}/messages/{longitude}/{latitude}/{accuracy}/{limit}");

    /**
     * Retrieves all messages by location
     * @param location
     * @return
     */
    public List<DisplayGeoMessage> retrieveMessages(Location location) {
        Map<String,Object> values = new HashMap<String, Object>();
        values.put("apiUrl", prefs.getApiUrl());
        values.put("longitude", location.getLongitude());
        values.put("latitude", location.getLatitude());
        values.put("accuracy", location.getAccuracy());
        values.put("limit", prefs.getPullLimit());
        URI uri = uriTemplate.expand(values);
        Log.d(TAG, "Calling " + uri.toString());
        DisplayGeoMessage[] forObject = restTemplate.getForObject(uri, DisplayGeoMessage[].class);
        return Arrays.asList(forObject);
    }

    /**
     * Submits a message to the server
     * @param location GPS location
     * @param content message to post
     * @return Result with state and accepted message
     */
    public Result composeMessage(Location location, String content) {
        String url = prefs.getApiUrl() + "/messages";
        ComposeGeoMessage message = new ComposeGeoMessage(content, location.getLongitude(), location.getLatitude(), location.getAccuracy(), prefs.getUsername(), prefs.getIdentificationToken());
        return restTemplate.postForObject(url, message, Result.class);
    }

    /**
     * @return true if the restclient is compatible with the server version
     */
    public boolean isCompatibleWithServer() {
        String url = prefs.getApiUrl() + "/version";
        try {
            MubbleVersion mubbleVersion = restTemplate.getForObject(url, MubbleVersion.class);
            return mubbleVersion.getCompatibleVersion().contains(MubbleConstants.REST_SERVER_VERSION);
        } catch (RestClientException ex) {
            Log.e(TAG, "Error while calling version", ex);
            return true;
        }
    }
}
