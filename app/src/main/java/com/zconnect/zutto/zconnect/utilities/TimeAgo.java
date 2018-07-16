package com.zconnect.zutto.zconnect.utilities;

import android.util.Log;

/**
 * Created by akhiller on 25/3/18.
 */

public class TimeAgo {
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

    public TimeAgo() {

    }

    public TimeAgo(long postedTime, long currentTime) {
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
            return TimeAgo.JUST_NOW;
        }
        else if(span < 3600000) {
            Log.d("span time", String.valueOf(span));
            long x = span / 60000;
            return x == 1 ? TimeAgo.A_MIN_AGO : x + TimeAgo.X_MIN_AGO;
        }
        else if (span < 24 * 3600000) {
            long x = span / 3600000;
            return x == 1 ? TimeAgo.AN_HR_AGO : x + TimeAgo.X_HR_AGO;
        }
        else if (span < 30 * 24 * 3600000L) {
            long x = span / (24 * 3600000);
            return x == 1 ? TimeAgo.A_DAY_AGO : x + TimeAgo.X_DAY_AGO;
        }
        else if ((span / 1000) < 12 * 30 * 24 * 3600L) {
            long x = (span / 1000) / (30 * 24 * 3600L);
            return x == 1 ? TimeAgo.A_MTH_AGO : x + TimeAgo.X_MTH_AGO;
        }
        else {
            long x = (span / 1000) / (12 * 30 * 24 * 3600L);
            return x == 1 ? TimeAgo.A_YR_AGO : x + TimeAgo.X_YR_AGO;
        }
    }
}
