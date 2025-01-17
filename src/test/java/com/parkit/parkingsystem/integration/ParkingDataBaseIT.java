package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.Date;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    private static final String regNumber = "ABCDEF";

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    public static void setUp() {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    public void setUpPerTest() {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(regNumber);
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    public static void tearDown() {

    }

    @Test
    public void testParkingACar() {
        //GIVEN
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        //verify that ticket doesn't exist
        Assertions.assertNull(ticketDAO.getTicket(regNumber));

        //WHEN
        //Vehicle incoming, and ticket creation
        parkingService.processIncomingVehicle();

        //THEN
        Ticket createdTicket = ticketDAO.getTicket(regNumber);
        //Ticket is NOT null
        Assertions.assertNotNull(createdTicket);
        //the availability of the parking spot is false
        Assertions.assertFalse(createdTicket.getParkingSpot().isAvailable());
    }


    @Test
    public void testParkingLotExit() {
        //GIVEN
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        //Vehicle incoming
        parkingService.processIncomingVehicle();

        //Get created ticket
        Ticket ticket = ticketDAO.getTicket(regNumber);
        //ticket is NOT null
        Assertions.assertNull(ticket.getOutTime());
        //the price of the ticket is 0 before .processExitingVehicle
        Assertions.assertEquals(0, ticket.getPrice());

        //create intime is two hours before than original inTime
        Date inTimeMinusTwoHours = new Timestamp(ticket.getInTime().getTime() - (1000 * 60 * 60 * 2));
        ticket.setInTime(inTimeMinusTwoHours);
        ticketDAO.updateTicket(ticket);

        //WHEN
        parkingService.processExitingVehicle();

        //THEN
        //create exiting ticket
        Ticket exitTicket = ticketDAO.getTicketById(ticket.getIdTicket());
        //ticket ID, vehicle registration number and inTime must be the same
        Assertions.assertEquals(ticket.getIdTicket(), exitTicket.getIdTicket());
        Assertions.assertEquals(ticket.getVehicleRegNumber(), exitTicket.getVehicleRegNumber());
        Assertions.assertEquals(ticket.getInTime(), exitTicket.getInTime());
        //exiting ticket's outTime is NOT null
        Assertions.assertNotNull(exitTicket.getOutTime());
        //the outTime is after the inTime
        Assertions.assertTrue(exitTicket.getOutTime().after(exitTicket.getInTime()));
        //the price is more than 0
        Assertions.assertTrue(exitTicket.getPrice() > 0);
    }

}
