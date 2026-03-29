package co.com.jara.usecase.franchise;

import co.com.jara.exception.BranchNotFoundException;
import co.com.jara.exception.DuplicateNameException;
import co.com.jara.exception.FranchiseNotFoundException;
import co.com.jara.exception.ProductNotFoundException;
import co.com.jara.model.branch.Branch;
import co.com.jara.model.franchise.Franchise;
import co.com.jara.model.franchise.TopStockProduct;
import co.com.jara.model.gateway.FranchiseRepository;
import co.com.jara.model.product.Product;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.UUID;

@RequiredArgsConstructor
public class FranchiseUseCase {
    private final FranchiseRepository franchiseRepository;

    public Mono<Franchise> createFranchise(String name) {
        return franchiseRepository.existsByName(name)
                .flatMap(exists -> exists
                                ? Mono.error(new DuplicateNameException("Franchise", name))
                                : franchiseRepository.save(
                                Franchise.builder()
                                        .id(UUID.randomUUID().toString())
                                        .name(name)
                                        .branches(new ArrayList<>())
                                        .build()
                        )
                );
    }

    public Mono<Franchise> updateFranchiseName(String franchiseId, String newName) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException(franchiseId)))
                .flatMap(franchise -> franchiseRepository.update(franchise.updateName(newName)));
    }

    public Flux<Franchise> findAllFranchises() {
        return franchiseRepository.findAll();
    }


    public Mono<Franchise> addBranch(String franchiseId, String branchName) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException(franchiseId)))
                .flatMap(franchise -> {
                    Branch branch = Branch.builder()
                            .id(UUID.randomUUID().toString())
                            .name(branchName)
                            .products(new ArrayList<>())
                            .build();
                    return franchiseRepository.update(franchise.addBranch(branch));
                });
    }

    public Mono<Franchise> updateBranchName(String franchiseId, String branchId, String newName) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException(franchiseId)))
                .flatMap(franchise -> {
                    Branch branch = franchise.findBranch(branchId)
                            .orElseThrow(() -> new BranchNotFoundException(branchId));
                    return franchiseRepository.update(
                            franchise.updateBranch(branch.updateName(newName))
                    );
                });
    }


    public Mono<Franchise> addProduct(String franchiseId, String branchId, String productName, int stock) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException(franchiseId)))
                .flatMap(franchise -> {
                    Branch branch = franchise.findBranch(branchId)
                            .orElseThrow(() -> new BranchNotFoundException(branchId));
                    Product product = Product.builder()
                            .id(UUID.randomUUID().toString())
                            .name(productName)
                            .stock(stock)
                            .build();
                    return franchiseRepository.update(
                            franchise.updateBranch(branch.addProduct(product))
                    );
                });
    }

    public Mono<Franchise> removeProduct(String franchiseId, String branchId, String productId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException(franchiseId)))
                .flatMap(franchise -> {
                    Branch branch = franchise.findBranch(branchId)
                            .orElseThrow(() -> new BranchNotFoundException(branchId));
                    validateProductExists(branch, productId);
                    return franchiseRepository.update(
                            franchise.updateBranch(branch.removeProduct(productId))
                    );
                });
    }

    public Mono<Franchise> updateProductStock(String franchiseId, String branchId, String productId, int newStock) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException(franchiseId)))
                .flatMap(franchise -> {
                    Branch branch = franchise.findBranch(branchId)
                            .orElseThrow(() -> new BranchNotFoundException(branchId));
                    validateProductExists(branch, productId);
                    return franchiseRepository.update(
                            franchise.updateBranch(branch.updateProductStock(productId, newStock))
                    );
                });
    }

    public Mono<Franchise> updateProductName(String franchiseId, String branchId, String productId, String newName) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException(franchiseId)))
                .flatMap(franchise -> {
                    Branch branch = franchise.findBranch(branchId)
                            .orElseThrow(() -> new BranchNotFoundException(branchId));
                    validateProductExists(branch, productId);
                    return franchiseRepository.update(
                            franchise.updateBranch(branch.updateProductName(productId, newName))
                    );
                });
    }

    public Flux<TopStockProduct> getTopStockProductsPerBranch(String franchiseId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException(franchiseId)))
                .flatMapMany(franchise -> Flux.fromIterable(franchise.branches())
                        .flatMap(branch -> Mono.justOrEmpty(branch.topStockProduct())
                                .map(product -> new TopStockProduct(
                                        franchise.id(),
                                        franchise.name(),
                                        branch.id(),
                                        branch.name(),
                                        product
                                ))
                        )
                );
    }


    private void validateProductExists(Branch branch, String productId) {
        boolean exists = branch.products().stream()
                .anyMatch(p -> p.id().equals(productId));
        if (!exists) {
            throw new ProductNotFoundException(productId);
        }
    }
}
