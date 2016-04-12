package com.example.alfon.eventtest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by alfon on 2016-03-27.
 */
public class ExistingLocationPredictionsAdapter extends ArrayAdapter<Location> {

    public ExistingLocationPredictionsAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ExistingLocationPredictionsAdapter(Context context, int resource, List<Location> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_item_existing_location_prediction, null);
        }

        Location prediction = getItem(position);

        if (prediction != null) {
            TextView listItemDescription = (TextView) v.findViewById(R.id.description);
            TextView listItemDistance = (TextView) v.findViewById(R.id.distance);

            if (listItemDescription != null) {
                listItemDescription.setText(prediction.longName);
            }
            if (listItemDistance != null) {
                listItemDistance.setText(prediction.distance);
            }
        }

        return v;
    }

}
