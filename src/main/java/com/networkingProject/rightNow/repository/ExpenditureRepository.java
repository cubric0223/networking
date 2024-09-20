package com.networkingProject.rightNow.repository;

import com.networkingProject.rightNow.dto.Response.ExpenditureSelectResponseDTO;
import com.networkingProject.rightNow.entity.Expenditure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {
    Optional<Expenditure> findByExpenditureId(Long ExpenditureId);

    @Query("SELECT e FROM Expenditure e WHERE e.travelId.travelId = :travelId")
    List<Expenditure> findByTravelId(@Param("travelId") Long travelId);

    @Query("SELECT new com.networkingProject.rightNow.dto.Response.ExpenditureSelectResponseDTO(" +
            "e.expenditureName, e.expenditureId, e.expenditureMoney, e.classification, e.receipt, e.memo, e.travelId.travelId, e.lastModifiedDate) " +
            "FROM Expenditure e WHERE e.travelId.travelId = :travelId")
    List<ExpenditureSelectResponseDTO> findByTravelIdResponse(@Param("travelId") Long travelId);


//    // 가장 최근에 추가된 expenditureId를 가져오는 쿼리
//    @Query("SELECT e FROM Expenditure e ORDER BY e.expenditureId DESC")
//    Expenditure findLatestExpenditure();

    @Modifying
    @Transactional
    @Query("DELETE FROM Expenditure e WHERE e.travelId.travelId = :travelId")
    void deleteByTravelId(@Param("travelId") Long travelId);
}
