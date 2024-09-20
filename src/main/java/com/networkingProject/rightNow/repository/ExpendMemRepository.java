package com.networkingProject.rightNow.repository;

import com.networkingProject.rightNow.entity.ExpendMem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ExpendMemRepository extends JpaRepository<ExpendMem, Long> {
    // 특정 지출 항목에 연결된 모든 expend_mem 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM ExpendMem em WHERE em.expenditureId.expenditureId = :expenditureId")
    void deleteByExpenditureId(@Param("expenditureId") Long expenditureId);

    // expenditureId로 해당 지출에 참여한 사용자 이름 조회
    @Query("SELECT em.userId.name FROM ExpendMem em WHERE em.expenditureId.expenditureId = :expenditureId")
    List<String> findUserNamesByExpenditureId(@Param("expenditureId") Long expenditureId);
}
