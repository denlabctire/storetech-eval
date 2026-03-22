package com.cantire.storetech.evaluation.converter;

import com.cantire.storetech.evaluation.dto.ReservationRequest;
import com.cantire.storetech.evaluation.model.ReservationItem;
import com.cantire.storetech.evaluation.model.ReservationStatus;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ReservationRequestConverter {

    public static ReservationItem toEntity(ReservationRequest request, ReservationStatus status) {
        ReservationItem reservationItem = new ReservationItem();
        reservationItem.setSku(request.getSku());
        reservationItem.setQuantity(request.getQuantity());
        reservationItem.setCustomerId(request.getCustomerId());
        reservationItem.setStatus(status);
        return reservationItem;
    }
}
