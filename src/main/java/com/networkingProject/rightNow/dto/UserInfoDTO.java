package com.networkingProject.rightNow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    private String loginId;
    private String name;
    private String accountName;
    private int accountNumber;
    private String bank;
}
