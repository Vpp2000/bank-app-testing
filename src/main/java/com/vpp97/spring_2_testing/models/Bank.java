package com.vpp97.spring_2_testing.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bank {
    private Long id;
    private String name;
    private Integer totalTransfers;
}
