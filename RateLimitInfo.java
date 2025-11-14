package org.acme;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class RateLimitInfo {
    // Limite máximo de requisições por janela (ex: 1 minuto)
    public static final int LIMIT = 50;

    // Janela de tempo em segundos (ex: 60 segundos)
    public static final long WINDOW_SECONDS = 60;

    // Contador de requisições restantes na janela atual
    public AtomicInteger remaining = new AtomicInteger(LIMIT);

    // Momento em que o limite será reiniciado (Reset)
    public Instant resetTime = Instant.now().plusSeconds(WINDOW_SECONDS);
}