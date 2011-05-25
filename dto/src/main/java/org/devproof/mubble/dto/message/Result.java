package org.devproof.mubble.dto.message;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Result when a message was posted
 * Contains the status and posted message
 *
 * @author Carsten Hufe
 */
public class Result {
    public enum Status {ACCEPTED, USERNAME_TOO_SHORT, USERNAME_TO_LONG, MISSING_IDENTIFIER, MESSAGE_TO_SHORT, MESSAGE_TO_LONG, DUPLICATE_MESSAGE, TOO_MANY_REQUESTS}

    @JsonProperty("status")
    private Status status;
    @JsonProperty("posted_message")
    private DisplayGeoMessage postedMessage;

    public Result() {
    }

    public Result(Status status, DisplayGeoMessage postedMessage) {
        this.status = status;
        this.postedMessage = postedMessage;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public DisplayGeoMessage getPostedMessage() {
        return postedMessage;
    }

    public void setPostedMessage(DisplayGeoMessage postedMessage) {
        this.postedMessage = postedMessage;
    }

    @Override
    public String toString() {
        return "Result{" + "status=" + status + ", postedMessage=" + postedMessage + '}';
    }
}
