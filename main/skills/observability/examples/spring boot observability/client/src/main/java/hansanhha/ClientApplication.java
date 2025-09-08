package hansanhha;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    CommandLineRunner sendRequestToServer(ObservationRegistry registry, RestTemplate restTemplate) {
        Random random = new Random();
        List<String> lowCardinalityValues = Arrays.asList("userType1", "userType2", "userType3");
        return args -> {
            String highCardinalityUserId = String.valueOf(random.nextLong(100_000));
            Observation.createNotStarted("my.observation", registry)
                    .lowCardinalityKeyValue("userType", lowCardinalityValues.get(random.nextInt(0, 2)))
                    .highCardinalityKeyValue("userId", highCardinalityUserId)
                    .contextualName("command-line-runner")
                    .observe(() -> {
                        Logger log = LoggerFactory.getLogger(getClass());
                        log.info("Will send a request to the server");
                        String response = restTemplate.getForObject("http://localhost:8081/users/{userId}", String.class, highCardinalityUserId);
                        log.info("Got response [{}]", response);
                    });

        };
    }
}
