package co.com.jara.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
        String code,
        String message,
        List<String> details,
        Instant timestamp
) {
    public static ApiErrorResponse of(String code, String message) {
        return new ApiErrorResponse(code, message, null, Instant.now());
    }

    public static ApiErrorResponse of(String code, String message, List<String> details) {
        return new ApiErrorResponse(code, message, details, Instant.now());
    }
}
