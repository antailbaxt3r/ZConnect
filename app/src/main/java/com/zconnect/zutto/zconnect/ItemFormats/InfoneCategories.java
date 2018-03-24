package com.zconnect.zutto.zconnect.ItemFormats;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shubhamk on 23/3/18.
 */

public class InfoneCategories implements Parcelable {
    public static final Parcelable.Creator<InfoneCategories> CREATOR = new Parcelable.Creator<InfoneCategories>() {
        public InfoneCategories createFromParcel(Parcel in) {
            return new InfoneCategories(in);
        }

        public InfoneCategories[] newArray(int size) {

            return new InfoneCategories[size];
        }

    };
    String title;
    String memberno;

    public InfoneCategories(Parcel in) {
        super();
        readFromParcel(in);
    }

    public InfoneCategories(String title, String memberno) {
        this.title = title;
        this.memberno = memberno;
    }

    public InfoneCategories() {
    }

    private void readFromParcel(Parcel in) {
        this.title = in.readString();
        this.memberno = in.readString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMemberno() {
        return memberno;
    }

    public void setMemberno(String memberno) {
        this.memberno = memberno;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.memberno);
    }
}
