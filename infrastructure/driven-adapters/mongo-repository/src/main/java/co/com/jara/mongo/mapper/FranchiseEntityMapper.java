package co.com.jara.mongo.mapper;


import co.com.jara.model.branch.Branch;
import co.com.jara.model.franchise.Franchise;
import co.com.jara.model.product.Product;
import co.com.jara.mongo.entity.FranchiseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FranchiseEntityMapper {

    FranchiseEntity toEntity(Franchise franchise);

    Franchise toDomain(FranchiseEntity entity);

    FranchiseEntity.BranchEntity toEntity(Branch branch);

    Branch toDomain(FranchiseEntity.BranchEntity entity);

    FranchiseEntity.ProductEntity toEntity(Product product);

    Product toDomain(FranchiseEntity.ProductEntity entity);
}
