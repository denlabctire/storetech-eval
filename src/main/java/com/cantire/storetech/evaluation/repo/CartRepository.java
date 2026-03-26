package com.cantire.storetech.evaluation.repo;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cantire.storetech.evaluation.model.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("SELECT c FROM Cart c WHERE c.updatedAt between :startZdt AND :endZdt")
    List<Cart> findAllBetweenCutoffTimes(ZonedDateTime startZdt, ZonedDateTime endZdt);
}
