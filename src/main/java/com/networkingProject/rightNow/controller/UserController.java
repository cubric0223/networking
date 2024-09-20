package com.networkingProject.rightNow.controller;

import com.networkingProject.rightNow.dto.MessageDTO;
import com.networkingProject.rightNow.dto.UserInfoDTO;
import com.networkingProject.rightNow.entity.User;
import com.networkingProject.rightNow.service.LoginService;
import com.networkingProject.rightNow.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final LoginService loginService;

    @GetMapping("/userInfo")
    public Map<String, List<UserInfoDTO>> getUserInfo(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);
        if(session == null || session.getAttribute("userId") == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not Login or session expired : getUserInfo method");
        }
        Long userId = (Long) session.getAttribute("userId");
        Optional<User> userInfo = userService.getUserInfo(userId);

        List<UserInfoDTO> collect = userInfo.stream()
                .map(user -> new UserInfoDTO(
                        user.getLoginId(),
                        user.getName(),
                        user.getAccountName(),
                        user.getAccountNumber(),
                        user.getBank()
                ))
                .collect(Collectors.toList());

        Map<String, List<UserInfoDTO>> response = new HashMap<>();
        response.put("info", collect);
        return response;
    }

    @PutMapping("/update")
    public ResponseEntity<MessageDTO> updateUserInfo(@Valid @RequestBody UserInfoDTO request,
                                                     HttpServletRequest httpServletRequest) {
        MessageDTO messageDTO = new MessageDTO();
        HttpSession session = httpServletRequest.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            messageDTO.setMessage("로그인이 필요합니다.");
            return new ResponseEntity<>(messageDTO, HttpStatus.UNAUTHORIZED);
        }

        String loginId = (String) session.getAttribute("loginId");
        System.out.println(loginId);
        try {
            loginService.updateUserInfo(loginId, request);
            messageDTO.setMessage("정보 수정 성공");
            return new ResponseEntity<>(messageDTO, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            messageDTO.setMessage(e.getMessage());
            return new ResponseEntity<>(messageDTO, HttpStatus.BAD_REQUEST);
        }
    }
}
