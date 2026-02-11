package com.cantire.storetech.evaluation.service;

import com.cantire.storetech.evaluation.model.TaxInfo;

import java.util.List;

public interface TaxService {
    List<TaxInfo> getTaxesForRegion(String region, String countryCode);
}
