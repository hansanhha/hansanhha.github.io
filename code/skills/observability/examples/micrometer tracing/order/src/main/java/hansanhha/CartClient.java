package hansanhha;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class CartClient {

    private static final URI CART_REQUEST_URI = URI.create("http://localhost:8081/cart");

    private final RestTemplate restTemplate;
    private final Tracer tracer;

    public CartClient(RestTemplate restTemplate, Tracer tracer) {
        this.restTemplate = restTemplate;
        this.tracer = tracer;
    }

    public void decreaseQuantity(int productId, int quantity) {
        Span cartClientSpan = tracer.nextSpan()
                .name("call cart service")
                .tag("productId", productId)
                .tag("quantity", quantity)
                .start();

        try (Tracer.SpanInScope cartClientSpanInScope = tracer.withSpan(cartClientSpan)) {
            String cartServiceUri = UriComponentsBuilder.fromUri(CART_REQUEST_URI)
                    .queryParam("id", productId)
                    .queryParam("quantity", quantity)
                    .toUriString();

            String response = restTemplate.postForObject(cartServiceUri, null, String.class);

            cartClientSpan.event("cart service response: " + response);
        } catch (Exception e) {
            cartClientSpan.error(e);
            throw e;
        }
    }
}
