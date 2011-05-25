package org.devproof.mubble.androidclient.activity;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.google.inject.Inject;
import org.devproof.mubble.androidclient.R;
import org.devproof.mubble.androidclient.bean.GeoMessageBean;
import org.devproof.mubble.dto.message.DisplayGeoMessage;
import roboguice.inject.ContextScoped;

import java.util.List;

/**
 * @author Carsten Hufe
 */
@ContextScoped
public class GeoMessageAdapter extends BaseAdapter {
    @Inject
    private GeoMessageBean geoMessageBean;
    @Inject
    private Context context;
    private List<DisplayGeoMessage> messages;

    @Override
    public int getCount() {
        return getMessages().size();
    }

    @Override
    public Object getItem(int i) {
        return getMessages().get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public void notifyDataSetChanged() {
        messages = geoMessageBean.findMessages();
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = getInflatedView(convertView);
        final DisplayGeoMessage message = getMessages().get(position);
        if (message != null) {
            TextView distanceGroup = (TextView) v.findViewById(R.id.timelineDistanceGroup);
            String distanceText = context.getString(R.string.timelineDistanceGroup, message.getDistanceInMeterGroup());
            distanceGroup.setText(distanceText);
            if(position == 0 || isNewDistanceGroup(message, position)) {
                distanceGroup.setVisibility(View.VISIBLE);
            }
            else {
                distanceGroup.setVisibility(View.GONE);
            }
            TextView username = (TextView) v.findViewById(R.id.timelineItemUsername);
            username.setText(message.getUsername());
            TextView content = (TextView) v.findViewById(R.id.timelineItemContent);
            content.setText(message.getMessage());
            TextView postedAt = (TextView) v.findViewById(R.id.timelineItemPostedAt);
            CharSequence relativeDateTimeString = DateUtils.getRelativeDateTimeString(context, message.getPostedAt().getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL);
            postedAt.setText(relativeDateTimeString);
            TextView distance = (TextView) v.findViewById(R.id.timelineItemDistance);
            distance.setText(Math.round(message.getDistanceInMeter()) + context.getString(R.string.timelineDistance));
            View timelineMainContent = v.findViewById(R.id.timelineMainContent);
            timelineMainContent.setBackgroundColor(0x00000000);
            if(!message.isDisplayed()) {
                timelineMainContent.setBackgroundColor(0x55CCCCEE);
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.scale);
                timelineMainContent.setAnimation(animation);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        message.setDisplayed(true);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }
        }
        return v;
    }

    private boolean isNewDistanceGroup(DisplayGeoMessage currentMessage, int position) {
        DisplayGeoMessage previousMessage = getMessages().get(position - 1);
        return currentMessage.getDistanceInMeterGroup() != previousMessage.getDistanceInMeterGroup();
    }

    private List<DisplayGeoMessage> getMessages() {
        if(messages == null) {
            messages = geoMessageBean.findMessages();
        }
        return messages;
    }

    private View getInflatedView(View convertView) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = LayoutInflater.from(context);
            v = vi.inflate(R.layout.timeline_item, null);
        }
        return v;
    }
}
