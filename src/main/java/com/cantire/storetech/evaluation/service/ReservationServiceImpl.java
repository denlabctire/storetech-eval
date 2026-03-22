package com.cantire.storetech.evaluation.service;

import org.springframework.stereotype.Service;

import com.cantire.storetech.evaluation.dto.ReservationRequest;
import com.cantire.storetech.evaluation.dto.ReservationResponse;
import com.cantire.storetech.evaluation.repo.ReservationRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final EntityManager entityManager;
    private final ReservationRepository reservationRepository;

    /**
     * This method handles the reservation logic. It first checks if the
     * requested SKU exists and has enough available *inventory*.
     *
     * If the inventory is sufficient, it deducts the requested quantity from
     * the inventory and saves the reservation with an accepted status.
     * Otherwise, it saves the reservation with a rejected status and throws an
     * OutOfStockException.
     *
     *
     * see ReservationRequestConverter for converting the request to an entity
     * and ReservationResponseConverter for converting the entity to a response.
     */
    @Override
    public ReservationResponse reserve(ReservationRequest request) {
        //TODO Implement the reservation logic here
        return null;
    }
}
