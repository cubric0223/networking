package com.networkingProject.rightNow.service;

import com.networkingProject.rightNow.entity.User;
import com.networkingProject.rightNow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public Optional<User> getUserInfo(Long userId) {
        return userRepository.findByUserId(userId);
    }
}
