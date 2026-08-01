package com.olympic.dakar.soap;

import com.olympic.dakar.athlete.AthleteService;
import com.olympic.dakar.athlete.dto.AthleteResponse;
import com.olympic.dakar.result.ResultService;
import com.olympic.dakar.result.dto.ResultResponse;
import com.olympic.dakar.soap.jaxb.GetAthleteRequest;
import com.olympic.dakar.soap.jaxb.GetAthleteResponse;
import com.olympic.dakar.soap.jaxb.GetAthleteResultsRequest;
import com.olympic.dakar.soap.jaxb.GetAthleteResultsResponse;
import com.olympic.dakar.soap.jaxb.GetEventResultsRequest;
import com.olympic.dakar.soap.jaxb.GetEventResultsResponse;
import com.olympic.dakar.soap.jaxb.GetNationMedalHistoryRequest;
import com.olympic.dakar.soap.jaxb.GetNationMedalHistoryResponse;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.List;

@Endpoint
public class OlympicManagementEndpoint {

    private static final String NAMESPACE = "http://olympic.dakar.com/soap/olympic-management";

    private final AthleteService athleteService;
    private final ResultService resultService;

    public OlympicManagementEndpoint(AthleteService athleteService, ResultService resultService) {
        this.athleteService = athleteService;
        this.resultService = resultService;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "getAthleteRequest")
    @ResponsePayload
    public GetAthleteResponse getAthlete(@RequestPayload GetAthleteRequest request) {
        AthleteResponse athlete = athleteService.findById(request.getAthleteId());
        GetAthleteResponse response = new GetAthleteResponse();
        response.setAthlete(SoapMapper.toAthleteType(athlete));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "getAthleteResultsRequest")
    @ResponsePayload
    public GetAthleteResultsResponse getAthleteResults(@RequestPayload GetAthleteResultsRequest request) {
        List<ResultResponse> results = resultService.findByAthlete(request.getAthleteId());
        GetAthleteResultsResponse response = new GetAthleteResultsResponse();
        results.forEach(result -> response.getResult().add(SoapMapper.toResultType(result)));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "getEventResultsRequest")
    @ResponsePayload
    public GetEventResultsResponse getEventResults(@RequestPayload GetEventResultsRequest request) {
        List<ResultResponse> results = resultService.findByEvent(request.getEventId());
        GetEventResultsResponse response = new GetEventResultsResponse();
        results.forEach(result -> response.getResult().add(SoapMapper.toResultType(result)));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "getNationMedalHistoryRequest")
    @ResponsePayload
    public GetNationMedalHistoryResponse getNationMedalHistory(@RequestPayload GetNationMedalHistoryRequest request) {
        List<ResultResponse> history = resultService.findMedalHistoryByNationality(request.getNationality());
        GetNationMedalHistoryResponse response = new GetNationMedalHistoryResponse();
        history.forEach(result -> response.getResult().add(SoapMapper.toResultType(result)));
        return response;
    }
}
