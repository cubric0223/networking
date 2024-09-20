package com.networkingProject.rightNow.dto.Request.Expenditure;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CollectionOfMoneyRequestDTO {
    private Long travelId;
    private int collectionOfMoney;
}
