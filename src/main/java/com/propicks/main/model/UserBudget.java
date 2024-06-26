package com.propicks.main.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBudget {

    private BigDecimal minBudget;
    private BigDecimal maxBudget;

}
