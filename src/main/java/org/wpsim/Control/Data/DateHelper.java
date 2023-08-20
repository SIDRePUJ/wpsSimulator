package org.wpsim.Control.Data;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.wpsim.World.Helper.WorldConfiguration;

/**
 *
 * @author jairo
 */
public class DateHelper {

    /**
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int differenceDaysBetweenTwoDates(String date1, String date2) {
        WorldConfiguration config = WorldConfiguration.getPropsInstance();
        String dateFormat = config.getProperty("date.format");
        DateTimeFormatter formatter = DateTimeFormat.forPattern(dateFormat);
        DateTime dateTime1 = formatter.parseDateTime(date1);
        DateTime dateTime2 = formatter.parseDateTime(date2);
        return Days.daysBetween(dateTime1, dateTime2).getDays();
    }

    /**
     *
     * @param date
     * @return
     */
    public static DateTime getDateInJoda(String date) {
        WorldConfiguration config = WorldConfiguration.getPropsInstance();
        String dateFormat = config.getProperty("date.format");
        DateTimeFormatter formatter = DateTimeFormat.forPattern(dateFormat);
        return formatter.parseDateTime(date);
    }

    /**
     *
     * @param time
     * @return
     */
    public static String parseDateTimeToString(DateTime time) {
        WorldConfiguration config = WorldConfiguration.getPropsInstance();
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern(config.getProperty("date.format"));
        return dtfOut.print(time);
    }

    /**
     *
     * @param date
     * @return
     */
    public static int getMonthFromStringDate(String date) {
        return getDateInJoda(date).getMonthOfYear() - 1;
    }
}
