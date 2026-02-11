package com.cantire.storetech.evaluation.service;

import com.cantire.storetech.evaluation.exception.InvalidCurrencyCodeException;
import com.cantire.storetech.evaluation.model.TaxInfo;
import com.cantire.storetech.evaluation.repo.TaxInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TaxServiceImpl implements TaxService {

    private static final Set<Locale> AVAILABLE_LOCALES = Set.of(Locale.CANADA);
    private final TaxInfoRepository taxInfoRepository;

    public static Optional<Locale> getLocaleFromCurrencyCode(String currencyCode) {
        return AVAILABLE_LOCALES.stream()
                .filter(locale -> {
                    try {
                        return Currency.getInstance(locale).getCurrencyCode().equals(currencyCode);
                    } catch (IllegalArgumentException e) {
                        return false;
                    }
                })
                .findFirst();
    }

    /**
     * Helper method to get applicable taxes for a region.
     *
     * @param region       The region (province abbreviation)
     * @param currencyCode the currency code (e.g., "CAD") to determine the country for tax lookup
     * @return List of applicable TaxInfo for the region
     */
    @Override
    public List<TaxInfo> getTaxesForRegion(String region, String currencyCode) {

        Locale locale = getLocaleFromCurrencyCode(currencyCode).
                orElseThrow(() -> new InvalidCurrencyCodeException("Invalid currency code: " + currencyCode));

        return taxInfoRepository.findByStateProvinceAndCountryCode(region, locale.getCountry());
    }
}
