package com.cantire.storetech.evaluation.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * PriceInfo is a value object accessible only through its aggregate root (Product).
 * It represents pricing information for a product in a specific locale/currency.
 */
@Entity
@Data
@Table(name = "price_info")
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "product")
public class PriceInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private String currencyCode;

    private BigDecimal price;

    private ZonedDateTime effectiveDate;

    private ZonedDateTime expiryDate;
}
