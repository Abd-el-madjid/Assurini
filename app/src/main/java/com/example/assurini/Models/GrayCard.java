package com.example.assurini.Models;

public class GrayCard {

    private long idGrayCard;
    private String wilaya;
    private String commune;
    private int registrationNumber;
    private int previousNumber;
    private int yearOfFirstCirculation;
    private String numberInTheTypeSeries;
    private String type;
    private String mark;
    private String gender;
    private int payload;
    private int grossWeight;
    private int power;
    private String body;
    private String personCar;
    private String fuelType;
    private boolean isAssure ; // Default value is false
    public GrayCard() {
        this.isAssure = false; // Default value is "false"
    }
    public long getIdGrayCard() {
        return idGrayCard;
    }

    public void setIdGrayCard(long idGrayCard) {
        this.idGrayCard = idGrayCard;
    }

    public String getWilaya() {
        return wilaya;
    }

    public void setWilaya(String wilaya) {
        this.wilaya = wilaya;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    public int getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(int registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public int getPreviousNumber() {
        return previousNumber;
    }

    public void setPreviousNumber(int previousNumber) {
        this.previousNumber = previousNumber;
    }

    public int getYearOfFirstCirculation() {
        return yearOfFirstCirculation;
    }

    public void setYearOfFirstCirculation(int yearOfFirstCirculation) {
        this.yearOfFirstCirculation = yearOfFirstCirculation;
    }

    public String getNumberInTheTypeSeries() {
        return numberInTheTypeSeries;
    }

    public void setNumberInTheTypeSeries(String numberInTheTypeSeries) {
        this.numberInTheTypeSeries = numberInTheTypeSeries;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getPayload() {
        return payload;
    }

    public void setPayload(int payload) {
        this.payload = payload;
    }

    public int getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(int grossWeight) {
        this.grossWeight = grossWeight;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getPersonCar() {
        return personCar;
    }

    public void setPersonCar(String personCar) {
        this.personCar = personCar;
    }

    public boolean isAssure() {
        return isAssure;
    }

    public void setAssure(boolean assure) {
        isAssure = assure;
    }

    @Override
    public String toString() {
        return "GrayCard{" +
                "idGrayCard=" + idGrayCard +
                ", wilaya='" + wilaya + '\'' +
                ", commune='" + commune + '\'' +
                ", registrationNumber=" + registrationNumber +
                ", previousNumber=" + previousNumber +
                ", yearOfFirstCirculation=" + yearOfFirstCirculation +
                ", numberInTheTypeSeries='" + numberInTheTypeSeries + '\'' +
                ", type='" + type + '\'' +
                ", mark='" + mark + '\'' +
                ", gender='" + gender + '\'' +
                ", payload=" + payload +
                ", grossWeight=" + grossWeight +
                ", power=" + power +
                ", body='" + body + '\'' +
                ", fuelType='" + fuelType + '\'' +
                ", personCar='" + personCar + '\'' +
                ", isAssure=" + isAssure +
                '}';
    }
}
