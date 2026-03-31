package co.com.jara.exception;

public class ProductNotFoundException extends DomainException {

    public ProductNotFoundException(String productId) {
        super("PRODUCT_NOT_FOUND", "Product not found with id: " + productId);
    }
}
