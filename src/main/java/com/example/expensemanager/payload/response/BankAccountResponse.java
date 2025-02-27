// BankAccountResponse.java
package com.example.expensemanager.payload.response;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountResponse {
    private Long id;
    private String accountName;
    private BigDecimal balance;
    private String description;
}