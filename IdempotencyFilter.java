package org.acme;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import jakarta.transaction.Transactional;
import java.io.IOException;

@Provider
@Priority(500) // Define a ordem de execução do filtro
public class IdempotencyFilter implements ContainerRequestFilter {

    private static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";

    @Override
    @Transactional
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String method = requestContext.getMethod();

        // Aplica o filtro apenas para requisições POST
        if (!"POST".equalsIgnoreCase(method)) {
            return;
        }

        String idempotencyKey = requestContext.getHeaderString(IDEMPOTENCY_KEY_HEADER);

        // Se a chave não for fornecida, a requisição não é tratada como idempotente
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return;
        }

        // 1. Verificar se a chave já existe no banco
        IdempotencyKey existingKey = IdempotencyKey.findById(idempotencyKey);

        if (existingKey != null) {
            // 2. Se a chave for encontrada, retorna 409 Conflict para indicar duplicação
            // Nota: O ideal seria retornar o resultado anterior (200/201), mas para simplificar
            // a lógica inicial, retornamos 409 ou 400.

            Response conflict = Response.status(Response.Status.CONFLICT)
                    .entity("Idempotency Key already in use. Request was already processed.")
                    .build();

            requestContext.abortWith(conflict);
            return;
        }

        // 3. Se a chave não existe, registra para a requisição atual
        IdempotencyKey newKey = new IdempotencyKey();
        newKey.key = idempotencyKey;
        newKey.httpMethod = method;
        newKey.requestPath = requestContext.getUriInfo().getPath();

        // A chave é persistida (pré-processada)
        newKey.persist();

        // A requisição continua para o HunterResource para processamento real.
        // Se a transação falhar mais tarde no Resource, a chave será revertida.
    }
}