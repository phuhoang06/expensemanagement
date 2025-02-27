// BankAccountRequest.java
package com.example.expensemanager.payload.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountRequest {

    @NotBlank(message = "Account name cannot be blank")
    private String accountName;

    @NotNull(message = "Initial balance cannot be null")
    @PositiveOrZero(message = "Initial balance must be positive or zero")
    private BigDecimal balance;

    private String description;
}