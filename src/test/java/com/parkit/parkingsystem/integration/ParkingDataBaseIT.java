package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import junit.framework.Assert;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;

import static com.parkit.parkingsystem.constants.DBConstants.ticket;
import static junit.framework.Assert.assertEquals;
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
        Ticket ticket = new Ticket();
        ticket = ticketDAO.getTicket("ABCDEF");
        Connection con = null;
        String VehicleRegNumber = ticket.getVehicleRegNumber();
        int parkingNumber = -0;

            try {
                con = dataBaseTestConfig.getConnection();
                PreparedStatement ps = con.prepareStatement("select * from ticket where VEHICLE_REG_NUMBER = ?");
                ps.setString(1, VehicleRegNumber);
                ResultSet rs = ps.executeQuery();

                parkingNumber = rs.getInt(2);

                ps = con.prepareStatement("select * from parking where PARKING_NUMBER = ?");
                ps.setInt(1, parkingNumber);
                rs = ps.executeQuery();
                boolean availability = rs.getBoolean(2);
            } catch (Exception e) {
                System.out.println("Error requesting parkingNumber");
            } finally {
                dataBaseTestConfig.closeConnection(con);

            }


        //THEN
        assertEquals(1,ticket.getId());
        assertEquals("ABCDEF",ticket.getVehicleRegNumber());
        //assertEquals(false, availablility);
        //TODO: check that a ticket is actually saved in DB and Parking table is updated with availability
    }

    @Test
    public void testParkingLotExit(){
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();



        //TODO: check that the fare generated and out time are populated correctly in the database
    }

}
