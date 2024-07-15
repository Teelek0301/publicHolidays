package com.example.Countries.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Country {
    private String name;

    private List<String> Capital;

    private String region;

    private String googlemaps;
}