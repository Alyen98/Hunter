package org.acme;

import jakarta.annotation.Priority;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Provider
@Priority(600) // Executado após o filtro de Idempotência (500)
public class RateLimitFilter implements ContainerRequestFilter {

    // Mapa thread-safe para rastrear o uso por IP
    static final ConcurrentMap<String, RateLimitInfo> USAGE_MAP = new ConcurrentHashMap<>();

    @Override
    public void filter(ContainerRequestContext requestContext) {
        //IP vai de um proxy/load balancer (e.g., X-Forwarded-For).
        // Pra simular, usar um cabeçalho customizado ou um valor fixo se não houver.
        String clientIp = requestContext.getHeaderString("X-Forwarded-For");
        if (clientIp == null || clientIp.isBlank()) {
            clientIp = "127.0.0.1"; // Fallback para IP Local ou de Teste
        }

        // Obtém ou inicializa as informações de limite para o IP
        RateLimitInfo info = USAGE_MAP.computeIfAbsent(clientIp, k -> new RateLimitInfo());

        // 1. Verificar se a janela de tempo expirou (Time-Reset)
        if (Instant.now().isAfter(info.resetTime)) {
            // Reinicia a contagem
            info.remaining.set(RateLimitInfo.LIMIT - 1); // Consome 1 da nova janela
            info.resetTime = Instant.now().plusSeconds(RateLimitInfo.WINDOW_SECONDS);

        } else {
            // 2. Verificar se o limite foi atingido
            if (info.remaining.get() <= 0) {
                // Limite Excedido -> Retorna 429 Too Many Requests
                Response response = Response.status(429) // HTTP 429 Too Many Requests
                        .header("X-RateLimit-Limit", RateLimitInfo.LIMIT)
                        .header("X-RateLimit-Remaining", 0)
                        .header("X-RateLimit-Reset", info.resetTime.getEpochSecond())
                        .entity("Limite de requisições excedido. Tente novamente em breve.")
                        .build();

                requestContext.abortWith(response);
                return;
            }

            // Consome 1 requisição
            info.remaining.decrementAndGet();
        }
    }
}