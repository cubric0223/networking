package com.networkingProject.rightNow.dto.Response;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class ExpenditureSelectResponseDTO {
    private String expenditureName;
    private Long expenditureId;
    private int expenditureMoney;
    private String classification;
    private String receipt;
    private String memo;
    private Long travelId;
    private LocalDateTime lastModifiedDate;
}
