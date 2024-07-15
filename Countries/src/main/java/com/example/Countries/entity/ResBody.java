package com.example.Countries.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ResBody {
    private Country country;
    private List<PublicHoliday> publicHoliday;
}
