package co.com.jara.mongo.adapter;


import co.com.jara.model.franchise.Franchise;
import co.com.jara.model.gateway.FranchiseRepository;
import co.com.jara.mongo.mapper.FranchiseEntityMapper;
import co.com.jara.mongo.repository.FranchiseMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FranchiseRepositoryAdapter implements FranchiseRepository {

    private final FranchiseMongoRepository mongoRepository;
    private final FranchiseEntityMapper mapper;

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        log.debug("Saving franchise: {}", franchise.name());
        return mongoRepository.save(mapper.toEntity(franchise))
                .map(mapper::toDomain)
                .doOnSuccess(f -> log.debug("Franchise saved with id: {}", f.id()));
    }

    @Override
    public Mono<Franchise> findById(String id) {
        log.debug("Finding franchise by id: {}", id);
        return mongoRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Franchise> findAll() {
        log.debug("Finding all franchises");
        return mongoRepository.findAll()
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return mongoRepository.existsByName(name);
    }

    @Override
    public Mono<Franchise> update(Franchise franchise) {
        log.debug("Updating franchise: {}", franchise.id());
        return mongoRepository.save(mapper.toEntity(franchise))
                .map(mapper::toDomain);
    }
}
