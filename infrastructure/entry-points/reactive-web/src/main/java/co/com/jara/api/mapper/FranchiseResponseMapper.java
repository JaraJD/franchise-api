package co.com.jara.api.mapper;


import co.com.jara.api.dto.response.BranchResponse;
import co.com.jara.api.dto.response.FranchiseResponse;
import co.com.jara.api.dto.response.ProductResponse;
import co.com.jara.api.dto.response.TopStockProductResponse;
import co.com.jara.model.branch.Branch;
import co.com.jara.model.franchise.Franchise;
import co.com.jara.model.franchise.TopStockProduct;
import co.com.jara.model.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FranchiseResponseMapper {

    FranchiseResponse toResponse(Franchise franchise);

    BranchResponse toResponse(Branch branch);

    ProductResponse toResponse(Product product);

    TopStockProductResponse toResponse(TopStockProduct topStockProduct);
}
