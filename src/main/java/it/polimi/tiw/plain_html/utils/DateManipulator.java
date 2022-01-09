package it.polimi.tiw.plain_html.utils;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateManipulator {

    private static DateManipulator instance;

    public static DateManipulator getInstance(){
        if (instance == null){
            instance = new DateManipulator();
        }
        return instance;
    }

    public String dateDifferenceToString(long milliseconds){
        String result = "";
        int seconds = (int) (milliseconds / 1000) % 60 ;
        if(seconds != 0) result = seconds + "s" + result;
        int minutes = (int) ((milliseconds / (1000*60)) % 60);
        if(minutes != 0) result = minutes + "m " +result;
        int hours   = (int) ((milliseconds / (1000*60*60)) % 24);
        if(hours != 0) result = hours + "h " +result;
        int days = (int) (milliseconds / (1000*60*60*24));
        if(days != 0) result = days + "D " +result;
        return result;
    }

    public String fancyDateToString(Date date){
        String pattern = "dd/MM/yyyy HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }
}
