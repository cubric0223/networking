package com.networkingProject.rightNow.service;

import com.networkingProject.rightNow.dto.Request.UserLoginRequestDTO;
import com.networkingProject.rightNow.dto.Request.UserSignUpRequestDTO;
import com.networkingProject.rightNow.dto.UserInfoDTO;
import com.networkingProject.rightNow.entity.User;
import com.networkingProject.rightNow.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final UserRepository userRepository;

    // 이메일 중복 체크
    public boolean checkEmailDuplication(String email){
        return userRepository.existsByLoginId(email);
    }

    // 회원가입
    @Transactional
    public void signUp(UserSignUpRequestDTO request) throws IllegalArgumentException {
        User user = new User();
        user.setLoginId(request.getLoginId());
        user.setPassword(request.getPassword());
        user.setName(request.getName());
        user.setBank(request.getBank());
        user.setAccountName(request.getAccountName());
        user.setAccountNumber(request.getAccountNumber());
        userRepository.save(user);  // repository에 저장
    }

    // 로그인
    public User login(UserLoginRequestDTO request){
        Optional<User> optionalUser = userRepository.findByLoginId(request.getId());

        if(optionalUser.isEmpty() || !optionalUser.get().getPassword().equals(request.getPassword())){
            return null;
        }
        return optionalUser.get();
    }

    // 사용자 정보 업데이트
    @Transactional
    public void updateUserInfo(String loginId, UserInfoDTO request) {
        int updatedRows = userRepository.updateUserInfo(
                request.getName(),
                request.getBank(),
                request.getAccountName(),
                request.getAccountNumber(),
                loginId
        );

        // 업데이트된 행의 수를 기반으로 처리
        if (updatedRows == 0) {
            throw new EntityNotFoundException("User with loginId " + loginId + " not found or no changes were made.");
        } else {
            System.out.println(updatedRows + " row(s) updated successfully.");
        }    }
}
