package com.example.carrental;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Car {
    private final String plateNumber;
    private final String brand;
    private final int price;
    private boolean rented;
    @JsonIgnore
    private Dates rentalDates;

    public Car(String plateNumber, String brand, int price) {
        this.plateNumber = plateNumber;
        this.brand = brand;
        this.price = price;
    }

    public String getPlateNumber() { return plateNumber; }
    public String getBrand() { return brand; }
    public int getPrice() { return price; }
    public boolean isRented() { return rented; }
    public Dates getRentalDates() { return rentalDates; }

    public void rent(Dates dates) {
        rented = true;
        rentalDates = dates;
    }

    public void returnCar() {
        rented = false;
        rentalDates = null;
    }
}
