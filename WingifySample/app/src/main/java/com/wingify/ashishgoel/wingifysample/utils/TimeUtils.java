package com.wingify.ashishgoel.wingifysample.utils;

import android.text.format.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ashish Goel on 11/28/2015.
 */
public class TimeUtils {

    public static CharSequence getPostTime(Date date) {
        try {
            CharSequence simpleDate = DateUtils.getRelativeTimeSpanString(
                    date.getTime(), Calendar.getInstance().getTimeInMillis(),
                    DateUtils.MINUTE_IN_MILLIS);

            if (simpleDate.equals("0 minutes ago"))
                simpleDate = date.toString();

            return simpleDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Just Now";
    }
}
