package com.outsourceowl.dto;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BalanceDTO {
    
    @NotNull
    @Min(1)
    @Max(10000)
    private Double balance;
}
