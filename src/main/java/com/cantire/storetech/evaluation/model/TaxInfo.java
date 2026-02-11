package com.cantire.storetech.evaluation.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TaxInfo represents applicable tax rates for a specific province/state and tax type.
 */
@Entity
@Data
@Table(name = "tax_info")
@NoArgsConstructor
@AllArgsConstructor
public class TaxInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String countryCode;

    private String stateProvince;

    private Double percentage;

    @Enumerated(EnumType.STRING)
    private TaxType taxType;

    private String name;

    public enum TaxType {
        HST,  // Harmonized Sales Tax
        PST,  // Provincial Sales Tax
        GST   // Goods and Services Tax
    }
}
