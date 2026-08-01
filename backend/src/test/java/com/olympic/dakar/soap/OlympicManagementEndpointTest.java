package com.olympic.dakar.soap;

import com.olympic.dakar.athlete.Athlete;
import com.olympic.dakar.athlete.AthleteRepository;
import com.olympic.dakar.athlete.Gender;
import com.olympic.dakar.discipline.Discipline;
import com.olympic.dakar.discipline.DisciplineRepository;
import com.olympic.dakar.event.Event;
import com.olympic.dakar.event.EventRepository;
import com.olympic.dakar.event.EventStatus;
import com.olympic.dakar.result.Result;
import com.olympic.dakar.result.ResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.xml.transform.StringSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.clientOrSenderFault;
import static org.springframework.ws.test.server.ResponseMatchers.noFault;
import static org.springframework.ws.test.server.ResponseMatchers.xpath;

@SpringBootTest
@Transactional
class OlympicManagementEndpointTest {

    private static final String NS = "http://olympic.dakar.com/soap/olympic-management";
    private static final Map<String, String> NAMESPACES = Map.of("tns", NS);

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private DisciplineRepository disciplineRepository;
    @Autowired
    private AthleteRepository athleteRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ResultRepository resultRepository;

    private MockWebServiceClient mockClient;

    private Discipline discipline;
    private Athlete bolt;
    private Event event;

    @BeforeEach
    void setUp() {
        mockClient = MockWebServiceClient.createClient(applicationContext);

        discipline = disciplineRepository.save(new Discipline("Athlétisme-SOAP", null));
        bolt = athleteRepository.save(new Athlete("Usain", "Bolt", Gender.MALE,
                LocalDate.of(1986, 8, 21), "Jamaïque", discipline, 195, 94.0));
        event = eventRepository.save(new Event("100m", discipline,
                LocalDateTime.of(2026, 8, 10, 18, 0), "Stade Dakar", EventStatus.SCHEDULED));
        resultRepository.save(new Result(event, bolt, 1, "9.58s", null));
    }

    @Test
    void getAthleteShouldReturnAthleteDetails() {
        StringSource request = new StringSource("""
                <getAthleteRequest xmlns="%s">
                    <athleteId>%d</athleteId>
                </getAthleteRequest>
                """.formatted(NS, bolt.getId()));

        mockClient.sendRequest(withPayload(request))
                .andExpect(noFault())
                .andExpect(xpath("/tns:getAthleteResponse/tns:athlete/tns:lastName", NAMESPACES).evaluatesTo("Bolt"))
                .andExpect(xpath("/tns:getAthleteResponse/tns:athlete/tns:nationality", NAMESPACES).evaluatesTo("Jamaïque"));
    }

    @Test
    void getAthleteShouldReturnClientFaultWhenMissing() {
        StringSource request = new StringSource("""
                <getAthleteRequest xmlns="%s">
                    <athleteId>999999</athleteId>
                </getAthleteRequest>
                """.formatted(NS));

        mockClient.sendRequest(withPayload(request))
                .andExpect(clientOrSenderFault("Ressource introuvable"));
    }

    @Test
    void getAthleteShouldReturnValidationFaultWhenAthleteIdMissing() {
        StringSource request = new StringSource("""
                <getAthleteRequest xmlns="%s">
                </getAthleteRequest>
                """.formatted(NS));

        mockClient.sendRequest(withPayload(request))
                .andExpect(clientOrSenderFault());
    }

    @Test
    void getAthleteShouldReturnValidationFaultWhenAthleteIdIsNotNumeric() {
        StringSource request = new StringSource("""
                <getAthleteRequest xmlns="%s">
                    <athleteId>not-a-number</athleteId>
                </getAthleteRequest>
                """.formatted(NS));

        mockClient.sendRequest(withPayload(request))
                .andExpect(clientOrSenderFault());
    }

    @Test
    void getAthleteResultsShouldReturnResultsForAthlete() {
        StringSource request = new StringSource("""
                <getAthleteResultsRequest xmlns="%s">
                    <athleteId>%d</athleteId>
                </getAthleteResultsRequest>
                """.formatted(NS, bolt.getId()));

        mockClient.sendRequest(withPayload(request))
                .andExpect(noFault())
                .andExpect(xpath("count(/tns:getAthleteResultsResponse/tns:result)", NAMESPACES).evaluatesTo(1))
                .andExpect(xpath("/tns:getAthleteResultsResponse/tns:result[1]/tns:medal", NAMESPACES).evaluatesTo("GOLD"));
    }

    @Test
    void getEventResultsShouldReturnResultsForEvent() {
        StringSource request = new StringSource("""
                <getEventResultsRequest xmlns="%s">
                    <eventId>%d</eventId>
                </getEventResultsRequest>
                """.formatted(NS, event.getId()));

        mockClient.sendRequest(withPayload(request))
                .andExpect(noFault())
                .andExpect(xpath("count(/tns:getEventResultsResponse/tns:result)", NAMESPACES).evaluatesTo(1))
                .andExpect(xpath("/tns:getEventResultsResponse/tns:result[1]/tns:athleteLastName", NAMESPACES).evaluatesTo("Bolt"));
    }

    @Test
    void getNationMedalHistoryShouldReturnMedalResultsForNation() {
        StringSource request = new StringSource("""
                <getNationMedalHistoryRequest xmlns="%s">
                    <nationality>Jamaïque</nationality>
                </getNationMedalHistoryRequest>
                """.formatted(NS));

        mockClient.sendRequest(withPayload(request))
                .andExpect(noFault())
                .andExpect(xpath("count(/tns:getNationMedalHistoryResponse/tns:result)", NAMESPACES).evaluatesTo(1))
                .andExpect(xpath("/tns:getNationMedalHistoryResponse/tns:result[1]/tns:medal", NAMESPACES).evaluatesTo("GOLD"));
    }

    @Test
    void getNationMedalHistoryShouldReturnEmptyForUnknownNation() {
        StringSource request = new StringSource("""
                <getNationMedalHistoryRequest xmlns="%s">
                    <nationality>Atlantide</nationality>
                </getNationMedalHistoryRequest>
                """.formatted(NS));

        mockClient.sendRequest(withPayload(request))
                .andExpect(noFault())
                .andExpect(xpath("count(/tns:getNationMedalHistoryResponse/tns:result)", NAMESPACES).evaluatesTo(0));
    }
}
