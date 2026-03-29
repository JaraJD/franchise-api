package co.com.jara.api.dto.request;

import jakarta.validation.constraints.Min;

public record UpdateStockRequest(
        @Min(value = 0, message = "Stock must be zero or greater")
        int stock
) {}
