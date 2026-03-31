package co.com.jara.api.exception;


import co.com.jara.api.dto.response.ApiErrorResponse;
import co.com.jara.exception.BranchNotFoundException;
import co.com.jara.exception.DomainException;
import co.com.jara.exception.FranchiseNotFoundException;
import co.com.jara.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Order(-2)
@Component
@RequiredArgsConstructor
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status;
        ApiErrorResponse errorResponse;

        if (ex instanceof NoResourceFoundException) {
            exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
            return exchange.getResponse().setComplete();
        }

        log.error("Unhandled error: {}", ex.getMessage(), ex);

        if (ex instanceof ValidationException ve) {
            status = HttpStatus.BAD_REQUEST;
            errorResponse = ApiErrorResponse.of("VALIDATION_ERROR", ve.getMessage(), ve.getDetails());

        } else if (ex instanceof FranchiseNotFoundException
                || ex instanceof BranchNotFoundException
                || ex instanceof ProductNotFoundException) {
            status = HttpStatus.NOT_FOUND;
            errorResponse = ApiErrorResponse.of(((DomainException) ex).getCode(), ex.getMessage());

        } else if (ex instanceof DomainException de) {
            status = HttpStatus.CONFLICT;
            errorResponse = ApiErrorResponse.of(de.getCode(), de.getMessage());

        } else if (ex instanceof org.springframework.web.server.ResponseStatusException rse) {
            status = HttpStatus.valueOf(rse.getStatusCode().value());
            errorResponse = ApiErrorResponse.of("REQUEST_ERROR", rse.getReason() != null ? rse.getReason() : "Bad request");

        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            errorResponse = ApiErrorResponse.of("INTERNAL_ERROR", "An unexpected error occurred. Please try again later.");
        }

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);

        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
