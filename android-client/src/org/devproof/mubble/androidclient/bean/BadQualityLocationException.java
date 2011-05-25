package org.devproof.mubble.androidclient.bean;

/**
 * Thrown when location to bad to post something
 *
 * @author Carsten Hufe
 */
public class BadQualityLocationException extends Exception {
    public BadQualityLocationException() {
    }

    public BadQualityLocationException(String detailMessage) {
        super(detailMessage);
    }
}
