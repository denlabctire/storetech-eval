package com.cantire.storetech.evaluation.service;

import com.cantire.storetech.evaluation.model.TaxInfo;
import com.cantire.storetech.evaluation.repo.TaxInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaxServiceImpl implements TaxService {

    private final TaxInfoRepository taxInfoRepository;

    /**
     * Helper method to get applicable taxes for a region.
     *
     * @param region The region (province abbreviation)
     * @return List of applicable TaxInfo for the region
     */
    @Override
    public List<TaxInfo> getTaxesForRegion(String region) {
        return taxInfoRepository.findByStateProvince(region);
    }
}
