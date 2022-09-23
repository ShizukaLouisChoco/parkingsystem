package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    private static final String regNumber = "ABCDEF";

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    public static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    public void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(regNumber);
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    public static void tearDown(){

    }

    @Test
    public void testParkingACar(){
        //GIVEN
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        //Ticket doesn't exist
        Assertions.assertNull(ticketDAO.getTicket(regNumber));

        //WHEN
        //Vehicle incoming, and ticket creation
        parkingService.processIncomingVehicle();

        //THEN
        Ticket createdTicket = ticketDAO.getTicket(regNumber);
        Assertions.assertNotNull(createdTicket);
        Assertions.assertFalse(createdTicket.getParkingSpot().isAvailable());
    }


    @Test
    public void testParkingLotExit(){
        //GIVEN
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        //Vehicle incoming
        parkingService.processIncomingVehicle();

        //Get created ticket
        Ticket ticket = ticketDAO.getTicket(regNumber);
        Assertions.assertNull(ticket.getOutTime());
        Assertions.assertEquals(0, ticket.getPrice());

        //update intime
        Date inTimeMinusTwoHours = new Timestamp(ticket.getInTime().getTime() - (1000 *60 * 60 *2));

        ticket.setInTime(inTimeMinusTwoHours);
        ticketDAO.updateTicket(ticket);
        System.out.println(ticket.getInTime());

        //WHEN
        parkingService.processExitingVehicle();

        //THEN
        Ticket exitTicket = ticketDAO.getTicket(regNumber);
        Assertions.assertEquals(ticket.getId(), exitTicket.getId() );
        Assertions.assertEquals(ticket.getVehicleRegNumber(), exitTicket.getVehicleRegNumber() );
        Assertions.assertEquals(ticket.getInTime(), exitTicket.getInTime() );
        Assertions.assertNotNull(exitTicket.getOutTime());
        Assertions.assertTrue(exitTicket.getOutTime().after(exitTicket.getInTime()));
        Assertions.assertTrue( exitTicket.getPrice() > 0 );
    }

}
