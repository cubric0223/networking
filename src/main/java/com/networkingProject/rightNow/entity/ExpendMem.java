package com.networkingProject.rightNow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpendMem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ExpendMemId;

    @ManyToOne
    @JoinColumn(name = "expenditure_id")
    private Expenditure expenditureId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;
}
