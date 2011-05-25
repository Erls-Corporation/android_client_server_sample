package org.devproof.mubble.dto.message;

import com.j256.ormlite.field.DatabaseField;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * Base geo message, contains shared data
 *
 * Created At: 08.04.11 14:33
 *
 * @author Carsten Hufe
 */
public abstract class GeoMessage implements Serializable {

    @DatabaseField(columnName = "message")
    @JsonProperty("message")
    private String message;
    @DatabaseField(columnName = "longitude")
    @JsonProperty("longitude")
    private double longitude;
    @DatabaseField(columnName = "latitude")
    @JsonProperty("latitude")
    private double latitude;
    @DatabaseField(columnName = "accuracy")
    @JsonProperty("accuracy")
    private float accuracy;
    @DatabaseField(columnName = "username")
    @JsonProperty("username")
    private String username;

    public GeoMessage() {
    }

    public GeoMessage(String message, double longitude, double latitude, float accuracy, String username) {
        this.message = message;
        this.longitude = longitude;
        this.latitude = latitude;
        this.accuracy = accuracy;
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "GeoMessage{" + "message='" + message + '\'' + ", longitude=" + longitude + ", latitude=" + latitude + ", accuracy=" + accuracy + ", username='" + username + '\'' + '}';
    }
}