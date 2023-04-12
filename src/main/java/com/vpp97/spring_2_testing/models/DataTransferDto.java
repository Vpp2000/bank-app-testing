package com.vpp97.spring_2_testing.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataTransferDto {
    private Long bankId;
    private Long sourceAccountId;
    private Long destinationAccountId;
    private BigDecimal amount;
}
