package com.cantire.storetech.evaluation.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {

    private Long id;

    private String firstName;

    private String lastName;

    private boolean autoPayEnabled;

    private List<String> associatedCreditCardIds;
}
