package co.com.jara.mongo.repository;

import co.com.jara.mongo.entity.FranchiseEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface FranchiseMongoRepository extends ReactiveMongoRepository<FranchiseEntity, String> {

    Mono<Boolean> existsByName(String name);
}
