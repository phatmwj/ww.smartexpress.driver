package ww.smartexpress.driver.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import ww.smartexpress.driver.constant.Constants;

public class DateUtils {
    static SimpleDateFormat sdf = new SimpleDateFormat(Constants.dateFormat);

    public static String formatDate(Date date){
        return sdf.format(date);
    }

    public static String dateFormat(String dateString){
        if(dateString == null){
            return "";
        }
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        outputFormat.setTimeZone(TimeZone.getDefault());
        Date date = null;
        try {
            date = inputFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputFormat.format(date);
    }

    public static String dateOnlyFormat(String dateString){
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = inputFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputFormat.format(date);
    }

    public static String dateStartFormat(Date date){
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String dateString = inputDateFormat.format(date);
        // Định dạng lại thời gian với định dạng mong muốn
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date adjustedDate = null;
        try {
            Date inputDate = inputDateFormat.parse(dateString);

            // Đặt giờ, phút, giây về 00:00:00
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(inputDate);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            // Lấy thời gian đã điều chỉnh
            adjustedDate = calendar.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputDateFormat.format(adjustedDate);
    }

    public static String dateEndFormat(Date date){
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String dateString = inputDateFormat.format(date);
        // Định dạng lại thời gian với định dạng mong muốn
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date adjustedDate = null;
        try {
            Date inputDate = inputDateFormat.parse(dateString);

            // Đặt giờ, phút, giây về 00:00:00
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(inputDate);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            // Lấy thời gian đã điều chỉnh
            adjustedDate = calendar.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputDateFormat.format(adjustedDate);
    }

    public static String startWeekFormat(Date date){
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        // Lấy Calendar và thiết lập thời gian
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Đặt giờ, phút, giây, mili giây về 0 để lấy ngày đầu tiên của tuần
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Đặt ngày trong tuần về ngày đầu tiên
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

        // Lấy ngày đầu tiên của tuần
        Date firstDayOfWeek = calendar.getTime();

        // In ra kết quả
        System.out.println("Ngày đầu tiên của tuần: " + firstDayOfWeek);
        return outputDateFormat.format(firstDayOfWeek);
    }

    public static String endWeekFormat(Date date){
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek()+6);

        Date firstDayOfWeek = calendar.getTime();

        return outputDateFormat.format(firstDayOfWeek);
    }

    public static String startMonthFormat(Date date){
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        calendar.set(Calendar.DAY_OF_MONTH, 1);

        Date firstDayOfWeek = calendar.getTime();

        return outputDateFormat.format(firstDayOfWeek);
    }

    public static String endMonthFormat(Date date){
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

        Date firstDayOfWeek = calendar.getTime();

        return outputDateFormat.format(firstDayOfWeek);
    }

    public static String convertToUTC(String dateString){
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        outputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = inputFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputFormat.format(date);
    }
}
