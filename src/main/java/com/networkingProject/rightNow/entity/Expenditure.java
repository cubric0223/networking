package com.networkingProject.rightNow.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)  // Auditing 기능 활성화
public class Expenditure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long expenditureId;

    @Column(nullable = false, length = 20)
    private String expenditureName;

    @Column(nullable = false, length = 10)
    private int expenditureMoney;

    @Column(nullable = false, length = 10)
    private String classification;

    @Column(nullable = false, length = 100)
    private String receipt;

    @Column(length = 255)
    private String memo;

    @ManyToOne
    @JoinColumn(name = "travel_id", nullable = false)
    private Travel travelId;

    // 마지막 수정 날짜
    @LastModifiedDate
    @Column(name = "last_modified_date", nullable = false)
    private LocalDateTime lastModifiedDate;
}
