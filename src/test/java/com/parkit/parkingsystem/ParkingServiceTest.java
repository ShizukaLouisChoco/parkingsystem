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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
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
                {1,ParkingType.CAR},
                {2,ParkingType.BIKE}
        };
    }

    @ParameterizedTest
    @MethodSource("incomingType")
    public void processIncomingVehicleTest(int userChoice, ParkingType IncomingParkingType){
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
            when(inputReaderUtil.readSelection()).thenReturn(userChoice);
            when(parkingSpotDAO.getNextAvailableSlot(IncomingParkingType)).thenReturn(1);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }

        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        //WHEN
        parkingService.processIncomingVehicle();

        //THEN
        verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
    }
    /*
    @ParameterizedTest
    @MethodSource("parkingType")
    public void NextParkingNumberIsNotAvailable_ThenThrowsException(ParkingType parkingType) throws SQLException, ClassNotFoundException {
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
            when(inputReaderUtil.readSelection()).thenReturn(1);
            when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(0);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }

        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        //WHEN
        //THEN
        Exception e = assertThrows(Exception.class,() ->parkingService.getNextParkingNumberIfAvailable());
        assertEquals("Error fetching next available parking slot",e.getMessage());
        assertThrows(Exception.class, () -> parkingService.getNextParkingNumberIfAvailable());
        //todo: ERROR mais pas exception
    }
*/

    @Test
    public void getIncorrectVehicleType_ThenThrowIllegalArgumentException()throws Exception{
        parkingService = new ParkingService(inputReaderUtil,parkingSpotDAO,ticketDAO);
        Method method = ParkingService.class.getDeclaredMethod("getVehicleType");
        method.setAccessible(true);
        try{
            ParkingType parkingType1 = (ParkingType)method.invoke(parkingService);
        }catch(InvocationTargetException e){
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
        }
        //todo: private method
    }

}
