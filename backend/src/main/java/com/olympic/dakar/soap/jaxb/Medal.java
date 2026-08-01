package com.olympic.dakar.soap.jaxb;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "medalType")
@XmlEnum
public enum Medal {
    GOLD,
    SILVER,
    BRONZE,
    NONE
}
