// ReminderResponse.java
package com.example.expensemanager.payload.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReminderResponse {
    private Long id;
    private String message;
    private LocalDateTime reminderDateTime;
    private boolean isSent;
    private Long transactionId; // Can be null
    private String transactionDescription;  // Optional: Description of the related transaction.

}