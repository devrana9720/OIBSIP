
package com.oibsip.reservation.model;

import java.time.LocalDate;

public class Reservation {

    private int id;
    private String pnr;
    private String passengerName;
    private String trainNumber;
    private String trainName;
    private String sourceStation;
    private String destinationStation;
    private LocalDate journeyDate;
    private String travelClass;
    private String bookingStatus;

    public Reservation(String passengerName, String trainNumber,
                       LocalDate journeyDate, String travelClass) {
        this(0, null, passengerName, trainNumber, null,
                null, null, journeyDate, travelClass, "CONFIRMED");
    }

    public Reservation(int id, String passengerName, String trainNumber,
                       LocalDate journeyDate, String travelClass) {
        this(id, null, passengerName, trainNumber, null,
                null, null, journeyDate, travelClass, "CONFIRMED");
    }

    public Reservation(String pnr, String passengerName,
                       String trainNumber, String trainName,
                       String sourceStation, String destinationStation,
                       LocalDate journeyDate, String travelClass) {
        this(0, pnr, passengerName, trainNumber, trainName,
                sourceStation, destinationStation, journeyDate,
                travelClass, "CONFIRMED");
    }

    public Reservation(int id, String pnr, String passengerName,
                       String trainNumber, String trainName,
                       String sourceStation, String destinationStation,
                       LocalDate journeyDate, String travelClass,
                       String bookingStatus) {
        this.id = id;
        this.pnr = pnr;
        this.passengerName = passengerName;
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.sourceStation = sourceStation;
        this.destinationStation = destinationStation;
        this.journeyDate = journeyDate;
        this.travelClass = travelClass;
        this.bookingStatus = bookingStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPnr() {
        return pnr;
    }

    public void setPnr(String pnr) {
        this.pnr = pnr;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public String getSourceStation() {
        return sourceStation;
    }

    public void setSourceStation(String sourceStation) {
        this.sourceStation = sourceStation;
    }

    public String getDestinationStation() {
        return destinationStation;
    }

    public void setDestinationStation(String destinationStation) {
        this.destinationStation = destinationStation;
    }

    public LocalDate getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(LocalDate journeyDate) {
        this.journeyDate = journeyDate;
    }

    public String getTravelClass() {
        return travelClass;
    }

    public void setTravelClass(String travelClass) {
        this.travelClass = travelClass;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }
}
