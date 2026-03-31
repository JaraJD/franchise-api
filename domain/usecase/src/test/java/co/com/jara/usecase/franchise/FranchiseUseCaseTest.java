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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("FranchiseUseCase")
class FranchiseUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    private FranchiseUseCase useCase;


    private static final String FRANCHISE_ID = "franchise-1";
    private static final String BRANCH_ID = "branch-1";
    private static final String PRODUCT_ID = "product-1";

    private Product sampleProduct;
    private Branch sampleBranch;
    private Franchise sampleFranchise;

    @BeforeEach
    void setUp() {
        useCase = new FranchiseUseCase(franchiseRepository);

        sampleProduct = Product.builder()
                .id(PRODUCT_ID)
                .name("Widget A")
                .stock(50)
                .build();

        sampleBranch = Branch.builder()
                .id(BRANCH_ID)
                .name("Branch North")
                .products(new ArrayList<>(List.of(sampleProduct)))
                .build();

        sampleFranchise = Franchise.builder()
                .id(FRANCHISE_ID)
                .name("Franchise One")
                .branches(new ArrayList<>(List.of(sampleBranch)))
                .build();
    }


    @Nested
    @DisplayName("createFranchise")
    class CreateFranchise {

        @Test
        @DisplayName("should create franchise when name is unique")
        void shouldCreateFranchise() {
            when(franchiseRepository.existsByName("New Franchise")).thenReturn(Mono.just(false));
            when(franchiseRepository.save(any())).thenReturn(Mono.just(sampleFranchise));

            StepVerifier.create(useCase.createFranchise("New Franchise"))
                    .assertNext(f -> assertThat(f.name()).isEqualTo("Franchise One"))
                    .verifyComplete();
        }

        @Test
        @DisplayName("should fail with DuplicateNameException when name exists")
        void shouldFailWhenNameExists() {
            when(franchiseRepository.existsByName("Franchise One")).thenReturn(Mono.just(true));

            StepVerifier.create(useCase.createFranchise("Franchise One"))
                    .expectError(DuplicateNameException.class)
                    .verify();
        }
    }


    @Nested
    @DisplayName("updateFranchiseName")
    class UpdateFranchiseName {

        @Test
        @DisplayName("should update name when franchise exists")
        void shouldUpdateName() {
            Franchise updated = sampleFranchise.updateName("Updated Name");
            when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(sampleFranchise));
            when(franchiseRepository.update(any())).thenReturn(Mono.just(updated));

            StepVerifier.create(useCase.updateFranchiseName(FRANCHISE_ID, "Updated Name"))
                    .assertNext(f -> assertThat(f.name()).isEqualTo("Updated Name"))
                    .verifyComplete();
        }

        @Test
        @DisplayName("should fail with FranchiseNotFoundException when not found")
        void shouldFailWhenNotFound() {
            when(franchiseRepository.findById("unknown")).thenReturn(Mono.empty());

            StepVerifier.create(useCase.updateFranchiseName("unknown", "X"))
                    .expectError(FranchiseNotFoundException.class)
                    .verify();
        }
    }


    @Nested
    @DisplayName("addBranch")
    class AddBranch {

        @Test
        @DisplayName("should add branch to existing franchise")
        void shouldAddBranch() {
            when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(sampleFranchise));
            when(franchiseRepository.update(argThat(f -> f.branches().size() == 2)))
                    .thenReturn(Mono.just(sampleFranchise));

            StepVerifier.create(useCase.addBranch(FRANCHISE_ID, "Branch South"))
                    .assertNext(f -> assertThat(f).isNotNull())
                    .verifyComplete();
        }

        @Test
        @DisplayName("should fail when franchise not found")
        void shouldFailWhenFranchiseNotFound() {
            when(franchiseRepository.findById("x")).thenReturn(Mono.empty());

            StepVerifier.create(useCase.addBranch("x", "Branch"))
                    .expectError(FranchiseNotFoundException.class)
                    .verify();
        }
    }


    @Nested
    @DisplayName("updateBranchName")
    class UpdateBranchName {

        @Test
        @DisplayName("should update branch name")
        void shouldUpdateBranchName() {
            Franchise updated = sampleFranchise.updateBranch(sampleBranch.updateName("New Branch Name"));
            when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(sampleFranchise));
            when(franchiseRepository.update(any())).thenReturn(Mono.just(updated));

            StepVerifier.create(useCase.updateBranchName(FRANCHISE_ID, BRANCH_ID, "New Branch Name"))
                    .assertNext(f -> assertThat(f.branches().get(0).name()).isEqualTo("New Branch Name"))
                    .verifyComplete();
        }

        @Test
        @DisplayName("should fail when branch not found")
        void shouldFailWhenBranchNotFound() {
            when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(sampleFranchise));

            StepVerifier.create(useCase.updateBranchName(FRANCHISE_ID, "unknown-branch", "X"))
                    .expectError(BranchNotFoundException.class)
                    .verify();
        }
    }


    @Nested
    @DisplayName("addProduct")
    class AddProduct {

        @Test
        @DisplayName("should add product to branch")
        void shouldAddProduct() {
            when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(sampleFranchise));
            when(franchiseRepository.update(any())).thenReturn(Mono.just(sampleFranchise));

            StepVerifier.create(useCase.addProduct(FRANCHISE_ID, BRANCH_ID, "Product B", 10))
                    .assertNext(f -> assertThat(f).isNotNull())
                    .verifyComplete();
        }

        @Test
        @DisplayName("should fail when branch not found")
        void shouldFailWhenBranchNotFound() {
            when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(sampleFranchise));

            StepVerifier.create(useCase.addProduct(FRANCHISE_ID, "unknown", "Product", 5))
                    .expectError(BranchNotFoundException.class)
                    .verify();
        }
    }


    @Nested
    @DisplayName("removeProduct")
    class RemoveProduct {

        @Test
        @DisplayName("should remove product from branch")
        void shouldRemoveProduct() {
            when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(sampleFranchise));
            when(franchiseRepository.update(any())).thenReturn(Mono.just(sampleFranchise));

            StepVerifier.create(useCase.removeProduct(FRANCHISE_ID, BRANCH_ID, PRODUCT_ID))
                    .assertNext(f -> assertThat(f).isNotNull())
                    .verifyComplete();
        }

        @Test
        @DisplayName("should fail when product not found")
        void shouldFailWhenProductNotFound() {
            when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(sampleFranchise));

            StepVerifier.create(useCase.removeProduct(FRANCHISE_ID, BRANCH_ID, "unknown-product"))
                    .expectError(ProductNotFoundException.class)
                    .verify();
        }
    }


    @Nested
    @DisplayName("updateProductStock")
    class UpdateProductStock {

        @Test
        @DisplayName("should update stock successfully")
        void shouldUpdateStock() {
            Franchise updated = sampleFranchise.updateBranch(
                    sampleBranch.updateProductStock(PRODUCT_ID, 99));
            when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(sampleFranchise));
            when(franchiseRepository.update(any())).thenReturn(Mono.just(updated));

            StepVerifier.create(useCase.updateProductStock(FRANCHISE_ID, BRANCH_ID, PRODUCT_ID, 99))
                    .assertNext(f -> {
                        int stock = f.branches().get(0).products().get(0).stock();
                        assertThat(stock).isEqualTo(99);
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("should fail when product not found in branch")
        void shouldFailWhenProductNotFound() {
            when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(sampleFranchise));

            StepVerifier.create(useCase.updateProductStock(FRANCHISE_ID, BRANCH_ID, "ghost-id", 5))
                    .expectError(ProductNotFoundException.class)
                    .verify();
        }
    }


    @Nested
    @DisplayName("getTopStockProductsPerBranch")
    class GetTopStockProducts {

        @Test
        @DisplayName("should return top stock product per branch")
        void shouldReturnTopStockPerBranch() {
            Product highStock = Product.builder().id("p2").name("Widget B").stock(200).build();
            Branch branchWithTwo = sampleBranch.addProduct(highStock);
            Franchise franchiseWithProducts = sampleFranchise.updateBranch(branchWithTwo);

            when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(franchiseWithProducts));

            StepVerifier.create(useCase.getTopStockProductsPerBranch(FRANCHISE_ID))
                    .assertNext(top -> {
                        assertThat(top).isInstanceOf(TopStockProduct.class);
                        assertThat(top.product().stock()).isEqualTo(200);
                        assertThat(top.branchId()).isEqualTo(BRANCH_ID);
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("should emit empty when branch has no products")
        void shouldEmitEmptyWhenNoBranchProducts() {
            Branch emptyBranch = Branch.builder()
                    .id(BRANCH_ID).name("Empty Branch").products(new ArrayList<>()).build();
            Franchise franchiseEmpty = Franchise.builder()
                    .id(FRANCHISE_ID).name("F").branches(List.of(emptyBranch)).build();

            when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(franchiseEmpty));

            StepVerifier.create(useCase.getTopStockProductsPerBranch(FRANCHISE_ID))
                    .verifyComplete();
        }

        @Test
        @DisplayName("should fail when franchise not found")
        void shouldFailWhenFranchiseNotFound() {
            when(franchiseRepository.findById("none")).thenReturn(Mono.empty());

            StepVerifier.create(useCase.getTopStockProductsPerBranch("none"))
                    .expectError(FranchiseNotFoundException.class)
                    .verify();
        }
    }
}
