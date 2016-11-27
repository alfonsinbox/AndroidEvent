package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.alfonslange.eventbeta.R;

import java.util.List;

import objects.User;

/**
 * Created by alfon on 2016-02-29.
 */
public class UserSearchAdapter extends ArrayAdapter<User> {
    public UserSearchAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public UserSearchAdapter(Context context, int resource, List<User> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_item_user_search, null);
        }

        User user = getItem(position);

        if (user != null) {
            TextView listItemFullName = (TextView) v.findViewById(R.id.user_full_name);
            TextView listItemUsername = (TextView) v.findViewById(R.id.user_username);
            CheckBox checkBoxUserSelected = (CheckBox) v.findViewById(R.id.user_is_selected);

            if (listItemFullName != null) {
                listItemFullName.setText(user.fullName);
            }
            if (listItemUsername != null) {
                listItemUsername.setText(user.username);
            }
            if(user.user_is_selected){
                checkBoxUserSelected.setChecked(true);
            }else{
                checkBoxUserSelected.setChecked(false);
            }
        }

        return v;
    }

}
