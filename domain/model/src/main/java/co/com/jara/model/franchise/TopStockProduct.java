package co.com.jara.model.franchise;


import co.com.jara.model.product.Product;

public record TopStockProduct(
        String franchiseId,
        String franchiseName,
        String branchId,
        String branchName,
        Product product
) {}
