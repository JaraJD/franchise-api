package co.com.jara.model.product;

import lombok.Builder;
import lombok.With;

@Builder(toBuilder = true)
@With
public record Product(
        String id,
        String name,
        int stock
) {
    public Product withUpdatedStock(int newStock) {
        return this.toBuilder().stock(newStock).build();
    }

    public Product withUpdatedName(String newName) {
        return this.toBuilder().name(newName).build();
    }
}
