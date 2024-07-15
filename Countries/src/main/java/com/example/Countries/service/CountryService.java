package com.example.Countries.service;

import com.example.Countries.entity.Country;
import com.example.Countries.entity.PublicHoliday;
import com.example.Countries.entity.ResBody;
import com.example.Countries.utils.GetCountriesAPI;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class CountryService {

    @Autowired
    private  GetCountriesAPI getCountriesAPI;

    public Country callGetCountryAPI(String countryResponse){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode response;

        try {
            response = objectMapper.readTree(countryResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Country country = Country.builder()
                .name(response.get(0).get("name").get("common").asText())
                .Capital(Collections.singletonList(response.get(0).get("capital").toString().trim()))
                .region(response.get(0).get("region").asText())
                .googlemaps(response.get(0).get("maps").get("googleMaps").asText())
                .build();

        return country;
    }


    public List<PublicHoliday> callGetPublicHolidayAPI(String publicHoliResponse){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode response;

        try {
            response = objectMapper.readTree(publicHoliResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        List<PublicHoliday> publicHolidays = new ArrayList<>();
        int i = 0;
        Object Date;
        while (true){
            Date = response.get(i);
            if (Date == null){
                break;
            }
            PublicHoliday publicHoliday = PublicHoliday.builder()
                    .Date(response.get(i).get("date").asText())
                    .localName(response.get(i).get("localName").asText())
                    .name(response.get(i).get("name").asText())
                    .countryCode(response.get(i).get("countryCode").asText())
                    .build();
            publicHolidays.add(publicHoliday);
            i++;
        }
        return publicHolidays;
    }

    public ResBody runAsync (String year, String countryCode) throws ExecutionException, InterruptedException{
        String countryBase = "https://restcountries.com/v3.1/alpha/";

        String publicholiBase = "https://date.nager.at/api/v3/publicholidays/";

        //CompletableFuture<Country> countryFuture = CompletableFuture.supplyAsync(() -> callGetCountryAPI(countryCode));
        //CompletableFuture<List<PublicHoliday>> publicHolidaysFuture = CompletableFuture.supplyAsync(() -> callGetPublicHolidayAPI(year, countryCode));
        CompletableFuture<String> countryFuture = getCountriesAPI.getCountryData(countryBase + countryCode);
        CompletableFuture<String> publicHolidaysFuture = getCountriesAPI.getCountryData(publicholiBase+year+"/"+countryCode);

        CompletableFuture<Void> combineFuture = CompletableFuture.allOf(countryFuture, publicHolidaysFuture);

        combineFuture.join();
        String holiResponse;
        String  countryResponse;

        try {
            holiResponse = publicHolidaysFuture.get();
            countryResponse = countryFuture.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        ResBody resBody = ResBody.builder()
                .country(callGetCountryAPI(countryResponse))
                .publicHoliday(callGetPublicHolidayAPI(holiResponse))
                .build();
        return resBody;
//        return countryFuture.thenCombine(publicHolidaysFuture, (country, publicHolidays) ->
//                ResBody.builder()
//                        .country(country)
//                        .publicHoliday(publicHolidays)
//                        .build()
//        );
    }



}
