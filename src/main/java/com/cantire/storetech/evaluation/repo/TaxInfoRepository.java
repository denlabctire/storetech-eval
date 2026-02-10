package com.cantire.storetech.evaluation.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cantire.storetech.evaluation.model.TaxInfo;

@Repository
public interface TaxInfoRepository extends JpaRepository<TaxInfo, Long> {
    List<TaxInfo> findByStateProvince(String stateProvince);
}
