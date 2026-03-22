package com.cantire.storetech.evaluation.service;

import com.cantire.storetech.evaluation.dto.ReservationRequest;
import com.cantire.storetech.evaluation.dto.ReservationResponse;

public interface ReservationService {

    ReservationResponse reserve(ReservationRequest request);
}
