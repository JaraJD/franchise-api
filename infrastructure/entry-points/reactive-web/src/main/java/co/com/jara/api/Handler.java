package co.com.jara.api;

import co.com.jara.api.dto.request.*;
import co.com.jara.api.dto.response.FranchiseResponse;
import co.com.jara.api.dto.response.TopStockProductResponse;
import co.com.jara.api.exception.RequestValidator;
import co.com.jara.api.mapper.FranchiseResponseMapper;
import co.com.jara.usecase.franchise.FranchiseUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
    private final FranchiseUseCase franchiseUseCase;
    private final FranchiseResponseMapper responseMapper;
    private final RequestValidator validator;


    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(CreateFranchiseRequest.class)
                .flatMap(validator::validate)
                .flatMap(req -> franchiseUseCase.createFranchise(req.name()))
                .map(responseMapper::toResponse)
                .flatMap(body -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(body));
    }

    public Mono<ServerResponse> updateFranchiseName(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(validator::validate)
                .flatMap(req -> franchiseUseCase.updateFranchiseName(franchiseId, req.name()))
                .map(responseMapper::toResponse)
                .flatMap(body -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(body));
    }

    public Mono<ServerResponse> getAllFranchises(ServerRequest request) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(franchiseUseCase.findAllFranchises().map(responseMapper::toResponse), FranchiseResponse.class);
    }

    public Mono<ServerResponse> addBranch(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        return request.bodyToMono(CreateBranchRequest.class)
                .flatMap(validator::validate)
                .flatMap(req -> franchiseUseCase.addBranch(franchiseId, req.name()))
                .map(responseMapper::toResponse)
                .flatMap(body -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(body));
    }

    public Mono<ServerResponse> updateBranchName(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(validator::validate)
                .flatMap(req -> franchiseUseCase.updateBranchName(franchiseId, branchId, req.name()))
                .map(responseMapper::toResponse)
                .flatMap(body -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(body));
    }


    public Mono<ServerResponse> addProduct(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        return request.bodyToMono(CreateProductRequest.class)
                .flatMap(validator::validate)
                .flatMap(req -> franchiseUseCase.addProduct(franchiseId, branchId, req.name(), req.stock()))
                .map(responseMapper::toResponse)
                .flatMap(body -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(body));
    }

    public Mono<ServerResponse> removeProduct(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        String productId = request.pathVariable("productId");
        return franchiseUseCase.removeProduct(franchiseId, branchId, productId)
                .map(responseMapper::toResponse)
                .flatMap(body -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(body));
    }

    public Mono<ServerResponse> updateProductStock(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        String productId = request.pathVariable("productId");
        return request.bodyToMono(UpdateStockRequest.class)
                .flatMap(validator::validate)
                .flatMap(req -> franchiseUseCase.updateProductStock(franchiseId, branchId, productId, req.stock()))
                .map(responseMapper::toResponse)
                .flatMap(body -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(body));
    }

    public Mono<ServerResponse> updateProductName(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        String productId = request.pathVariable("productId");
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(validator::validate)
                .flatMap(req -> franchiseUseCase.updateProductName(franchiseId, branchId, productId, req.name()))
                .map(responseMapper::toResponse)
                .flatMap(body -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(body));
    }


    public Mono<ServerResponse> getTopStockProducts(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(franchiseUseCase.getTopStockProductsPerBranch(franchiseId)
                                .map(responseMapper::toResponse), TopStockProductResponse.class);
    }
}
