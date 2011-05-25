package org.devproof.mubble.dto.message;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Message DTO for posting new messages
 *
 * @author Carsten Hufe
 */
public class ComposeGeoMessage extends GeoMessage {
    @JsonProperty("identifier")
    private String identifier;

    public ComposeGeoMessage() {
    }

    public ComposeGeoMessage(String message, double longitude, double latitude, float accuracy, String username, String identifier) {
        super(message, longitude, latitude, accuracy, username);
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return "ComposeGeoMessage{" + "identifier='" + identifier + '\'' + "} " + super.toString();
    }
}
