package com.bedubs.rxfiller;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by bedubs on 3/31/17.
 *
 */

public class PatientInfo implements Parcelable {
    private String name = null;
    private String id = null;
    private List<PatientOrders> orders = null;

    public PatientInfo(String name, String id, List<PatientOrders> orders) {
        this.name = name;
        this.id = id;
        this.orders = orders;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    List<PatientOrders> getOrders() {
        return orders;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(id);
        out.writeList(orders);
    }

    public static final Parcelable.Creator<PatientInfo> CREATOR
            = new Parcelable.Creator<PatientInfo>() {
        public PatientInfo createFromParcel(Parcel in) {
            return new PatientInfo(in);
        }

        public PatientInfo[] newArray(int size) {
            return new PatientInfo[size];
        }
    };

    private PatientInfo(Parcel in) {
        name = in.readString();
        id = in.readString();
        in.readList(orders, ClassLoader.getSystemClassLoader());
    }
}
