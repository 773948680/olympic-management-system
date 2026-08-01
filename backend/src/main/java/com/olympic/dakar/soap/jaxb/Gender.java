package com.olympic.dakar.soap.jaxb;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "genderType")
@XmlEnum
public enum Gender {
    MALE,
    FEMALE
}
