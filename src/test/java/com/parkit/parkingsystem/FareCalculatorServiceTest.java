package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;



import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    public static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    public void setUpPerTest() {
        ticket = new Ticket();
    }

    private static Object[][] calculateFareAll() {
        return new Object[][]{
                //ParkingType, minutes, discount, expected price
                {ParkingType.CAR, 5, false, 0}, //5 minutes
                {ParkingType.CAR, 5, true, 0},
                {ParkingType.BIKE, 5, false, 0},
                {ParkingType.BIKE, 5, true, 0},
                {ParkingType.CAR, 29, false, 0}, //29 minutes
                {ParkingType.BIKE, 29, false, 0},
                {ParkingType.CAR, 29, true, 0},
                {ParkingType.BIKE, 29, true, 0},
                {ParkingType.CAR, 30, false, roundPrice(0.5, ParkingType.CAR, false)}, //30 minutes
                {ParkingType.BIKE, 30, false, roundPrice(0.5, ParkingType.BIKE, false)},
                {ParkingType.CAR, 30, true, roundPrice(0.5, ParkingType.CAR, true)},
                {ParkingType.BIKE, 30, true, roundPrice(0.5, ParkingType.BIKE, true)},
                {ParkingType.CAR, 45, false, roundPrice(0.75, ParkingType.CAR, false)}, //45minutes
                {ParkingType.BIKE, 45, false, roundPrice(0.75, ParkingType.BIKE, false)},
                {ParkingType.CAR, 45, true, roundPrice(0.75, ParkingType.CAR, true)},
                {ParkingType.BIKE, 45, true, roundPrice(0.75, ParkingType.BIKE, true)},
                {ParkingType.CAR, 60, false, roundPrice(1, ParkingType.CAR, false)}, //1 hour
                {ParkingType.BIKE, 60, false, roundPrice(1, ParkingType.BIKE, false)},
                {ParkingType.CAR, 60, true, roundPrice(1, ParkingType.CAR, true)},
                {ParkingType.BIKE, 60, true, roundPrice(1, ParkingType.BIKE, true)},
                {ParkingType.CAR, 120, false, roundPrice(2, ParkingType.CAR, false)}, //2 hours
                {ParkingType.BIKE, 120, false, roundPrice(2, ParkingType.BIKE, false)},
                {ParkingType.CAR, 120, true, roundPrice(2, ParkingType.CAR, true)},
                {ParkingType.BIKE, 120, true, roundPrice(2, ParkingType.BIKE, true)},
                {ParkingType.CAR, 1440, false, roundPrice(24, ParkingType.CAR, false)}, //24 hours
                {ParkingType.BIKE, 1440, false, roundPrice(24, ParkingType.BIKE, false)},
                {ParkingType.CAR, 1440, true, roundPrice(24, ParkingType.CAR, true)},
                {ParkingType.BIKE, 1440, true, roundPrice(24, ParkingType.BIKE, true)},
                {ParkingType.CAR, 2880, false, roundPrice(24, ParkingType.CAR, false)}, //48 hours
                {ParkingType.BIKE, 2880, false, roundPrice(24, ParkingType.BIKE, false)},
                {ParkingType.CAR, 2880, true, roundPrice(24, ParkingType.CAR, true)},
                {ParkingType.BIKE, 2880, true, roundPrice(24, ParkingType.BIKE, true)},
        };
    }

    private static Object roundPrice(double timeInHour, ParkingType parkingType, Boolean discount) {
        switch (parkingType) {
            case CAR: {
                if (discount) {
                    return  Math.round(timeInHour * ((Fare.CAR_RATE_PER_HOUR * 0.95) * 100.0) / 100.0);
                } else {
                return Math.round(timeInHour * ((Fare.CAR_RATE_PER_HOUR) * 100.0) / 100.0);
                }
            }case BIKE: {
                if (discount) {
                    return  Math.round(timeInHour * ((Fare.BIKE_RATE_PER_HOUR * 0.95) * 100.0) / 100.0);
                } else {
                    return Math.round(timeInHour * ((Fare.BIKE_RATE_PER_HOUR) * 100.0) / 100.0);
                }
            } default: {
                return 0;
            }
        }
    }


    @ParameterizedTest
    @MethodSource("calculateFareAll")
    public void calculateFare(ParkingType parkingType, Integer timeInMinute, boolean discount, double fare) {
        //GIVEN
        ParkingSpot parkingSpot = new ParkingSpot(1, parkingType, false);

        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (timeInMinute * 60 * 1000));
        Date outTime = new Date();

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setDiscount(discount);

        //WHEN
        fareCalculatorService.calculateFare(ticket);

        //THEN
        assertEquals(fare, ticket.getPrice());
    }

    @Test
    public void calculateFareUnknownType() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    private static Object[][] vehicleTypes() {
        return new Object[][]{
                {ParkingType.CAR},
                {ParkingType.BIKE}
        };
    }
    @ParameterizedTest
    @MethodSource("vehicleTypes")
    public void calculateFareWithFutureInTime(ParkingType parkingType) {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, parkingType, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }
}
