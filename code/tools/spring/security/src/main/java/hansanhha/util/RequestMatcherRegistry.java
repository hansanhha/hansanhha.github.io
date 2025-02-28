package hansanhha.util;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.firewall.FirewalledRequest;
import org.springframework.security.web.server.header.HttpHeaderWriterWebFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.DispatcherTypeRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RequestMatcherRegistry {

    List<RequestMatcher> requestMatchers;

    private RequestMatcherRegistry(List<RequestMatcher> requestMatchers) {
        this.requestMatchers = requestMatchers;
    }

    public static RequestMatcherRegistry configureAndCreate(Customizer<RequestMatcherRegistryConfigurer> configurer) {
        RequestMatcherRegistryConfigurer requestMatcherRegistryConfigurer = new RequestMatcherRegistryConfigurer(new ArrayList<>());
        configurer.customize(requestMatcherRegistryConfigurer);
        return new RequestMatcherRegistry(requestMatcherRegistryConfigurer.requestMatchers);
    }

    public RequestMatcherRegistry add(RequestMatcher requestMatcher) {
        requestMatchers.add(requestMatcher);
        return this;
    }

    public boolean anyMatches(HttpServletRequest request) {
        return isForwardRequest(request) || requestMatchers.stream().anyMatch(requestMatcher -> requestMatcher.matches(request));
    }

    private boolean isForwardRequest(HttpServletRequest request) {
        Class<? extends HttpServletRequest> type = request.getClass();

        return type.isAssignableFrom(HttpHeaderWriterWebFilter.class)
                || type.isAssignableFrom(FirewalledRequest.class)
                || request.getDispatcherType().equals(DispatcherType.FORWARD);
    }

    public static class RequestMatcherRegistryConfigurer {

        private final List<RequestMatcher> requestMatchers;

        private RequestMatcherRegistryConfigurer(List<RequestMatcher> requestMatchers) {
            this.requestMatchers = requestMatchers;
        }

        public RequestMatcherRegistryConfigurer requestMatcher(String requestMatcher) {
            this.requestMatchers.add(new AntPathRequestMatcher(requestMatcher));
            return this;
        }

        public RequestMatcherRegistryConfigurer requestMatcher(RequestMatcher requestMatcher) {
            this.requestMatchers.add(requestMatcher);
            return this;
        }

        public RequestMatcherRegistryConfigurer requestMatcher(HttpMethod httpMethod, String requestMatcher) {
            this.requestMatchers.add(new AntPathRequestMatcher(requestMatcher, httpMethod.name()));
            return this;
        }

        public RequestMatcherRegistryConfigurer requestMatcher(DispatcherTypeRequestMatcher requestMatcher) {
            this.requestMatchers.add(requestMatcher);
            return this;
        }
    }
}
