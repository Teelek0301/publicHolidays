package com.example.Countries.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PublicHoliday {
    private String Date;
    private String localName;
    private String name;
    private String countryCode;
}
