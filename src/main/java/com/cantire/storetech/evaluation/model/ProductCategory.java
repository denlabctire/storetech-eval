package com.cantire.storetech.evaluation.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table
@Data
public class ProductCategory {

    @Id
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private HiearchyLevel hierarchyLevel;

}

