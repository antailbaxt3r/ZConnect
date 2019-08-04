package com.zconnect.zutto.zconnect.itemFormats;

import android.util.Log;

public class UserSeenNotifItemFormat {

    private long seenNotifications;
    private long totalNotifications;

    public UserSeenNotifItemFormat() {
    }

    public UserSeenNotifItemFormat(long seenNotifications, long totalNotifications) {
        this.seenNotifications = seenNotifications;
        this.totalNotifications = totalNotifications;
    }

    public long getSeenNotifications() {
        return seenNotifications;
    }

    public void setSeenNotifications(long seenNotifications) {
        this.seenNotifications = seenNotifications;
    }

    public long getTotalNotifications() {
        return totalNotifications;
    }

    public void setTotalNotificatons(long totalNotifications) {
        this.totalNotifications = totalNotifications;
    }

}
