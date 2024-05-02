package com.example.assurini.Models;

public class InsuranceAgency {

    private String idInsuranceAgency;
    private String name;
    private String address;
    private String insuranceType;
    private String email;
    private String phone;

    public InsuranceAgency() {
    }

    public InsuranceAgency(String idInsuranceAgency, String name, String address, String insuranceType, String email, String phone) {
        this.idInsuranceAgency = idInsuranceAgency;
        this.name = name;
        this.address = address;
        this.insuranceType = insuranceType;
        this.email = email;
        this.phone = phone;
    }

    public String getIdInsuranceAgency() {
        return idInsuranceAgency;
    }

    public void setIdInsuranceAgency(String idInsuranceAgency) {
        this.idInsuranceAgency = idInsuranceAgency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getInsuranceType() {
        return insuranceType;
    }

    public void setInsuranceType(String insuranceType) {
        this.insuranceType = insuranceType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "InsuranceAgency{" +
                "idInsuranceAgency='" + idInsuranceAgency + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", insuranceType='" + insuranceType + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
