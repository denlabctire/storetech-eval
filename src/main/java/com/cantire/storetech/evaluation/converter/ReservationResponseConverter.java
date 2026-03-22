package com.cantire.storetech.evaluation.converter;

import com.cantire.storetech.evaluation.dto.ReservationResponse;
import com.cantire.storetech.evaluation.model.ReservationItem;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ReservationResponseConverter {

    public static ReservationResponse toResponse(ReservationItem reservationItem) {
        ReservationResponse response = new ReservationResponse();
        response.setReservationId(reservationItem.getId());
        response.setSku(reservationItem.getSku());
        response.setQuantity(reservationItem.getQuantity());
        response.setCustomerId(reservationItem.getCustomerId());
        response.setStatus(reservationItem.getStatus());
        response.setCreatedAt(reservationItem.getCreatedAt());
        return response;
    }
}
