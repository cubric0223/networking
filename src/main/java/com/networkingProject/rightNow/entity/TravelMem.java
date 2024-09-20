package com.networkingProject.rightNow.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(TravelMemId.class)
public class TravelMem {

    @Id
    @ManyToOne
    @JoinColumn(name = "travel_id")
    private Travel travelId;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;
}
