package com.parkit.parkingsystem.model;

import com.parkit.parkingsystem.constants.ParkingType;

public class ParkingSpot {
    private int parkingNumber;
    private ParkingType parkingType;
    private boolean isAvailable;

    public ParkingSpot(int parkingNumber, ParkingType parkingType, boolean isAvailable) {
        this.parkingNumber = parkingNumber;
        this.parkingType = parkingType;
        this.isAvailable = isAvailable;
    }

    public int getId() {
        return parkingNumber;
    }

    public void setId(int parkingNumber) {
        this.parkingNumber = parkingNumber;
    }

    public ParkingType getParkingType() {
        return parkingType;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public boolean equals(Object o){
        if(this == o)return true;
        if(o == null||getClass() !=o.getClass())return false;
        ParkingSpot that = (ParkingSpot) o;
        return parkingNumber == that.parkingNumber;
    }

    @Override
    public int hashCode(){return parkingNumber;}
}
