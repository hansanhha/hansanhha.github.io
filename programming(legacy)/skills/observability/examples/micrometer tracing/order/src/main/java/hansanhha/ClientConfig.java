package hansanhha;

import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ClientConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, Tracer tracer, Propagator propagator) {
        return builder
                .additionalInterceptors((request, body, execution) -> {
                    propagator.inject(
                            tracer.currentSpan().context(),
                            request,
                            (req, key, value) -> req.getHeaders().set(key, value)
                    );
                    return execution.execute(request, body);
                })
                .build();
    }

}
