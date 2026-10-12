
package com.oibsip.reservation.model;

import java.time.LocalDate;

public class Reservation {

    private String passengerName;
    private String trainNumber;
    private LocalDate journeyDate;
    private String travelClass;

    public Reservation(String passengerName, String trainNumber,
                       LocalDate journeyDate, String travelClass) {
        this.passengerName = passengerName;
        this.trainNumber = trainNumber;
        this.journeyDate = journeyDate;
        this.travelClass = travelClass;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public LocalDate getJourneyDate() {
        return journeyDate;
    }

    public String getTravelClass() {
        return travelClass;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public void setJourneyDate(LocalDate journeyDate) {
        this.journeyDate = journeyDate;
    }

    public void setTravelClass(String travelClass) {
        this.travelClass = travelClass;
    }
}
