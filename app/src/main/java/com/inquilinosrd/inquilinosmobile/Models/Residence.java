package com.inquilinosrd.inquilinosmobile.Models;

import java.util.List;

public class Residence {
    public int Id;
    public double Latitude;
    public double Longitude;
    public String Address;
    public int Rooms;
    public int Bathrooms;
    public int ParkingSpaces;
    public String Description;
    public double Price;
    public boolean ForRent;
    public boolean IsFurnituresIncluded;
    public ResidenceTypes ResidenceType;
    public String ResidenceSeller;
    public double Dimensions;
    public String Images;

    @Override
    public String toString() {
        return "Residence{" +
                "Id=" + Id +
                ", Latitude=" + Latitude +
                ", Longitude=" + Longitude +
                ", Address='" + Address + '\'' +
                ", Rooms=" + Rooms +
                ", Bathrooms=" + Bathrooms +
                ", ParkingSpaces=" + ParkingSpaces +
                ", Description='" + Description + '\'' +
                ", Price=" + Price +
                ", ForRent=" + ForRent +
                ", IsFurnituresIncluded=" + IsFurnituresIncluded +
                ", ResidenceType=" + ResidenceType +
                ", ResidenceSeller=" + ResidenceSeller +
                ", Dimensions=" + Dimensions +
                ", Images=" + Images +
                '}';
    }
}
