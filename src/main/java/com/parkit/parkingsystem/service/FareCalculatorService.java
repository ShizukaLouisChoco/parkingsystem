package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.util.Date;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }
        Date inHour = ticket.getInTime();
        Date outHour = ticket.getOutTime();

        //TODO: Some tests are failing here. Need to check if this logic is correct
        //Calculate seconds
        double differenceSeconds = (outHour.getTime() - inHour.getTime())/1000;
        //Calculate minutes
        double differenceMinutes =  ((differenceSeconds/60)%60);
        //Calculate hour
        double differenceHours = differenceSeconds/(60* 60);
        if(differenceHours <1){
            differenceHours = 0.0;
        }else if(differenceHours >25){
            differenceHours = 24;
        }
        double duration = differenceMinutes/60 + differenceHours;

        System.out.println("seconds" + differenceSeconds);
        System.out.println("minutes" + differenceMinutes);
        System.out.println("hours" + differenceHours);
        System.out.println("duration" + duration);

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}