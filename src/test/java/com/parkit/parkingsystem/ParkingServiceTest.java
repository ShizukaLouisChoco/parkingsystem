package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    @BeforeEach
    public void setUpPerTest() {
    }

    private static Object[][] parkingType() {
        return new Object[][]{
                {ParkingType.CAR},
                {ParkingType.BIKE}
        };
    }


    @ParameterizedTest
    @MethodSource("parkingType")
    public void processExitingVehicleTest(ParkingType parkingType) {
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
            //GIVEN
            ParkingSpot parkingSpot = new ParkingSpot(1, parkingType, false);

            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("123456789");
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

            //WHEN
            parkingService.processExitingVehicle();

            //THEN
            verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));

    }

    private static Object[][] incomingType() {
        return new Object[][]{
                {1,ParkingType.CAR,false},
                {2,ParkingType.CAR,true},
                {3,ParkingType.BIKE,false},
                {4,ParkingType.BIKE,true}
        };
    }
    /*
    @ParameterizedTest
    @MethodSource("incomingType")
    public void processIncomingVehicleTest(int parkingSpotNumber, ParkingType IncomingParkingType,Boolean isRecurringVehicle){
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
            when(inputReaderUtil.readSelection()).thenReturn(1);

        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
            //parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
            //int parkingNumber = parkingSpotNumber;
            ParkingSpot parkingSpot = new ParkingSpot(parkingSpotNumber, IncomingParkingType,true);
            //when(parkingService.getNextParkingNumberIfAvailable()).thenReturn(parkingSpot);
        //WHEN
        Ticket ticket = new Ticket();
        parkingService.processIncomingVehicle();

        //THEN
        verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
    }*/

    @ParameterizedTest
    @MethodSource("parkingType")
    public void NextParkingNumberIsNotAvailable_ThenThrowsException(ParkingType parkingType){
        //GIVEN
        ParkingSpot parkingSpot = new ParkingSpot(-1, parkingType,false);

        //WHEN

        //THEN
        assertThrows(Exception.class,()->parkingService.getNextParkingNumberIfAvailable());

    }

}
