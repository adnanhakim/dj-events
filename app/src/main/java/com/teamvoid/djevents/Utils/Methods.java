package com.teamvoid.djevents.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Methods {
    public static String formatTimestamp(Date date) {
        SimpleDateFormat sfd = new SimpleDateFormat(Constants.DATE_FORMAT_PATTERN, Locale.US);
        return formatDates(sfd.format(date), sfd.format(new Date()));

    }

    private static String formatDates(String sDate, String eDate) {
        SimpleDateFormat sfd = new SimpleDateFormat(Constants.DATE_FORMAT_PATTERN, Locale.US);

        Date startDate = null;
        Date endDate = null;
        try {
            startDate = sfd.parse(sDate);
            endDate = sfd.parse(eDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assert endDate != null;
        assert startDate != null;
        long duration = endDate.getTime() - startDate.getTime();

        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
        long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);

        if (diffInMinutes < 1 && diffInDays == 0) return "Just now";

        if (diffInDays == 0 && diffInHours == 0) {
            if (diffInMinutes <= 1) return "Just now";
            else return diffInMinutes + " m";
        } else if (diffInDays == 0 && diffInHours >= 1 && diffInHours < 24) {
            return diffInHours + " h";
        } else if (diffInDays < 7) return diffInDays + " d";
        else if ((int) (diffInDays / 7) <= 24) return ((int) (diffInDays / 7)) + " w";
        else return sDate.substring(0, 10);
    }

    public static String formatDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        StringBuilder builder = new StringBuilder();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        builder.append(day);
        if (day == 1 || day == 21 || day == 31)
            builder.append("st ");
        else if (day == 2 || day == 22)
            builder.append("nd ");
        else if (day == 3 || day == 23)
            builder.append("rd ");
        else builder.append("th ");

        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        int month = calendar.get(Calendar.MONTH);
        if (month > 0 && month < 12)
            builder.append(months[month]).append(" ");

        builder.append(calendar.get(Calendar.YEAR));
        return builder.toString();
    }
}
