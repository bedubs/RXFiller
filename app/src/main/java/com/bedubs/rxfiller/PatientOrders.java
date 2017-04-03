package com.bedubs.rxfiller;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bedubs on 4/2/17.
 *
 */

class PatientOrders implements Parcelable {
    private String medicine;
    private String dosage;
    private String refillsRemaining;
    private String lastRefill;

    PatientOrders(String medicine, String dosage, String refillsRemaining, String lastRefill) {
        this.medicine = medicine;
        this.dosage = dosage;
        this.refillsRemaining = refillsRemaining;
        this.lastRefill = lastRefill;
    }

    String getMedicine() {
        return medicine;
    }

    public String getDosage() {
        return dosage;
    }

    public String getRefillsRemaining() {
        return refillsRemaining;
    }

    public String getLastRefill() {
        return lastRefill;
    }

    public void setMedicine(String medicine) {
        this.medicine = medicine;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public void setRefillsRemaining(String refillsRemaining) {
        this.refillsRemaining = refillsRemaining;
    }

    public void setLastRefill(String lastRefill) {
        this.lastRefill = lastRefill;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(medicine);
        out.writeString(dosage);
        out.writeString(refillsRemaining);
        out.writeString(lastRefill);
    }

    public static final Parcelable.Creator<PatientOrders> CREATOR
            = new Parcelable.Creator<PatientOrders>() {
        public PatientOrders createFromParcel(Parcel in) {
            return new PatientOrders(in);
        }

        public PatientOrders[] newArray(int size) {
            return new PatientOrders[size];
        }
    };

    private PatientOrders(Parcel in) {
        medicine = in.readString();
        dosage = in.readString();
        refillsRemaining = in.readString();
        lastRefill = in.readString();
    }
}
