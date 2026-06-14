package hansanhha;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration(
        proxyBeanMethods = false
)
public class UserConfig {

    @Profile("default")
    @Qualifier("default user")
    @Bean
    User defaultUser() {
        return new User("default user");
    }

    @Profile("dev")
    @Qualifier("dev user")
    @Bean
    User devUser() {
        return new User("dev user");
    }

    @Profile("prod")
    @Qualifier("prod user")
    @Bean
    User prodUser() {
        return new User("prod user");
    }

    @Profile("staging")
    @Qualifier("staging user")
    @Bean
    User stagingUser() {
        return new User("staging user");
    }

}
