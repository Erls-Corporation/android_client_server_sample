package org.devproof.mubble.androidclient;

/**
 * @author Carsten Hufe
 */
public interface MubbleConstants {
    String PACKAGE = MubbleConstants.class.getPackage().getName();
    String BROADCAST_NEW_MESSAGES = PACKAGE + ".newMessages";
    String BROADCAST_LOADING_MESSAGES = PACKAGE + ".loadingMessages";
    String BROADCAST_SYNCERROR = PACKAGE + ".networkerror";
    String DB_NAME = "mubble.db";
    int DB_VERSION = 7;

    int USERNAME_MIN_LENGTH = 3;
    int USERNAME_MAX_LENGTH = 16;
    String REST_SERVER_VERSION = "1";

}
