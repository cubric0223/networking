package com.networkingProject.rightNow.dto.Request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TravelRecommendRequestDTO {
    private int budget;
    private LocalDate startDate;
    private LocalDate endDate;
    private int numOfPeople;
}
