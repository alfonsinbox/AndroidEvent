package com.example.alfon.eventtest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by alfon on 2016-02-27.
 */
public class GoogleLocationPredictionsAdapter extends ArrayAdapter<Location> {
    public GoogleLocationPredictionsAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public GoogleLocationPredictionsAdapter(Context context, int resource, List<Location> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_item_google_location_prediction, null);
        }

        Location prediction = getItem(position);

        if (prediction != null) {
            TextView listItemDescription = (TextView) v.findViewById(R.id.description);

            if (listItemDescription != null) {
                listItemDescription.setText(prediction.longName);
            }
        }

        return v;
    }

}
