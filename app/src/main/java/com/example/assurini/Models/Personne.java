package com.example.assurini.Models;

import java.io.Serializable;
import java.util.Date;

public class Personne implements Serializable {

    private String fullnom;
    private Date dateNaissance;
    private String lieuNaissance;
    private String email;
    private long numTelephone;
    private long nationalNumber;



    private String motPasse;

    private Date lastLogin;
    private Date dateCreation;



    private String token ;
    private boolean isactive ;


    public Personne() {
        // Set default values
        this.lastLogin = null; // or you can set a specific default value if needed
        this.dateCreation = null;
        this.token = null;
        this.isactive=false;

    }



    public Personne(String fullnom,  Date dateNaissance,
                    String lieuNaissance, String email,long nationalNumber,
                    long numTelephone,  String motPasse,
                    Date lastLogin, Date dateCreation) {

        this.fullnom = fullnom;
this.nationalNumber =nationalNumber;
        this.dateNaissance = dateNaissance;
        this.lieuNaissance = lieuNaissance;

        this.isactive=false;
        this.email = email;
        this.numTelephone = numTelephone;
        this.motPasse = motPasse;


        this.lastLogin = lastLogin;
        this.dateCreation = dateCreation;
    }



    public String getFullnom() {
        return fullnom;
    }

    public void setFullnom(String fullnom) {
        this.fullnom = fullnom;
    }



    public Date getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getLieuNaissance() {
        return lieuNaissance;
    }

    public void setLieuNaissance(String lieuNaissance) {
        this.lieuNaissance = lieuNaissance;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public long getNumTelephone() {
        return numTelephone;
    }

    public void setNumTelephone(long numTelephone) {
        this.numTelephone = numTelephone;
    }



    public String getMotPasse() {
        return motPasse;
    }

    public void setMotPasse(String motPasse) {
        this.motPasse = motPasse;
    }


    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public boolean isIsactive() {
        return isactive;
    }

    public void setIsactive(boolean isactive) {
        this.isactive = isactive;
    }
    public long getNationalNumber() {
        return nationalNumber;
    }

    public void setNationalNumber(long nationalNumber) {
        this.nationalNumber = nationalNumber;
    }
}
