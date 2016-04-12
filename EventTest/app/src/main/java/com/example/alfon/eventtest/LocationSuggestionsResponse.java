package com.example.alfon.eventtest;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by alfon on 2016-03-25.
 */
public class LocationSuggestionsResponse {
    @SerializedName("existing")
    public List<Location> existingLocations;

    @SerializedName("google")
    public List<Location> googleLocations;
}
