package org.devproof.mubble.androidclient.bean;

import android.location.Location;
import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import org.apache.commons.lang.UnhandledException;
import org.devproof.mubble.androidclient.client.GeoMessageRestClient;
import org.devproof.mubble.dto.message.DisplayGeoMessage;
import org.devproof.mubble.dto.message.Result;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides the "java-service", service is in android already reserved
 * Avoid concurrent access to DB via synchronized methods
 *
 * Created At: 02.05.11 18:31
 *
 * @author Carsten Hufe
 */
@Singleton
public class GeoMessageBean {
    private static final String TAG = GeoMessageBean.class.getSimpleName();
    @Inject
    private Dao<DisplayGeoMessage, String> dao;
    @Inject
    private GeoMessageRestClient restClient;
    @Inject
    private LocationBean locationBean;

    /**
     * Submits a message and stores it in the local DB
     * @param content message
     * @return status and submited message
     * @throws BadQualityLocationException GPS quality to bad
     */
    public Result.Status submitMessage(String content) throws BadQualityLocationException {
        if(locationBean.isQualityAcceptableForComposing()) {
            Location location = locationBean.getCurrentLocation();
            Result result = restClient.composeMessage(location, content);
            Result.Status status = result.getStatus();
            if(status == Result.Status.ACCEPTED) {
                try {
                    synchronized (this) {
                        DisplayGeoMessage postedMessage = result.getPostedMessage();
                        DisplayGeoMessage queriedMessage = dao.queryForId(postedMessage.getId());
                        if(queriedMessage == null) {
                            dao.create(postedMessage);
                        }
                    }
                } catch (SQLException e) {
                    throw new UnhandledException(e);
                }
            }
            return status;
        }
        else {
            throw new BadQualityLocationException();
        }
    }

    /**
     * Syncs the messages from server to local DB, called by UpdaterService
     */
    public void syncGeoMessages() {
        try {
            List<DisplayGeoMessage> allAttachedMessages = retrieveMessages();
            // synchronized does a pseudo transaction
            synchronized (this) {
                List<DisplayGeoMessage> oldMessages = dao.queryBuilder().where().in("id", extractIds(allAttachedMessages)).query();
                dao.delete(dao.deleteBuilder().prepare());
                for (DisplayGeoMessage message : allAttachedMessages) {
                    message.setDisplayed(oldMessages.contains(message));
                    dao.create(message);
                    Log.d(TAG, "Create message: " + message);
                }
            }
        } catch (SQLException e) {
            throw new UnhandledException(e);
        }
    }

    /**
     * Loads all messages from DB ordery by distance group and date desc
     *
     * @return
     */
    public synchronized List<DisplayGeoMessage> findMessages() {
        try {
            QueryBuilder<DisplayGeoMessage,String> query = dao.queryBuilder().orderBy("distance_group", true).orderBy("posted_at", false);
            return query.query();
        } catch (SQLException e) {
            throw new UnhandledException(e);
        }
    }

    private List<DisplayGeoMessage> retrieveMessages() {
        return restClient.retrieveMessages(locationBean.getCurrentLocation());
    }

    private List<String> extractIds(List<DisplayGeoMessage> geoMessages) {
        List<String> result = new ArrayList<String>(geoMessages.size());
        for (DisplayGeoMessage geoMessage : geoMessages) {
            result.add(geoMessage.getId());
        }
        return result;
    }


}
