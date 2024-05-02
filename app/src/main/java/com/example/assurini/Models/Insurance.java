package com.example.assurini.Models;

import java.math.BigDecimal;
import java.util.Date;

public class Insurance {

    private String idInsurance;
    private Date startDate;
    private Date endDate;
    private String policeNumber;
    private Double price;
    private String agency;
    private String idGrayCard;
    private String etat; // new attribute

    public String getInsurancefacture() {
        return insurancefacture;
    }

    public void setInsurancefacture(String insurancefacture) {
        this.insurancefacture = insurancefacture;
    }

    private  String insurancefacture ;

    public Insurance() {
        this.etat = "active"; // default value
        this.insurancefacture= null ;
    }

    public Insurance(String idInsurance, Date startDate, Date endDate, String policeNumber, double price, String agency, String idGrayCard) {
        this.idInsurance = idInsurance;
        this.startDate = startDate;
        this.endDate = endDate;
        this.policeNumber = policeNumber;
        this.price = price;
        this.agency = agency;
        this.idGrayCard = idGrayCard;
        this.etat = "active";
        this.insurancefacture= null ;// default value
    }

    public String getIdInsurance() {
        return idInsurance;
    }

    public void setIdInsurance(String idInsurance) {
        this.idInsurance = idInsurance;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
        updateEtat(); // update etat when endDate is set
    }

    public String getPoliceNumber() {
        return policeNumber;
    }

    public void setPoliceNumber(String policeNumber) {
        this.policeNumber = policeNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public String getIdGrayCard() {
        return idGrayCard;
    }

    public void setIdGrayCard(String idGrayCard) {
        this.idGrayCard = idGrayCard;
    }

    public String getEtat() {
        return etat;
    }

    private void updateEtat() {
        if (endDate != null && endDate.before(new Date())) {
            this.etat = "expired";
        } else {
            this.etat = "active";
        }
    }

    @Override
    public String toString() {
        return "Insurance{" +
                "idInsurance='" + idInsurance + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", policeNumber='" + policeNumber + '\'' +
                ", price=" + price +
                ", agency='" + agency + '\'' +
                ", idGrayCard='" + idGrayCard + '\'' +
                ", etat='" + etat + '\'' +
                '}';
    }
}
