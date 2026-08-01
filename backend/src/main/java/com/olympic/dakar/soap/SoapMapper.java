package com.olympic.dakar.soap;

import com.olympic.dakar.athlete.dto.AthleteResponse;
import com.olympic.dakar.result.dto.ResultResponse;
import com.olympic.dakar.soap.jaxb.AthleteType;
import com.olympic.dakar.soap.jaxb.Gender;
import com.olympic.dakar.soap.jaxb.Medal;
import com.olympic.dakar.soap.jaxb.ResultType;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;

final class SoapMapper {

    private SoapMapper() {
    }

    static AthleteType toAthleteType(AthleteResponse athlete) {
        AthleteType type = new AthleteType();
        type.setId(athlete.id());
        type.setFirstName(athlete.firstName());
        type.setLastName(athlete.lastName());
        type.setGender(Gender.valueOf(athlete.gender().name()));
        type.setDateOfBirth(toXmlDate(athlete.dateOfBirth()));
        type.setNationality(athlete.nationality());
        type.setDisciplineId(athlete.disciplineId());
        type.setDisciplineName(athlete.disciplineName());
        type.setHeight(athlete.height());
        type.setWeight(athlete.weight());
        return type;
    }

    static ResultType toResultType(ResultResponse result) {
        ResultType type = new ResultType();
        type.setId(result.id());
        type.setEventId(result.eventId());
        type.setEventName(result.eventName());
        type.setAthleteId(result.athleteId());
        type.setAthleteFirstName(result.athleteFirstName());
        type.setAthleteLastName(result.athleteLastName());
        type.setPosition(result.position());
        type.setTime(result.time());
        type.setScore(result.score());
        type.setMedal(Medal.valueOf(result.medal().name()));
        return type;
    }

    private static XMLGregorianCalendar toXmlDate(LocalDate date) {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendarDate(
                    date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
                    DatatypeConstants.FIELD_UNDEFINED);
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException("Impossible d'initialiser DatatypeFactory", e);
        }
    }
}
