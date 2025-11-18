package org.acme;

import jakarta.annotation.Priority;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.util.concurrent.ConcurrentMap;

@Provider
@Priority(600)
public class RateLimitResponseFilter implements ContainerResponseFilter {

    // O mesmo mapa estático usado no RateLimitFilter (simulação)
    private static final ConcurrentMap<String, RateLimitInfo> USAGE_MAP = RateLimitFilter.USAGE_MAP;

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        String clientIp = requestContext.getHeaderString("X-Forwarded-For");
        if (clientIp == null || clientIp.isBlank()) {
            clientIp = "127.0.0.1";
        }

        RateLimitInfo info = USAGE_MAP.get(clientIp);

        // Adiciona cabeçalhos apenas se a requisição não foi abortada com 429
        if (info != null && responseContext.getStatus() < 400) {
            responseContext.getHeaders().add("X-RateLimit-Limit", RateLimitInfo.LIMIT);
            responseContext.getHeaders().add("X-RateLimit-Remaining", info.remaining.get());
            responseContext.getHeaders().add("X-RateLimit-Reset", info.resetTime.getEpochSecond());
        }
        // Se a resposta foi 429, os cabeçalhos já foram adicionados no RequestFilter.
    }
}