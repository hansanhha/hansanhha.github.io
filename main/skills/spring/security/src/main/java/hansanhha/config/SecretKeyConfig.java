package hansanhha.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class SecretKeyConfig {

    @Value("${service.security.secret-key}")
    private String plainSecretKey;

    private SecretKeySpec secretKeySpec;
    private static final JWSAlgorithm algorithm = JWSAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] plainSecretKeyBytes = plainSecretKey.getBytes(StandardCharsets.UTF_8);

        secretKeySpec = new SecretKeySpec(plainSecretKeyBytes, algorithm.getName());
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKeySpec));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(secretKeySpec).macAlgorithm(MacAlgorithm.valueOf(algorithm.getName())).build();
    }
}
