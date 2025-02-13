package hansanhha.documentation.swagger.schema;

import io.swagger.v3.oas.annotations.media.Schema;

public record SuccessResponseSchema<T> (

        @Schema(description = "비즈니스 로직 처리 성공 여부", example = "true")
        boolean success,

        @Schema(description = "비즈니스 로직 결과에 따른 human-readable 응답 메시지", example = "response message")
        String message,

        @Schema(description = "필요 응답 데이터", example = "data")
        T data) {
}
