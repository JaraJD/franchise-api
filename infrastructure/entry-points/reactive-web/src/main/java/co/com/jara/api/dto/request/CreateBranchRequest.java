package co.com.jara.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateBranchRequest(
        @NotBlank(message = "Branch name must not be blank")
        @Size(min = 2, max = 100, message = "Branch name must be between 2 and 100 characters")
        String name
) {}
