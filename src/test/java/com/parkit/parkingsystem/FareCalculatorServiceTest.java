package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

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

    @Test
    public void calculateFareCar(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(Fare.CAR_RATE_PER_HOUR, ticket.getPrice());
    }

    @Test
    public void calculateFareBike(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareUnkownType(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithFutureInTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() + (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice() );
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (0.75 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  24 * 60 * 60 * 1000) );//24 hours parking time should give 24 * parking fare per hour
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (24 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    public void BikeRecurringUsesHave5PercentDiscount(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );//Parking time is 1 hour
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
        String vehicleRegNumber = "123456789";
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber(vehicleRegNumber);
        ticket.setDiscount(true);
        fareCalculatorService.calculateFare(ticket);

        //     assertEquals("Welcome back! As a recurring user of our parking lot, you'll benefit from a 5% discount.",fareCalculatorService.RecurringUserDiscount());
        assertEquals( ((1 * Fare.BIKE_RATE_PER_HOUR) * 0.95) , ticket.getPrice());
    }

    @Test
    public void CarRecurringUsesHave5PercentDiscount(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );//Parking time is 1 hour
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        String vehicleRegNumber = "123456789";
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber(vehicleRegNumber);
        ticket.setDiscount(true);
        fareCalculatorService.calculateFare(ticket);

        // assertEquals("Welcome back! As a recurring user of our parking lot, you'll benefit from a 5% discount.",fareCalculatorService.calculateFare(ticket));
        assertEquals( ((1 * Fare.CAR_RATE_PER_HOUR) * 0.95) , ticket.getPrice());
    }
}
