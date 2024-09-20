package com.networkingProject.rightNow.repository;

import com.networkingProject.rightNow.entity.Travel;
import com.networkingProject.rightNow.entity.TravelMem;
import com.networkingProject.rightNow.entity.TravelMemId;
import com.networkingProject.rightNow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TravelMemRepository extends JpaRepository<TravelMem, TravelMemId> {
    // 현재 유저가 여행계획에 중복으로 참여했는지 검사
    boolean existsByTravelIdAndUserId(Travel travel, User user);

    // 특정 유저가 참여 중인 여행 목록
    @Query("SELECT t FROM TravelMem tm JOIN tm.travelId t WHERE tm.userId.userId = :userId ORDER BY t.travelId DESC")
    List<Travel> findAllTravelsByUserId(@Param("userId") Long userId);

    // 특정 여행에 참여 중인 모든 멤버의 이름
    @Query("SELECT u.name FROM TravelMem tm JOIN tm.userId u WHERE tm.travelId.travelId = :travelId")
    List<String> findAllMemberNamesByTravelId(@Param("travelId") Long travelId);

    // travel_id로 TravelMem 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM TravelMem tm WHERE tm.travelId.travelId = :travelId")
    void deleteByTravelId(@Param("travelId") Long travelId);

    // 특정 지출 항목에 연결된 모든 expend_mem 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM ExpendMem em WHERE em.expenditureId.expenditureId = :expenditureId")
    void deleteByExpenditureId(@Param("expenditureId") Long expenditureId);
}
