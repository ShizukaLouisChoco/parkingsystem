package com.parkit.parkingsystem.constants;

import com.parkit.parkingsystem.model.Ticket;

public class DBConstants {
    public static Ticket ticket = new Ticket();
    public static final String GET_NEXT_PARKING_SPOT = "select min(PARKING_NUMBER) from parking where AVAILABLE = true and TYPE = ?";
    public static final String UPDATE_PARKING_SPOT = "update parking set available = ? where PARKING_NUMBER = ?";

    public static final String SAVE_TICKET = "insert into ticket(PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME) values(?,?,?,?,?) ";
    public static final String UPDATE_TICKET = "update ticket set PRICE=?, IN_TIME=?, OUT_TIME=? where ID=?";
    public static final String GET_TICKET = "select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME, p.TYPE from ticket t,parking p where p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=? and t.OUT_TIME is null order by t.ID  limit 1";
    public static final String GET_TICKET_BY_ID = "select t.PARKING_NUMBER, t.VEHICLE_REG_NUMBER, t.PRICE, t.IN_TIME, t.OUT_TIME from ticket t where t.ID =? order by t.ID  limit 1";
    public static final String COUNT_TICKETS = "select count(t.ID) from ticket t where t.VEHICLE_REG_NUMBER =? and t.OUT_TIME is not null";
}
