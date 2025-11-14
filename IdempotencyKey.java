package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

public class IdempotencyKey extends PanacheEntityBase{

    // A chave de idempotência é o identificador único
        @Id
        public String key;

        public String httpMethod;
        public String requestPath;

        // Armazena o timestamp de quando a chave foi registrada
        public Instant createdAt;

        public IdempotencyKey() {
            this.createdAt = Instant.now();
        }
    }