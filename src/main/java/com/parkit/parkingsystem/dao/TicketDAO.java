package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class TicketDAO {

    private static final Logger logger = LogManager.getLogger("TicketDAO");

    public DataBaseConfig dataBaseConfig = new DataBaseConfig();

    public void saveTicket(Ticket ticket){
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET);
            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            ps.setInt(1,ticket.getParkingSpot().getId());
            ps.setString(2, ticket.getVehicleRegNumber());
            ps.setDouble(3, ticket.getPrice());
            ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
            ps.setTimestamp(5, (ticket.getOutTime() == null)?null: (new Timestamp(ticket.getOutTime().getTime())) );
            //ps.setInt(6,ticket.getIdTicket());
            ps.execute();
            //todo pas besoin de close ps?
        }catch (Exception ex){
            logger.error("Error fetching next available slot",ex);
        }finally {
            dataBaseConfig.closeConnection(con);
        }

    }

    public Ticket getTicket(String vehicleRegNumber) {
        Connection con = null;
        Ticket ticket = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET);
            ps.setString(1,vehicleRegNumber);

            ResultSet rs = ps.executeQuery();
            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            if(rs.next()){
                ticket = new Ticket();
                ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)),false);
                ticket.setParkingSpot(parkingSpot);
                ticket.setIdTicket(rs.getInt(2));
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(rs.getDouble(3));
                ticket.setInTime(rs.getTimestamp(4));
                ticket.setOutTime(rs.getTimestamp(5));
            }
            //todo  besoin de close rs et ps?
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);

        }catch (Exception ex){
            logger.error("Error fetching next available slot",ex);
        }finally {
            dataBaseConfig.closeConnection(con);
        }

       return ticket;
    }

    public Ticket getTicketById(int ticketId ) {
        Connection con = null;
        Ticket ticket = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET_BY_ID);
            ps.setInt(1,ticketId);

            ResultSet rs = ps.executeQuery();
            //t.PARKING_NUMBER, t.VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME,)
            if(rs.next()){
                ticket = new Ticket();
                int parkingNumber = rs.getInt(1);
                ParkingType parkingType;
                if(parkingNumber>=3){
                     parkingType = ParkingType.CAR;
                }else{
                    parkingType = ParkingType.BIKE;
                }
                ParkingSpot parkingSpot = new ParkingSpot(parkingNumber,parkingType,false);
                ticket.setParkingSpot(parkingSpot);
                ticket.setIdTicket(ticketId);
                ticket.setVehicleRegNumber(rs.getString(2));
                ticket.setPrice(rs.getDouble(3));
                ticket.setInTime(rs.getTimestamp(4));
                ticket.setOutTime(rs.getTimestamp(5));
            }
            //todo  besoin de close rs et ps?
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);

        }catch (Exception ex){
            logger.error("Error fetching next available slot",ex);
        }finally {
            dataBaseConfig.closeConnection(con);
        }
       return ticket;
    }

    public boolean updateTicket(Ticket ticket) {
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
            ps.setDouble(1, ticket.getPrice());
            ps.setTimestamp(2, new Timestamp(ticket.getInTime().getTime()));
            ps.setTimestamp(3, (ticket.getOutTime() == null)?null: (new Timestamp(ticket.getOutTime().getTime())) );
            ps.setInt(4,ticket.getIdTicket());
            ps.execute();
            return true;
            //todo:pas besoin de close ps?
        }catch (Exception ex){
            logger.error("Error saving ticket info",ex);
        }finally {
            dataBaseConfig.closeConnection(con);
        }
        return false;
    }

    public boolean recurringVehicle(String vehicleRegNumber){
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.COUNT_TICKETS);
            ps.setString(1,vehicleRegNumber);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                int recurringTime = rs.getInt(1);
                return (recurringTime > 0);
                //todo cloture ps et rs
            }
            //todo: besoin de close rs et ps?
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
        }catch (Exception ex){
            logger.error("Error verifying recurring user or not",ex);
        }finally {
            dataBaseConfig.closeConnection(con);
        }
        return false;
    }
}
