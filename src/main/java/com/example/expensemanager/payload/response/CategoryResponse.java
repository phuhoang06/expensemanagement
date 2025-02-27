// CategoryResponse.java
package com.example.expensemanager.payload.response;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
}