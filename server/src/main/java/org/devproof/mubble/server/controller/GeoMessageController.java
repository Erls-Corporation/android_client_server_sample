package org.devproof.mubble.server.controller;

import com.google.code.morphia.query.Query;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.bson.types.ObjectId;
import org.devproof.mubble.dto.message.ComposeGeoMessage;
import org.devproof.mubble.dto.message.DisplayGeoMessage;
import org.devproof.mubble.dto.message.Result;
import org.devproof.mubble.server.entity.Message;
import org.devproof.mubble.server.repository.MessageRepository;
import org.devproof.mubble.util.DistanceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Main controller for geo graphic requests
 *
 * Created At: 08.04.11 14:41
 *
 * @author Carsten Hufe
 */
@Controller
public class GeoMessageController {

    private static int DISTANCE_GROUPS[] = new int[]{20, 50, 100, 200, 500, 1000, 5000};
    //    private static double MAX_RANGE_IN_KM = 5;
//    private static double EARTH_RADIUS_IN_KM = 6378;
//    private static double MAX_DISTANCE_RADIAN = MAX_RANGE_IN_KM / EARTH_RADIUS_IN_KM;
    private static int MAX_MESSAGE_SIZE = 200;
    private static double DISTANCE_FACTOR = 1d / 111.1895769599889d / 1000d;
    private static float MAX_ACCURACY_METER = 1000;

    @Autowired
    private MessageRepository messageRepository;

    /**
     * Returns the appropriate messages
     *
     * @param longitude longitude = x
     * @param latitude latitude = y
     * @param accuracy accuracy in meter, its limited to 1000m to avoid a full download ...
     * @param limit maximum request messages, its limited to 200 to avoid a full download ...
     * @returnfound messages
     */
    @RequestMapping(value = "/messages/{longitude}/{latitude}/{accuracy}/{limit}", method = RequestMethod.GET)
    public @ResponseBody List<DisplayGeoMessage> listGeoMessages(@PathVariable double longitude, @PathVariable double latitude,
                                            @PathVariable float accuracy, @PathVariable int limit) {
        // http://www.mongodb.org/display/DOCS/Geospatial+Indexing
        List<DisplayGeoMessage> convertedMessages = new ArrayList<DisplayGeoMessage>();
        List<ObjectId> foundIds = new ArrayList<ObjectId>();
        int correctedLimit = limit < MAX_MESSAGE_SIZE ? limit : MAX_MESSAGE_SIZE;
        float correctedAccuracy = accuracy < MAX_ACCURACY_METER ? accuracy : MAX_ACCURACY_METER;
        for (int group : DISTANCE_GROUPS) {
            double factor = (DISTANCE_FACTOR * ((double) group + correctedAccuracy));
            Query<Message> query = messageRepository.createQuery();
            query.field("location").near(longitude, latitude, factor);
            if(!foundIds.isEmpty()) {
                query.field("id").notIn(foundIds);
            }
            // TODO limit wird vorm order ausgeführt ... was soll der mist?
            int currentMaximum = correctedLimit - convertedMessages.size();
//            query.limit(currentMaximum);
            query.order("-postedAt");
            List<Message> messages = query.asList();
            currentMaximum = currentMaximum > messages.size() ? messages.size() : currentMaximum;
            messages = messages.subList(0, currentMaximum);
            for (Message message : messages) {
                double distanceMeter = DistanceUtil.distanceInMeter(longitude, latitude, message.getLongitude(), message.getLatitude());
                if (distanceMeter > group) {
                    distanceMeter = group;
                }
                DisplayGeoMessage displayGeoMessage = createDisplayGeoMessage(message, group, distanceMeter);
                convertedMessages.add(displayGeoMessage);
                foundIds.add(message.getId());
            }
            if (convertedMessages.size() >= correctedLimit) {
                break;
            }
        }
        if(convertedMessages.isEmpty()) {
            String msg = "No messages. Be the first one who writes a post for this place. Just enter a message and press \"Post\" ;-)";
            String username = "Mubble";
            Date postedAt = DateUtils.addMinutes(new Date(), -2);
            convertedMessages.add(new DisplayGeoMessage("welcome", msg, longitude, latitude, accuracy, username, postedAt, DISTANCE_GROUPS[0], 1d));
        }
        return convertedMessages;
    }

