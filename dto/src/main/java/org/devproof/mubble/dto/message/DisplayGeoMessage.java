package org.devproof.mubble.dto.message;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

/**
 * DTO for transfering the displayed messages
 * The same class gets persisted by OrmLite in Android ... too lazy to map it twice
 *
 * @author Carsten Hufe
 */
@DatabaseTable(tableName = "geomessage")
public class DisplayGeoMessage extends GeoMessage {

    @DatabaseField(columnName = "id", id = true)
    @JsonProperty("id")
    private String id;

    @DatabaseField(columnName = "posted_at")
    @JsonProperty("posted_at")
    private Date postedAt;

    @DatabaseField(columnName = "distance_group")
    @JsonProperty("distanceInMeterGroup")
    private int distanceInMeterGroup;

    @DatabaseField(columnName = "distance_in_meter")
    @JsonProperty("distanceInMeter")
    private double distanceInMeter;
    @DatabaseField(columnName = "displayed")
    private boolean displayed = false;

    public DisplayGeoMessage() {
    }

    public DisplayGeoMessage(String id, String message, double longitude, double latitude, float accuracy, String username, Date postedAt, int distanceInMeterGroup, double distanceInMeter) {
        super(message, longitude, latitude, accuracy, username);
        this.id = id;
        this.postedAt = postedAt;
        this.distanceInMeterGroup = distanceInMeterGroup;
        this.distanceInMeter = distanceInMeter;
    }

    public Date getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(Date postedAt) {
        this.postedAt = postedAt;
    }

    public int getDistanceInMeterGroup() {
        return distanceInMeterGroup;
    }

    public void setDistanceInMeterGroup(int distanceInMeterGroup) {
        this.distanceInMeterGroup = distanceInMeterGroup;
    }

    public double getDistanceInMeter() {
        return distanceInMeter;
    }

    public void setDistanceInMeter(double distanceInMeter) {
        this.distanceInMeter = distanceInMeter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isDisplayed() {
        return displayed;
    }

    public void setDisplayed(boolean displayed) {
        this.displayed = displayed;
    }

    @Override
    public String toString() {
        return "DisplayGeoMessage{" + "id='" + id + '\'' + ", postedAt=" + postedAt + ", distanceInMeterGroup=" + distanceInMeterGroup + ", distanceInMeter=" + distanceInMeter + ", displayed=" + displayed + "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DisplayGeoMessage)) return false;

        DisplayGeoMessage that = (DisplayGeoMessage) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
