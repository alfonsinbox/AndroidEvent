package objects;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Observable;

/**
 * Created by alfon on 2016-02-18.
 */
public class User implements Serializable{
    public String id;

    @SerializedName("first_name")
    public String firstName;

    @SerializedName("last_name")
    public String lastName;
    public String username;

    @SerializedName("full_name")
    public String fullName;
    public String password;
    public String email;
    public String phoneNumber;

    @SerializedName("birth_date")
    public Calendar birthDate;

    @SerializedName("profile_picture_url")
    public String profilePictureUrl;

    public List<Category> interests;

    public boolean user_is_selected;
}
