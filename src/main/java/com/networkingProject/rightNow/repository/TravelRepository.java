package com.networkingProject.rightNow.repository;

import com.networkingProject.rightNow.entity.Travel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelRepository extends JpaRepository<Travel, Long> {
    // 여행이름 중복 확인
    boolean existsByTravelName(String travelName);
}
