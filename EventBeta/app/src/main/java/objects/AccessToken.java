package objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alfon on 2016-07-17.
 */
public class AccessToken {
    @SerializedName("access_token")
    public String token;

    @SerializedName("expires")
    public int expires;
}