    /**
     * Save a new message
     *
     * @param composeGeoMessage message to post
     * @param request required for IP
     * @return Status and Composed message
     */
    @RequestMapping(value = "/messages", method = RequestMethod.POST)
    public @ResponseBody Result composeMessage(@RequestBody ComposeGeoMessage composeGeoMessage, HttpServletRequest request) {
        Result.Status status = validate(composeGeoMessage);
        Message message = new Message();
        message.setMessage(StringUtils.replaceChars(composeGeoMessage.getMessage(), '\n', ' '));
        message.setPostedAt(new Date());
        message.setLatitude(composeGeoMessage.getLatitude());
        message.setLongitude(composeGeoMessage.getLongitude());
        message.setAccuracy(composeGeoMessage.getAccuracy());
        message.setUsername(StringUtils.replaceChars(composeGeoMessage.getUsername(), '\n', ' '));
        message.setIpAddress(request.getRemoteAddr());
        message.setIdentifier(composeGeoMessage.getIdentifier());
        if(status == Result.Status.ACCEPTED) {
            messageRepository.save(message);
        }
        return new Result(status, createDisplayGeoMessage(message, DISTANCE_GROUPS[0], 0));
    }

    private long findMessageCountForLast5MinutesForIdentifier(String identifier) {
        Query<Message> query = messageRepository.createQuery();
        query.field("identifier").equal(identifier);
        Date val = DateUtils.addMinutes(new Date(), -5);
        query.field("postedAt").greaterThan(val);
        return query.countAll();
    }

    private boolean isDuplicateMessage(String identifier, String message) {
        String escapedMessage = StringUtils.replace(message, "*", "\\*");
        escapedMessage = StringUtils.replace(escapedMessage, "$", "\\$");
        escapedMessage = StringUtils.replace(escapedMessage, "?", "\\?");
        escapedMessage = StringUtils.replace(escapedMessage, "!", "\\!");
        Query<Message> query = messageRepository.createQuery();
        query.field("identifier").equal(identifier);
        query.field("message").endsWithIgnoreCase(escapedMessage);
        Date val = DateUtils.addMinutes(new Date(), -5);
        query.field("postedAt").greaterThan(val);
        return query.countAll() >  0;
    }

    private Result.Status validate(ComposeGeoMessage composeGeoMessage) {
        if(StringUtils.isBlank(composeGeoMessage.getMessage())) {
            return Result.Status.MESSAGE_TO_SHORT;
        }
        else if(StringUtils.length(composeGeoMessage.getMessage()) > 200) {
            return Result.Status.MESSAGE_TO_LONG;
        }
        else if(StringUtils.isBlank(composeGeoMessage.getIdentifier())) {
            return Result.Status.MISSING_IDENTIFIER;
        }
        else if(StringUtils.length(composeGeoMessage.getUsername()) < 3) {
            return Result.Status.USERNAME_TOO_SHORT;
        }
        else if(StringUtils.length(composeGeoMessage.getUsername()) > 16) {
            return Result.Status.USERNAME_TO_LONG;
        }
        else if(findMessageCountForLast5MinutesForIdentifier(composeGeoMessage.getIdentifier()) > 20) {
            return Result.Status.TOO_MANY_REQUESTS;
        }
        else if(isDuplicateMessage(composeGeoMessage.getIdentifier(), composeGeoMessage.getMessage())) {
            return Result.Status.DUPLICATE_MESSAGE;
        }
        return Result.Status.ACCEPTED;
    }

    private DisplayGeoMessage createDisplayGeoMessage(Message message, int distanceInMeterGroup, double distanceInMeter) {
        return new DisplayGeoMessage(message.getIdString(), message.getMessage(), message.getLongitude(), message.getLatitude(), message.getAccuracy(), message.getUsername(), message.getPostedAt(), distanceInMeterGroup, distanceInMeter);
    }
}
