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

    private static Object[][] calculateFareMoreThenOneHour() {
        return new Object[][]{
                //ParkingType, minutes, discount, expected price
                { ParkingType.CAR , 5 ,false, 0},//5 minutes
                { ParkingType.BIKE , 5 ,false, 0},
                { ParkingType.CAR , 5 ,true, 0},
                { ParkingType.BIKE , 5 ,true, 0},
                { ParkingType.CAR , 29 ,false, 0}, //29 minutes
                { ParkingType.BIKE , 29 ,false, 0},
                { ParkingType.CAR , 29 ,true, 0},
                { ParkingType.BIKE , 29 ,true, 0},
                { ParkingType.CAR , 30 ,false , 0.5 * Fare.CAR_RATE_PER_HOUR}, //30 minutes
                { ParkingType.BIKE , 30 ,false ,  0.5 *  Fare.BIKE_RATE_PER_HOUR},
                { ParkingType.CAR , 30 ,true , 0.5 * Fare.CAR_RATE_PER_HOUR*0.95},
                { ParkingType.BIKE , 30 , true , 0.5 *  Fare.BIKE_RATE_PER_HOUR*0.95},
                { ParkingType.CAR , 45 ,false , 0.75 * Fare.CAR_RATE_PER_HOUR}, //45minutes
                { ParkingType.BIKE , 45 ,false , 0.75 * Fare.BIKE_RATE_PER_HOUR},
                { ParkingType.CAR , 45 , true ,0.75 * Fare.CAR_RATE_PER_HOUR*0.95},
                { ParkingType.BIKE , 45 , true ,0.75 * Fare.BIKE_RATE_PER_HOUR*0.95},
                { ParkingType.CAR ,  60 ,false , 1 * Fare.CAR_RATE_PER_HOUR},//1 hour
                { ParkingType.BIKE , 60 ,false , 1 * Fare.BIKE_RATE_PER_HOUR},
                { ParkingType.CAR ,  60 , true ,1 * Fare.CAR_RATE_PER_HOUR*0.95},
                { ParkingType.BIKE , 60 , true ,1 * Fare.BIKE_RATE_PER_HOUR*0.95},
                { ParkingType.CAR , 120 ,false , 2 * Fare.CAR_RATE_PER_HOUR},//2 hours
                { ParkingType.BIKE , 120 ,false , 2 * Fare.BIKE_RATE_PER_HOUR},
                { ParkingType.CAR , 120 , true ,2 * Fare.CAR_RATE_PER_HOUR*0.95},
                { ParkingType.BIKE , 120 , true ,2 * Fare.BIKE_RATE_PER_HOUR*0.95},
                { ParkingType.CAR , 1440 ,false , 24 * Fare.CAR_RATE_PER_HOUR},//24 hours
                { ParkingType.BIKE , 1440 ,false , 24 * Fare.BIKE_RATE_PER_HOUR},
                { ParkingType.CAR , 1440 , true ,24 * Fare.CAR_RATE_PER_HOUR*0.95},
                { ParkingType.BIKE , 1440 , true ,24 * Fare.BIKE_RATE_PER_HOUR*0.95},
                { ParkingType.CAR , 2880 ,false , 24 * Fare.CAR_RATE_PER_HOUR},//48 hours
                { ParkingType.BIKE , 2880 ,false , 24 * Fare.BIKE_RATE_PER_HOUR},
                { ParkingType.CAR , 2880 , true ,24 * Fare.CAR_RATE_PER_HOUR*0.95},
                { ParkingType.BIKE , 2880 , true ,24 * Fare.BIKE_RATE_PER_HOUR*0.95},
        };
    }



    @ParameterizedTest
    @MethodSource("calculateFareMoreThenOneHour")
    public void calculateFare(ParkingType parkingType, Integer timeIneMinute, boolean discount, double fare){
        //GIVEN
        ParkingSpot parkingSpot = new ParkingSpot(1, parkingType,false);

        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - ( timeIneMinute * 60 * 1000 ) );
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
    public void calculateFareUnknownType(){
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
}
