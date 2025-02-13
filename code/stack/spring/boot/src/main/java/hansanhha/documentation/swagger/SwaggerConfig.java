package hansanhha.documentation.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

@OpenAPIDefinition(
    info = @Info(
            title = "springboot webmvc openapi example docs",
            description = "using springdoc-openapi library",
            version = "v1"
    ),
    tags = {
            @Tag(name = "users", description = "APIs related to user management"),
            @Tag(name = "products", description = "APIs related to product management")
    },
    servers = {
            @Server(url = "localhost:8080", description = "localhost")
    }
)
@Configuration
public class SwaggerConfig {

    private static final String BEARER = "Bearer";
    private static final String JWT = "JWT";

    @Bean
    public OpenAPI openAPI() {
        // SecurityRequirement: openapi 문서에 보안 요구사항을 정의한다
        SecurityRequirement security = new SecurityRequirement();
        security.addList(JWT);

        // Components: openapi에서 사용할 보안 스키마, 파라미터, 응답을 정의한다
        Components components = new Components();
        components.addSecuritySchemes(JWT, getJwtScheme());

        registerCommonSchema(components);
        registerCommonErrorResponse(components);

        return new OpenAPI()
                .addSecurityItem(security)
                .components(components);
    }

    // SecurityScheme: 보안 스키마의 세부 사항을 정의한다
    private SecurityScheme getJwtScheme() {
        return new SecurityScheme()
                .name(JWT)
                .type(SecurityScheme.Type.HTTP) // 인증 타입
                .scheme(BEARER) // 인증 방식
                .bearerFormat(JWT); // 토큰 형식
    }

    private void registerCommonSchema(Components components) {
        components.addSchemas("FailureResponse",
                new Schema<>()
                        .type("object")
                        .properties(Map.of(
                                "success", new Schema<>().type("boolean").example(false),
                                "message", new Schema<>().type("string").example("failure cause"),
                                "data", new Schema<>().type("object").example("data"))
                        ));
    }

    // 공통 응답 정의
    private void registerCommonErrorResponse(Components components) {
        components.addResponses(HttpStatus.BAD_REQUEST.name(), new ApiResponse().content(new Content().addMediaType(APPLICATION_PROBLEM_JSON_VALUE, new MediaType().schema(new Schema<>().$ref("#/components/schemas/FailureResponse")))));
    }

}
