package com.olympic.dakar.soap.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"athlete"})
@XmlRootElement(name = "getAthleteResponse")
public class GetAthleteResponse {

    private AthleteType athlete;

    public AthleteType getAthlete() {
        return athlete;
    }

    public void setAthlete(AthleteType athlete) {
        this.athlete = athlete;
    }
}
