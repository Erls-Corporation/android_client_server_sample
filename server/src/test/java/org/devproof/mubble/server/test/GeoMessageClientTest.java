package org.devproof.mubble.server.test;

import org.devproof.mubble.dto.message.ComposeGeoMessage;
import org.devproof.mubble.dto.message.DisplayGeoMessage;
import org.devproof.mubble.dto.message.Result;
import org.devproof.mubble.dto.version.MubbleVersion;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

@Ignore
public class GeoMessageClientTest {

    /**
     * server URL ending with the servlet mapping on which the application can be reached.
     */
//    private static final String BASE_URL = "http://api.mubble.org";
    private static final String BASE_URL = "http://localhost:8080";

    private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void listMessages() {
//        String url = BASE_URL + "/messages/9.96/11.0001/0/200";
        String url = BASE_URL + "/messages/10.009998333333334/14.0/0.0/50";
        DisplayGeoMessage[] messages = restTemplate.getForObject(url, DisplayGeoMessage[].class);
        for (DisplayGeoMessage message : messages) {
            System.out.println(message);
        }
        System.out.println(messages.length);
    }

    @Test
	public void composeMessage() {
		String url = BASE_URL + "/messages";
        for(int i = 1; i > 0; i--) {
            double longitude = 11.4498192071915;
            double latitude = 48.1556564569673;
            ComposeGeoMessage message = new ComposeGeoMessage("hallo *!?$\"%|<>' sophie" + i, longitude, latitude, 0f, "devproof", "edadec01-d4eb-4f6e-97b6-d4d55c0a0014");
            Result result = restTemplate.postForObject(url, message, Result.class);
            System.out.println(result);
        }
	}

    @Test
	public void retrieveVersion() {
		String url = BASE_URL + "/version";
        MubbleVersion version = restTemplate.getForObject(url, MubbleVersion.class);
        System.out.println(version);
	}
}
