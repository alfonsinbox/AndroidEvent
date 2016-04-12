package com.example.alfon.eventtest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by alfon on 2016-03-18.
 */
public class EventsListAdapter extends ArrayAdapter<Event> {
    public EventsListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public EventsListAdapter(Context context, int resource, List<Event> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.grid_item_events, null);
        }

        Event event = getItem(position);

        if (event != null) {
            TextView listItemDescription = (TextView) v.findViewById(R.id.description);

            if (listItemDescription != null) {
                listItemDescription.setText(event.name);
            }
        }

        return v;
    }

}
