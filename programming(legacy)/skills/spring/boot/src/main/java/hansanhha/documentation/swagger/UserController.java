package hansanhha.documentation.swagger;

import hansanhha.documentation.swagger.schema.SuccessResponseSchema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "users")
@RestController
@RequestMapping(path = "/api/v1/users")
public class UserController {

    private static final Map<Long, User> userRepository = new HashMap<>();

    static {
        userRepository.put(1L, new User(1L, "user a"));
        userRepository.put(2L, new User(2L, "user b"));
        userRepository.put(3L, new User(3L, "user c"));
        userRepository.put(4L, new User(4L, "user d"));
    }

    @Operation(summary = "전체 사용자 목록 조회", description = "모든 사용자 정보를 반환한다")
    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userRepository.values().stream().toList());
    }

    @Operation(summary = "특정 사용자 조회", description = "id 값으로 사용자를 조회한다")
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userRepository.get(userId));
    }

    @Operation(summary = "사용자 생성", description = "id와 name 필드를 기반으로 새로운 사용자 엔티티를 생성한다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "사용자 생성 성공",
                content = {@Content(schema = @Schema(implementation = SuccessResponseSchema.class))}),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BAD_REQUEST")
    })
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody @Valid CreateUserRequest request) {
        User user = new User(request.userId(), request.userName());
        userRepository.put(request.userId(), user);
        return ResponseEntity.created(URI.create("localhost:8080/api/users/" + request.userId())).body(user);
    }

    @Operation(summary = "사용자 삭제", description = "로그인한 사용자가 자신의 아이디를 삭제한다")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable @Schema(description = "로그인한 사용자 id") Long userId) {
        userRepository.remove(userId);
        return ResponseEntity.ok(Map.of("message", "deleted user"));
    }

    @Schema(title = "사용자 생성 요청 DTO", example = "{\"id\": 1, \"userName\": \"hansanhha\"}")
    public record CreateUserRequest(

            @Min(value = 0, message = "0 이상의 아이디 값을 넣어주세요")
            @Schema (description = "고유한 사용자 아이디", example = "1234")
            Long userId,

            @NotBlank(message = "사용자 이름을 입력해주세요")
            @Size(min = 1, max = 15, message = "사용자 이름은 최소 1글자, 최대 15 글자 이하로 입력해야 됩니다")
            @Schema (description = "고유한 사용자 이름", example = "user x")
            String userName) {

    }

    @Schema(title = "사용자 정보 DTO", example = "{\"id\": 1, \"userName\": \"hansanhha\"}")
    public record User(
            Long id,
            String name) {
    }
}
