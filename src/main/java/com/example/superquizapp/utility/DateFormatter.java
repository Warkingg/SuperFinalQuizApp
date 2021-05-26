package com.example.superquizapp.utility;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {
    public final static String dateFormat="yyyy-MM-dd";
    public final static String dateAndTimeFormat="yyy-MM-dd hh:mm:ss aa";

    public  static  String formatDate(Date date) {
        //Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        String strDate = formatter.format(date);
        System.out.println("Date Format with MM/dd/yyyy : " + strDate);
        return strDate;
    }

    public static  String formatDateAndTime(Date date) {
        // Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(dateAndTimeFormat);
        String  strDate = formatter.format(date);
        System.out.println("Date Format with dd-M-yyyy hh:mm:ss : " + strDate);
        return strDate;
    }
}
