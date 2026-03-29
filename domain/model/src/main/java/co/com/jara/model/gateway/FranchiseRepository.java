package co.com.jara.model.gateway;

import co.com.jara.model.franchise.Franchise;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranchiseRepository {

    Mono<Franchise> save(Franchise franchise);

    Mono<Franchise> findById(String id);

    Flux<Franchise> findAll();

    Mono<Boolean> existsByName(String name);

    Mono<Franchise> update(Franchise franchise);
}
