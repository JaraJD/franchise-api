package co.com.jara.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateFranchiseRequest(
        @NotBlank(message = "Franchise name must not be blank")
        @Size(min = 2, max = 100, message = "Franchise name must be between 2 and 100 characters")
        String name
) {}
