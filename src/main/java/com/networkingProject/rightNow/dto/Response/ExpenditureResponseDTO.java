package com.networkingProject.rightNow.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ExpenditureResponseDTO {
    private String expenditureName;
    private int expenditureMoney;
    private String receipt;
    private String memo;
    private List<String> userNames;
}
