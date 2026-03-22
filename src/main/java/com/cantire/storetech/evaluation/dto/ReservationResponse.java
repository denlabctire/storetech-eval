package com.cantire.storetech.evaluation.dto;

import java.time.OffsetDateTime;

import com.cantire.storetech.evaluation.model.ReservationStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {

    private Long reservationId;

    private String sku;

    private Integer quantity;

    private Long customerId;

    private ReservationStatus status;

    private OffsetDateTime createdAt;
}
