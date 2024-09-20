package com.networkingProject.rightNow.dto.Request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserLoginRequestDTO {
    @NotNull(message = "id를 입력해주세요.")
    private String id;
    @NotNull(message = "비밀번호를 입력해주세요.")
    private String password;
}
