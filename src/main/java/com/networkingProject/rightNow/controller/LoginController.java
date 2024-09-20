package com.networkingProject.rightNow.controller;

import com.networkingProject.rightNow.dto.Request.UserLoginRequestDTO;
import com.networkingProject.rightNow.dto.Request.UserSignUpRequestDTO;
import com.networkingProject.rightNow.entity.User;
import com.networkingProject.rightNow.service.LoginService;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import com.networkingProject.rightNow.dto.*;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {
    private final LoginService loginService;

    @PostMapping("/signUp")
    public ResponseEntity<MessageDTO> signUp(@Valid @RequestBody UserSignUpRequestDTO request, BindingResult bindingResult) {
        MessageDTO messageDTO = new MessageDTO();

        if (bindingResult.hasErrors()) { // 입력받은 request body 값에 정상적인 값들이 들어있는지 확인
            List<FieldError> list = bindingResult.getFieldErrors();
            for (FieldError error : list) {
                messageDTO.setMessage(error.getDefaultMessage());
                return new ResponseEntity<>(messageDTO, HttpStatus.BAD_REQUEST);
            }
        }

        boolean isDuplicated = loginService.checkEmailDuplication(request.getLoginId()); // 아이디 중복 체크

        if (isDuplicated) {
            messageDTO.setMessage("중복된 아이디입니다.");
            return new ResponseEntity<>(messageDTO, HttpStatus.BAD_REQUEST);
        }

        try {
            loginService.signUp(request); // 중복되지 않았으면 계정 생성
            messageDTO.setMessage("회원가입 성공");
            return new ResponseEntity<>(messageDTO, HttpStatus.CREATED);
        }catch(IllegalArgumentException e){
            messageDTO.setMessage("sign up failed.");
            return new ResponseEntity<>(messageDTO, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<MessageDTO> login(@Valid @RequestBody UserLoginRequestDTO request,
                                            HttpServletRequest httpServletRequest,
                                            HttpServletResponse response,
                                            BindingResult bindingResult) {
        MessageDTO messageDTO = new MessageDTO();

        // 입력받은 request body 값에 정상적인 값들이 들어있는지 확인
        if (bindingResult.hasErrors()) {
            List<FieldError> list = bindingResult.getFieldErrors();
            for (FieldError error : list) {
                messageDTO.setMessage(error.getDefaultMessage());
                return new ResponseEntity<>(messageDTO, HttpStatus.BAD_REQUEST);
            }
        }

        // 로그인 성공시 User 객체 리턴 or 실패시 null 리턴
        User user = loginService.login(request);

        if (user == null) {
            messageDTO.setMessage("로그인 실패");
            return new ResponseEntity<>(messageDTO, HttpStatus.BAD_REQUEST);
        }

        // 기존 세션 무효화 및 새로운 세션 생성
        httpServletRequest.getSession().invalidate();
        HttpSession session = httpServletRequest.getSession(true);
        session.setAttribute("userId", user.getUserId()); // 세션에 userId 등록
        session.setAttribute("loginId", user.getLoginId()); // 세션에 userId 등록
        session.setAttribute("name", user.getName()); // 세션에 getLoginId 등록
        session.setMaxInactiveInterval(1800); // 세션 기한 30분 설정
        System.out.println(session.getAttribute("userId"));
        System.out.println(session.getAttribute("name"));
        messageDTO.setMessage("로그인 성공");

        Cookie cookie = new Cookie("JSESSIONID", session.getId());

        cookie.setDomain("당장가자.메인.한국");

        // Set the path for the cookie
        cookie.setPath("/");

        cookie.setHttpOnly(true);

        // cookie.setMaxAge(3600); // 1 시간

        response.addCookie(cookie);

        return new ResponseEntity<>(messageDTO, HttpStatus.OK);
    }


    @GetMapping("/logout")
    public ResponseEntity<MessageDTO> logout(HttpServletRequest httpServletRequest) {
        MessageDTO messageDTO = new MessageDTO();
        HttpSession session = httpServletRequest.getSession(false);

        if (session == null) {
            messageDTO.setMessage("잘못된 접근입니다.");
            return new ResponseEntity<>(messageDTO, HttpStatus.UNAUTHORIZED);
        }
        session.invalidate(); // 세션 정보 삭제

        messageDTO.setMessage("로그아웃 성공");
        return new ResponseEntity<>(messageDTO, HttpStatus.OK);
    }


}
