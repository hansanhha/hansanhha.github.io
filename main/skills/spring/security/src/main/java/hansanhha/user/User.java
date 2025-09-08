package hansanhha.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "users")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {

    @Id
    @GeneratedValue
    private Long id;

    private String nickname;

    @Column(unique = true)
    private String email;

    private String provider;

    @Column(unique = true)
    private String userNumber;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private Role role;

    public static User createNormalUser(String nickname, String email, String provider, String userNumber) {
        User user = new User();
        user.nickname = nickname;
        user.email = email;
        user.provider = provider;
        user.userNumber = userNumber;
        user.role = Role.USER;
        user.createdAt = LocalDateTime.now();
        return user;
    }
}
