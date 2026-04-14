package org.example.features.contract.dto;

import lombok.Data;

import java.util.List;

@Data
public class ContractTermsUpdateDTO {
    private List<String> qualityTerms;
    private List<String> cancelTerms;
    private String extraNotes;
}
