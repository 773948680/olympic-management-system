package com.olympic.dakar.soap.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"result"})
@XmlRootElement(name = "getNationMedalHistoryResponse")
public class GetNationMedalHistoryResponse {

    @XmlElement(name = "result")
    private List<ResultType> result = new ArrayList<>();

    public List<ResultType> getResult() {
        return result;
    }
}
