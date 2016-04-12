package com.example.alfon.eventtest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by alfon on 2016-03-28.
 */
public class CategorySearchAdapter extends ArrayAdapter<Category> {

    public CategorySearchAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public CategorySearchAdapter(Context context, int resource, List<Category> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_item_category_search, null);
        }

        Category category = getItem(position);

        if (category != null) {
            TextView listItemFullName = (TextView) v.findViewById(R.id.category_name);
            CheckBox checkBoxUserSelected = (CheckBox) v.findViewById(R.id.category_is_selected);

            if (listItemFullName != null) {
                listItemFullName.setText(category.name);
            }
            if(category.category_is_selected){
                checkBoxUserSelected.setChecked(true);
            }else{
                checkBoxUserSelected.setChecked(false);
            }
        }

        return v;
    }

}
