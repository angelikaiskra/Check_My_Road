package com.iskra.googlemapstutorial;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Angelika Iskra on 04.04.2018.
 */
public class EventAdapter extends ArrayAdapter<Event> {

    public EventAdapter(@NonNull Context context, List<Event> events) {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.event_list_item, parent, false);
        }
        Event currentEvent = getItem(position);

        TextView eventTitle = (TextView) listItemView.findViewById(R.id.event_title);
        TextView eventIcon = (TextView) listItemView.findViewById(R.id.event_icon);
        setEventNameAndIcon(currentEvent.getType(), eventTitle, eventIcon);

        Date dateObject = new Date(currentEvent != null ? currentEvent.getDateEvent() : null);

        TextView dateView = (TextView) listItemView.findViewById(R.id.event_date);
        String formattedDate = formatDate(dateObject);
        dateView.setText(formattedDate);

        TextView timeView = (TextView) listItemView.findViewById(R.id.event_time);
        String formattedTime = formatTime(dateObject);
        timeView.setText(formattedTime);

        return listItemView;
    }

    private void setEventNameAndIcon(String type, TextView eventTitle, TextView eventIcon) {
        switch (type) {
            case "SpeedCamera": {
                eventTitle.setText(R.string.SpeedCamera);
                eventIcon.setBackgroundResource(R.drawable.ic_speed_camera);
                break;
            }
            case "TrafficJam": {
                eventTitle.setText(R.string.TrafficJam);
                eventIcon.setBackgroundResource(R.drawable.ic_traffic_jam);
                break;
            }
            case "PoliceControl": {
                eventTitle.setText(R.string.PoliceControl);
                eventIcon.setBackgroundResource(R.drawable.ic_police_control);
                break;
            }
            case "CarAccident": {
                eventTitle.setText(R.string.CarAccident);
                eventIcon.setBackgroundResource(R.drawable.ic_car_accident);
                break;
            }
            case "RoadBuilding": {
                eventTitle.setText(R.string.RoadBuilding);
                eventIcon.setBackgroundResource(R.drawable.ic_road_building);
                break;
            }
            case "Blockade": {
                eventTitle.setText(R.string.Blockade);
                eventIcon.setBackgroundResource(R.drawable.ic_blockade);
                break;
            }
            default: {
                eventTitle.setText("Zdarzenie");
            }
        }
    }

    public static String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    public static String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

}
