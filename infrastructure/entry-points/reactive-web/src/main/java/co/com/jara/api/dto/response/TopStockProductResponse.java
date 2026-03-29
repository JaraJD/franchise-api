package co.com.jara.api.dto.response;

public record TopStockProductResponse(
        String franchiseId,
        String franchiseName,
        String branchId,
        String branchName,
        ProductResponse product
) {}
