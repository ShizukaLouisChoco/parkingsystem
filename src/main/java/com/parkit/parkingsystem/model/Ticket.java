package com.parkit.parkingsystem.model;

import java.util.Date;
import java.util.Optional;

public class Ticket {
    private int idTicket;
    private ParkingSpot parkingSpot;
    private String vehicleRegNumber;
    private double price;
    private Date inTime;
    private Date outTime;
    private boolean discount = false;

    public int getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(int idTicket) {
        this.idTicket = idTicket;
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot = new ParkingSpot(parkingSpot.getId(), parkingSpot.getParkingType(), parkingSpot.isAvailable());
    }

    public void setParkingSpot(ParkingSpot parkingSpot) {
        this.parkingSpot = new ParkingSpot(parkingSpot.getId(), parkingSpot.getParkingType(), parkingSpot.isAvailable());
    }

    public String getVehicleRegNumber() {
        return vehicleRegNumber;
    }

    public void setVehicleRegNumber(String vehicleRegNumber) {
        this.vehicleRegNumber = vehicleRegNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getInTime() {
        return inTime = new Date(inTime.getTime());
    }

    public void setInTime(Date inTime) {

        this.inTime = new Date(inTime.getTime());
    }

    public Date getOutTime() {
        return outTime = Optional.ofNullable(outTime).map(d -> new Date(d.getTime())).orElse(null);
    }

    public void setOutTime(Date outTime) {
        this.outTime = Optional.ofNullable(outTime).map(d -> new Date(d.getTime())).orElse(null);
    }

    public boolean isDiscount(){
        return this.discount;
    }

    public void setDiscount(boolean discount) {
        this.discount = discount;
    }

}
