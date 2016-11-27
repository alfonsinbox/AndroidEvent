package helpers;

import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by alfon on 2016-02-24.
 */
public class DateUtilities {
    /**
     * Returns a Calendar instance from
     * a provided date.
     * @param date
     * @return
     */
    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    /**
     * Calculates the difference in year
     * between the two provided dates.
     * @param first
     * @param last
     * @return
     */
    public static int differenceBetweenDatesInYears(Date first, Date last){
        Calendar firstCal = getCalendar(first);
        Calendar lastCal = getCalendar(last);
        int diff = lastCal.get(Calendar.YEAR) - firstCal.get(Calendar.YEAR);
        if (firstCal.get(Calendar.MONTH) > lastCal.get(Calendar.MONTH) ||
                (firstCal.get(Calendar.MONTH) == lastCal.get(Calendar.MONTH) && firstCal.get(Calendar.DATE) > lastCal.get(Calendar.DATE))) {
            diff--;
        }
        return diff;
    }

    public static int differenceBetweenDatesInYears(Calendar first, Calendar last){
        int diff = last.get(Calendar.YEAR) - first.get(Calendar.YEAR);
        if (first.get(Calendar.MONTH) > last.get(Calendar.MONTH) ||
                (first.get(Calendar.MONTH) == last.get(Calendar.MONTH) && first.get(Calendar.DATE) > last.get(Calendar.DATE))) {
            diff--;
        }
        return diff;
    }

    /**
     * Creates a formatted string from a
     * provided date and string format.
     * @param date
     * @param format
     * @return
     */
    public static String dateToStringFormat(Date date, String format){
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * Creates a formatted string from a
     * provided calendar and string format.
     * @param calendar
     * @param format
     * @return
     */
    public static String dateToStringFormat(Calendar calendar, String format){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(calendar.getTime());
    }

    public static Date dateFromDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);

        return calendar.getTime();
    }

    public static Calendar calendarFromDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);

        return calendar;
    }
}
