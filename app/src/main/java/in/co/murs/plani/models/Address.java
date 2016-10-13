package in.co.murs.plani.models;

import java.io.Serializable;

/**
 * Created by Ujjwal on 7/11/2016.
 */
public class Address implements Serializable{
    private long _id;
    private double latitude;
    private double longitude;
    private String address;

    public Address(){}

    public Address(double latitude, double longitude, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }
}
