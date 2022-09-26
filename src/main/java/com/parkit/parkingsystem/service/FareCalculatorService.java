package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.util.Date;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }
        Date inHour = ticket.getInTime();
        Date outHour = ticket.getOutTime();

        double durationInMillis = (outHour.getTime() - inHour.getTime());
        double duration = durationInMillis  / (1000 * 60 * 60);
        double price;

        if (duration < 0.5) {
            price = 0;
        } else {
            if (duration > 24)
                duration = 24;

            double discountFare = ticket.isDiscount()
                    ? 0.95
                    : 1;

            final double roundDigit = 100.0;

            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    price = Math.round((duration * discountFare * Fare.CAR_RATE_PER_HOUR * roundDigit) / roundDigit);
                    break;
                }
                case BIKE: {
                    price = Math.round((duration * discountFare * Fare.BIKE_RATE_PER_HOUR * roundDigit) / roundDigit);
                    break;
                }
                default: throw new IllegalArgumentException("Unknown Parking Type");
            }
        }
        ticket.setPrice(price);
    }
}
