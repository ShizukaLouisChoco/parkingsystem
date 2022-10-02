package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;


    @BeforeEach
    public void setUpPerTest() {
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
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
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("123456789");
        //GIVEN
        ParkingSpot parkingSpot = new ParkingSpot(1, parkingType, false);

        Ticket ticket = new Ticket();
        //reset inTime one hour before
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("123456789");

        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

        //WHEN
        parkingService.processExitingVehicle();

        //THEN
        //verify the method .updateParking is called 1 time
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));

    }

    private static Object[][] incomingType() {
        //userChoice, parkingType
        return new Object[][]{
                {1, ParkingType.CAR},
                {2, ParkingType.BIKE}
        };
    }

    @ParameterizedTest
    @MethodSource("incomingType")
    public void processIncomingVehicleTest(int userChoice, ParkingType IncomingParkingType) {

        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(inputReaderUtil.readSelection()).thenReturn(userChoice);
        when(parkingSpotDAO.getNextAvailableSlot(IncomingParkingType)).thenReturn(1);

        //WHEN
        parkingService.processIncomingVehicle();

        //THEN
        //verify the method .saveTicket is called 1 time
        verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
    }
}
