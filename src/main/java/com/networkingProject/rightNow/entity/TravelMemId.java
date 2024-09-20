package com.networkingProject.rightNow.entity;

import java.io.Serializable;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TravelMemId implements Serializable {
    private Long travelId;
    private Long userId;
}
