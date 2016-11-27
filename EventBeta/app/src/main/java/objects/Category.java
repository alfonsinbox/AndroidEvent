package objects;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by alfon on 2016-03-28.
 */
public class Category implements Serializable {
    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    public boolean category_is_selected;
}
