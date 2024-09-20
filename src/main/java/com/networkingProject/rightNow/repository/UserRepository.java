package com.networkingProject.rightNow.repository;

import com.networkingProject.rightNow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일 중복 체크
    boolean existsByLoginId(String loginId);

    // 로그인 ID로 사용자 조회
    Optional<User> findByLoginId(String name);
    Optional<User> findByName(String name);
    Optional<User> findByUserId(Long userId);


    // 회원가입 (User 객체 저장)

    // 사용자 정보 업데이트
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.name = :name, u.bank = :bank, u.accountName = :accountName, u.accountNumber = :accountNumber WHERE u.loginId = :loginId")
    int updateUserInfo(@Param("name") String name,
                        @Param("bank") String bank,
                        @Param("accountName") String accountName,
                        @Param("accountNumber") int accountNumber,
                        @Param("loginId") String loginId);
}