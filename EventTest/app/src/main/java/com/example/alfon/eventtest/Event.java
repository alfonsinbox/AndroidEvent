package com.example.alfon.eventtest;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by alfon on 2016-02-18.
 */
public class Event implements Serializable{

    public String id;

    public String name;

    public String description;

    @SerializedName("is_attending")
    public boolean isAttending;

    @SerializedName("is_creator")
    public boolean isCreator;

    @SerializedName("is_private")
    public String isPrivate;

    @SerializedName("is_canceled")
    public String isCanceled;

    @SerializedName("canceled_reason")
    public String canceledReason;

    @SerializedName("start_time")
    public Calendar startTime;

    @SerializedName("end_time")
    public Calendar endTime;

    @SerializedName("created_by")
    public User createdBy;

    public Location location;

    @SerializedName("location_id")
    public String locationId;

    @SerializedName("category_ids")
    public List<String> categoryIds;

    @SerializedName("host_ids")
    public List<String> hostIds;

    public List<Category> categories;
}
