package co.com.jara.mongo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "franchises")
public class FranchiseEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("name")
    private String name;

    @Field("branches")
    private List<BranchEntity> branches;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BranchEntity {

        @Field("id")
        private String id;

        @Field("name")
        private String name;

        @Field("products")
        private List<ProductEntity> products;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductEntity {

        @Field("id")
        private String id;

        @Field("name")
        private String name;

        @Field("stock")
        private int stock;
    }
}
