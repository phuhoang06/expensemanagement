//ReminderRequest.java
package com.example.expensemanager.payload.request;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReminderRequest {
    @NotBlank(message = "Message cannot be blank")
    private String message;

    @NotNull(message = "Reminder date and time cannot be null")
    @Future(message = "Reminder date and time must be in the future")
    private LocalDateTime reminderDateTime;

    private Long transactionId; // Optional
}