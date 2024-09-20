package com.networkingProject.rightNow.dto.Request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TravelRequestDTO {
    @NotNull(message = "시작 날짜를 입력해주세요.")
    private LocalDate startDate;

    @NotNull(message = "종료 날짜를 입력해주세요.")
    private LocalDate endDate;

    @NotNull(message = "메모를 입력해주세요.")
    private String memo;

    private Integer collectionOfMoney;

    @NotNull(message = "여행이름을 입력해주세요.")
    private String travelName;
}
