package hansanhha.documentation.swagger.schema;

import io.swagger.v3.oas.annotations.media.Schema;

public record FailureResponseSchema<T> (

        @Schema(description = "비즈니스 로직 처리 성공 여부", example = "false")
        boolean success,

        @Schema(description = "비즈니스 로직 결과에 따른 human-readable 응답 메시지", example = "failure cause")
        String message,

        @Schema(description = "필요 응답 데이터")
        T data) {
}
