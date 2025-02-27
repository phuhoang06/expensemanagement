// CategoryRequest.java
package com.example.expensemanager.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {
    @NotBlank(message = "Category name cannot be blank")
    @Size(max = 255, message = "Category name must be less than 255 characters")
    private String name;

    private String description;
}