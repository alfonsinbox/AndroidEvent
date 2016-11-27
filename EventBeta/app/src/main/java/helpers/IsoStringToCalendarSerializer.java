package helpers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by alfon on 2016-03-15.
 */
public class IsoStringToCalendarSerializer implements JsonDeserializer<Calendar>, JsonSerializer<Calendar> {
    @Override
    public Calendar deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        // this is the place where you convert .NET timestamp into Java Date object
        try {
            Calendar calendar = Calendar.getInstance();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss\'Z\'");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date date = simpleDateFormat.parse(json.getAsString());
            calendar.setTime(date);

            return calendar;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public JsonElement serialize(Calendar src, Type typeOfSrc, JsonSerializationContext context) {
        try {
            TimeZone timeZone = TimeZone.getDefault();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ssZ");
            dateFormat.setTimeZone(timeZone);

            String nowAsISO = dateFormat.format(new Date(src.getTimeInMillis()));
            return new JsonPrimitive(nowAsISO);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
