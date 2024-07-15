package com.example.Countries.controller;


import com.example.Countries.entity.ReqBody;
import com.example.Countries.entity.ResBody;
import com.example.Countries.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
public class CountryController {
    @Autowired
    private CountryService countryService;


        @PostMapping("/country")
    public ResBody getCountry(@RequestBody ReqBody reqBody) throws ExecutionException, InterruptedException {
        return countryService.runAsync(reqBody.getYear(), reqBody.getCountryCode());
    }
}
