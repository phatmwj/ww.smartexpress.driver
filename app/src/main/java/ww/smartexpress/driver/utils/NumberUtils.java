package ww.smartexpress.driver.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import ww.smartexpress.driver.constant.Constants;

public class NumberUtils {
    static DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    static DecimalFormat decimalFormat;

    public static String formatNumber(Double number) {
        symbols.setGroupingSeparator(Constants.SYMBOLS);
        decimalFormat = new DecimalFormat("#,###", symbols);
        return decimalFormat.format(number);
    }

    public static String formatCurrency(double d)
    {

        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        decimalFormatSymbols.setGroupingSeparator('.');

        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
        decimalFormat.setMaximumFractionDigits(0);
        decimalFormat.setMinimumFractionDigits(0);

        return decimalFormat.format(d) +" "+ "đ";
    }

    public static String formatDistance(double d){
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        decimalFormatSymbols.setGroupingSeparator('.');

        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
        decimalFormat.setMaximumFractionDigits(1);
        decimalFormat.setMinimumFractionDigits(1);

        return decimalFormat.format((double) d/1000) +" "+ "Km";
    }
    public static String formatNumberStar(int star){
        double d = (double) star;
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        decimalFormatSymbols.setGroupingSeparator('.');

        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
        decimalFormat.setMaximumFractionDigits(1);
        decimalFormat.setMinimumFractionDigits(1);

        return decimalFormat.format(d) ;
    }
}
