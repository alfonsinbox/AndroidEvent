package helpers;

import java.util.Calendar;

/**
 * Created by alfonslange on 19/09/16.
 */
public class EventDataBindingMethods {
    public static String formatDateStartToEnd(Calendar startDate, Calendar endDate) {
        if (startDate.get(Calendar.YEAR) != endDate.get(Calendar.YEAR)) {
            return String.format("%s - %s",
                    DateUtilities.dateToStringFormat(startDate, "dd MMMM YYYY"),
                    DateUtilities.dateToStringFormat(endDate, "dd MMMM YYYY"));
        } else if (startDate.get(Calendar.YEAR) != Calendar.getInstance().get(Calendar.YEAR)) {
            if (startDate.get(Calendar.MONTH) != endDate.get(Calendar.MONTH)) {
                return String.format("%s - %s",
                        DateUtilities.dateToStringFormat(startDate, "dd MMMM"),
                        DateUtilities.dateToStringFormat(endDate, "dd MMMM YYYY"));
            } else if (startDate.get(Calendar.DAY_OF_MONTH) != endDate.get(Calendar.DAY_OF_MONTH)) {
                return String.format("%s - %s",
                        DateUtilities.dateToStringFormat(startDate, "dd"),
                        DateUtilities.dateToStringFormat(endDate, "dd MMMM YYYY"));
            } else {
                return DateUtilities.dateToStringFormat(startDate, "dd MMMM YYYY");
            }
        } else if (startDate.get(Calendar.MONTH) != endDate.get(Calendar.MONTH)) {
            return String.format("%s - %s",
                    DateUtilities.dateToStringFormat(startDate, "dd MMMM"),
                    DateUtilities.dateToStringFormat(endDate, "dd MMMM"));
        } else if (startDate.get(Calendar.DAY_OF_MONTH) != endDate.get(Calendar.DAY_OF_MONTH)) {
            return String.format("%s - %s",
                    DateUtilities.dateToStringFormat(startDate, "dd"),
                    DateUtilities.dateToStringFormat(endDate, "dd MMMM"));
        } else {
            return DateUtilities.dateToStringFormat(startDate, "dd MMMM");
        }
    }

    public static String formatTimeStartToEnd(Calendar startDate, Calendar endDate) {
        return String.format("%s - %s",
                DateUtilities.dateToStringFormat(startDate, "HH:mm"),
                DateUtilities.dateToStringFormat(endDate, "HH:mm"));
    }
}
