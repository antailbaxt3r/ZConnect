package com.zconnect.zutto.zconnect.utilities;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Created by akhiller on 25/3/18.
 */

public class TimeUtilities {
    private long postedTime, currentTime;

    private static String JUST_NOW = "Just now",
        A_MIN_AGO = "1 minute ago",
        X_MIN_AGO = " minutes ago",
        AN_HR_AGO = "1 hour ago",
        X_HR_AGO = " hours ago",
        A_DAY_AGO = "1 day ago",
        X_DAY_AGO = " days ago",
        A_MTH_AGO = "1 month ago",
        X_MTH_AGO = " months ago",
        A_YR_AGO = "1 year ago",
        X_YR_AGO = " years ago";

    public TimeUtilities() {

    }

    public TimeUtilities(long postedTime, long currentTime) {
        this.postedTime = postedTime;
        this.currentTime = currentTime;
    }

    public String calculateTimeAgo() {
        long span = this.currentTime - this.postedTime;
        Log.d("currentTime", String.valueOf(this.currentTime));
        Log.d("postedTime", String.valueOf(this.postedTime));
        Log.d("span time", String.valueOf(span));
        if(span >=0 && span < 60000)
        {
            return TimeUtilities.JUST_NOW;
        }
        else if(span < 3600000) {
            Log.d("span time", String.valueOf(span));
            long x = span / 60000;
            return x == 1 ? TimeUtilities.A_MIN_AGO : x + TimeUtilities.X_MIN_AGO;
        }
        else if (span < 24 * 3600000) {
            long x = span / 3600000;
            return x == 1 ? TimeUtilities.AN_HR_AGO : x + TimeUtilities.X_HR_AGO;
        }
        else if (span < 30 * 24 * 3600000L) {
            long x = span / (24 * 3600000);
            return x == 1 ? TimeUtilities.A_DAY_AGO : x + TimeUtilities.X_DAY_AGO;
        }
        else if ((span / 1000) < 12 * 30 * 24 * 3600L) {
            long x = (span / 1000) / (30 * 24 * 3600L);
            return x == 1 ? TimeUtilities.A_MTH_AGO : x + TimeUtilities.X_MTH_AGO;
        }
        else {
            long x = (span / 1000) / (12 * 30 * 24 * 3600L);
            return x == 1 ? TimeUtilities.A_YR_AGO : x + TimeUtilities.X_YR_AGO;
        }
    }

    public String calculateTimeAgoStoreroom() {
        String timeAgo = calculateTimeAgo();
        if(!timeAgo.contains("day") && !timeAgo.contains("month") && !timeAgo.contains("year"))
        {
            timeAgo = "New";
        }
        else
        {
            timeAgo = timeAgo.substring(0, timeAgo.indexOf(" ago"));
            if(timeAgo.contains("month"))
                timeAgo = timeAgo.replace("month", "mth");
            else if(timeAgo.contains("year"))
                timeAgo = timeAgo.replace("year", "yr");
        }
        return timeAgo;
    }

    public String getTimeStamp(long ts1)
    {
        DateTimeZone indianZone = DateTimeZone.forID("Asia/Kolkata");
        DateTime dt1 = new DateTime(ts1, indianZone);
        DateTime dt2 = new DateTime(indianZone);
        boolean sameDay = (dt1.getYearOfEra() == dt2.getYearOfEra()) && (dt1.getMonthOfYear() == dt2.getMonthOfYear()) && (dt1.getDayOfMonth() == dt2.getDayOfMonth());
        if(sameDay){
            return "";
        }
        else
        {
            if(dt1.getYearOfEra()==dt2.getYearOfEra() && dt1.getMonthOfYear() == dt2.getMonthOfYear() && dt1.getDayOfMonth() == dt2.getDayOfMonth())
            {
                return "TODAY";
            }
            else if(dt1.getYearOfEra()==dt2.getYearOfEra() && dt1.getMonthOfYear() == dt2.getMonthOfYear() && dt1.getDayOfMonth() == dt2.getDayOfMonth()-1)
            {
                return "YESTERDAY";
            }
            else {
                return dt1.getDayOfMonth() + "/" + dt1.getMonthOfYear() + "/" + dt1.getYearOfCentury();
            }
        }
    }
}
