package co.com.jara.api;

import co.com.jara.api.dto.request.*;
import co.com.jara.api.dto.response.ApiErrorResponse;
import co.com.jara.api.dto.response.FranchiseResponse;
import co.com.jara.api.dto.response.TopStockProductResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;

@Configuration
public class RouterRest {
    private static final String FRANCHISE_BASE = "/api/v1/franchises";
    private static final String BRANCH_BASE = FRANCHISE_BASE + "/{franchiseId}/branches";
    private static final String PRODUCT_BASE = BRANCH_BASE + "/{branchId}/products";

    @Bean
    public RouterFunction<ServerResponse> swaggerUiRedirect() {
        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Franchise API - Swagger UI</title>
                    <meta charset="utf-8"/>
                    <meta name="viewport" content="width=device-width, initial-scale=1">
                    <link rel="stylesheet" type="text/css" href="/webjars/swagger-ui/5.21.0/swagger-ui.css">
                </head>
                <body>
                <div id="swagger-ui"></div>
                <script src="/webjars/swagger-ui/5.21.0/swagger-ui-bundle.js"></script>
                <script src="/webjars/swagger-ui/5.21.0/swagger-ui-standalone-preset.js"></script>
                <script src="/swagger-init.js"></script>
                </body>
                </html>
                """;
        return RouterFunctions.route()
                .GET("/swagger-ui.html", req -> ServerResponse
                        .ok()
                        .contentType(MediaType.TEXT_HTML)
                        .bodyValue(html))
                .build();
    }

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = FRANCHISE_BASE,
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "createFranchise",
                    operation = @Operation(
                            operationId = "createFranchise",
                            summary = "Create a new franchise",
                            tags = {"Franchises"},
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = CreateFranchiseRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Franchise created",
                                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Validation error",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
                                    @ApiResponse(responseCode = "409", description = "Duplicate name",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = FRANCHISE_BASE,
                    method = RequestMethod.GET,
                    beanClass = Handler.class,
                    beanMethod = "getAllFranchises",
                    operation = @Operation(
                            operationId = "getAllFranchises",
                            summary = "Get all franchises",
                            tags = {"Franchises"},
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "List of franchises",
                                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = FranchiseResponse.class))))
                            }
                    )
            ),
            @RouterOperation(
                    path = FRANCHISE_BASE + "/{franchiseId}",
                    method = RequestMethod.PATCH,
                    beanClass = Handler.class,
                    beanMethod = "updateFranchiseName",
                    operation = @Operation(
                            operationId = "updateFranchiseName",
                            summary = "Update franchise name",
                            tags = {"Franchises"},
                            parameters = {
                                    @Parameter(name = "franchiseId", in = ParameterIn.PATH, required = true)
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = UpdateNameRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Franchise updated",
                                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Franchise not found",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = BRANCH_BASE,
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "addBranch",
                    operation = @Operation(
                            operationId = "addBranch",
                            summary = "Add a branch to a franchise",
                            tags = {"Branches"},
                            parameters = {
                                    @Parameter(name = "franchiseId", in = ParameterIn.PATH, required = true)
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = CreateBranchRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Branch added",
                                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Franchise not found",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = BRANCH_BASE + "/{branchId}",
                    method = RequestMethod.PATCH,
                    beanClass = Handler.class,
                    beanMethod = "updateBranchName",
                    operation = @Operation(
                            operationId = "updateBranchName",
                            summary = "Update branch name",
                            tags = {"Branches"},
                            parameters = {
                                    @Parameter(name = "franchiseId", in = ParameterIn.PATH, required = true),
                                    @Parameter(name = "branchId", in = ParameterIn.PATH, required = true)
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = UpdateNameRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Branch updated",
                                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Not found",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = PRODUCT_BASE,
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "addProduct",
                    operation = @Operation(
                            operationId = "addProduct",
                            summary = "Add a product to a branch",
                            tags = {"Products"},
                            parameters = {
                                    @Parameter(name = "franchiseId", in = ParameterIn.PATH, required = true),
                                    @Parameter(name = "branchId", in = ParameterIn.PATH, required = true)
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = CreateProductRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Product added",
                                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Not found",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = PRODUCT_BASE + "/{productId}",
                    method = RequestMethod.DELETE,
                    beanClass = Handler.class,
                    beanMethod = "removeProduct",
                    operation = @Operation(
                            operationId = "removeProduct",
                            summary = "Remove a product from a branch",
                            tags = {"Products"},
                            parameters = {
                                    @Parameter(name = "franchiseId", in = ParameterIn.PATH, required = true),
                                    @Parameter(name = "branchId", in = ParameterIn.PATH, required = true),
                                    @Parameter(name = "productId", in = ParameterIn.PATH, required = true)
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Product removed",
                                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Not found",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = PRODUCT_BASE + "/{productId}/stock",
                    method = RequestMethod.PATCH,
                    beanClass = Handler.class,
                    beanMethod = "updateProductStock",
                    operation = @Operation(
                            operationId = "updateProductStock",
                            summary = "Update product stock",
                            tags = {"Products"},
                            parameters = {
                                    @Parameter(name = "franchiseId", in = ParameterIn.PATH, required = true),
                                    @Parameter(name = "branchId", in = ParameterIn.PATH, required = true),
                                    @Parameter(name = "productId", in = ParameterIn.PATH, required = true)
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = UpdateStockRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Stock updated",
                                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Not found",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = PRODUCT_BASE + "/{productId}/name",
                    method = RequestMethod.PATCH,
                    beanClass = Handler.class,
                    beanMethod = "updateProductName",
                    operation = @Operation(
                            operationId = "updateProductName",
                            summary = "Update product name",
                            tags = {"Products"},
                            parameters = {
                                    @Parameter(name = "franchiseId", in = ParameterIn.PATH, required = true),
                                    @Parameter(name = "branchId", in = ParameterIn.PATH, required = true),
                                    @Parameter(name = "productId", in = ParameterIn.PATH, required = true)
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = UpdateNameRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Product name updated",
                                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Not found",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = FRANCHISE_BASE + "/{franchiseId}/top-stock",
                    method = RequestMethod.GET,
                    beanClass = Handler.class,
                    beanMethod = "getTopStockProducts",
                    operation = @Operation(
                            operationId = "getTopStockProducts",
                            summary = "Get product with highest stock per branch in a franchise",
                            tags = {"Reports"},
                            parameters = {
                                    @Parameter(name = "franchiseId", in = ParameterIn.PATH, required = true)
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Top stock products per branch",
                                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = TopStockProductResponse.class)))),
                                    @ApiResponse(responseCode = "404", description = "Franchise not found",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> franchiseRoutes(Handler handler) {
        return RouterFunctions.route()
                // Franchise routes
                .POST(FRANCHISE_BASE, accept(MediaType.APPLICATION_JSON), handler::createFranchise)
                .GET(FRANCHISE_BASE, handler::getAllFranchises)
                .PATCH(FRANCHISE_BASE + "/{franchiseId}", accept(MediaType.APPLICATION_JSON), handler::updateFranchiseName)
                // Branch routes
                .POST(BRANCH_BASE, accept(MediaType.APPLICATION_JSON), handler::addBranch)
                .PATCH(BRANCH_BASE + "/{branchId}", accept(MediaType.APPLICATION_JSON), handler::updateBranchName)
                // Product routes
                .POST(PRODUCT_BASE, accept(MediaType.APPLICATION_JSON), handler::addProduct)
                .DELETE(PRODUCT_BASE + "/{productId}", handler::removeProduct)
                .PATCH(PRODUCT_BASE + "/{productId}/stock", accept(MediaType.APPLICATION_JSON), handler::updateProductStock)
                .PATCH(PRODUCT_BASE + "/{productId}/name", accept(MediaType.APPLICATION_JSON), handler::updateProductName)
                // Report routes
                .GET(FRANCHISE_BASE + "/{franchiseId}/top-stock", handler::getTopStockProducts)
                .build();
    }
}
