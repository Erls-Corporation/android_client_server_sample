package org.devproof.mubble.server.entity;

import com.google.code.morphia.annotations.*;
import com.google.code.morphia.utils.IndexDirection;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.Date;

/**
 * Persisted message in MongoDB
 *
 * @author Carsten Hufe
 */
@Entity(value = "message", noClassnameStored = true)
public class Message implements Serializable {
    @Id
    private ObjectId id;
    @Property("posted_at")
    @Indexed(IndexDirection.DESC)
    private Date postedAt;
    @Property("ip_address")
    private String ipAddress;
    @Property("message")
    private String message;
    @Property("username")
    private String username;
    @Property("identifier")
    @Indexed
    private String identifier;
    @Indexed(IndexDirection.GEO2D)
    protected double[] location = new double[2];
    @Property("accuracy")
    @Indexed
    private float accuracy;
    // TODO votes?

    public String getIdString() {
        if(id != null) {
            return id.toStringMongod();
        }
        return "";
    }

    public ObjectId getId() {
        return id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Date getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(Date postedAt) {
        this.postedAt = postedAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getLongitude() {
        return location[0];
    }

    public void setLongitude(double longitude) {
        this.location[0] = longitude;
    }

    public double getLatitude() {
        return location[1];
    }

    public void setLatitude(double latitude) {
        this.location[1] = latitude;
    }

    public double[] getLocation() {
        return location;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public String toString() {
        return "Message{" + "id=" + id + ", postedAt=" + postedAt + ", ipAddress='" + ipAddress + '\'' + ", message='" + message + '\'' + ", username='" + username + '\'' + ", location=" + location + ", accuracy=" + accuracy + '}';
    }
}
