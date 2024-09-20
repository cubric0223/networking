package com.networkingProject.rightNow.dto.Request.Expenditure;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenditureRequestDTO {
    private List<String> name;
    private String expenditureName;
    private Long travelId;
    private String classification;
    private String receipt;
    private String memo;
    private int expenditureMoney;
}
