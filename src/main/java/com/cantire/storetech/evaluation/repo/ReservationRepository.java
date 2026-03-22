package com.cantire.storetech.evaluation.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cantire.storetech.evaluation.model.ReservationItem;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationItem, Long> {
}
