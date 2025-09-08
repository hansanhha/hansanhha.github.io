package hansanhha.token;

import hansanhha.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Token {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "TEXT")
    private String tokenValue;

    private Instant issuedAt;

    private Instant expiredAt;

    private String accessId;

    @Setter
    private boolean used;

    @Builder
    public Token(User user, String tokenValue, Instant issuedAt, Instant expiredAt, String accessId) {
        this.user = user;
        this.tokenValue = tokenValue;
        this.issuedAt = issuedAt;
        this.expiredAt = expiredAt;
        this.accessId = accessId;
        this.used = true;
    }
}
