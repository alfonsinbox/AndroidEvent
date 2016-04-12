package com.example.alfon.eventtest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by alfon on 2016-03-20.
 */
public class EventSearchAdapter extends ArrayAdapter<Event>{
    public EventSearchAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public EventSearchAdapter(Context context, int resource, List<Event> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_item_event_search, null);
        }

        Event event = getItem(position);

        if (event != null) {
            TextView listItemName = (TextView) v.findViewById(R.id.event_name);
            TextView listItemCreator = (TextView) v.findViewById(R.id.event_creator);

            if (listItemName != null) {
                listItemName.setText(event.name);
            }
            if (listItemCreator != null) {
                listItemCreator.setText(String.format("%s (%s)", event.createdBy.fullName, event.createdBy.username));
            }
        }

        return v;
    }


}
