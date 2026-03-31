package co.com.jara.model.branch;

import co.com.jara.model.product.Product;
import lombok.Builder;
import lombok.With;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Builder(toBuilder = true)
@With
public record Branch(
        String id,
        String name,
        List<Product> products
) {
    public Branch addProduct(Product product) {
        List<Product> updated = new ArrayList<>(products);
        updated.add(product);
        return this.toBuilder().products(updated).build();
    }

    public Branch removeProduct(String productId) {
        List<Product> updated = products.stream()
                .filter(p -> !p.id().equals(productId))
                .toList();
        return this.toBuilder().products(updated).build();
    }

    public Branch updateProductStock(String productId, int newStock) {
        List<Product> updated = products.stream()
                .map(p -> p.id().equals(productId) ? p.withUpdatedStock(newStock) : p)
                .toList();
        return this.toBuilder().products(updated).build();
    }

    public Branch updateProductName(String productId, String newName) {
        List<Product> updated = products.stream()
                .map(p -> p.id().equals(productId) ? p.withUpdatedName(newName) : p)
                .toList();
        return this.toBuilder().products(updated).build();
    }

    public Optional<Product> topStockProduct() {
        return products.stream()
                .max(Comparator.comparingInt(Product::stock));
    }

    public Branch updateName(String newName) {
        return this.toBuilder().name(newName).build();
    }
}
