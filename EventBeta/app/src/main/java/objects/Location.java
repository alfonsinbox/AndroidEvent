package objects;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by alfon on 2016-02-18.
 */
public class Location implements Serializable{

    public String id;

    public float longitude;

    public float latitude;

    @SerializedName("short_name")
    public String shortName;

    @SerializedName("long_name")
    public String longName;

    public String city;

    public String country;

    public String description;

    public List<Event> events;

    public User createdBy;

    public String distance;
}
