package org.acme;

import java.time.Instant;
import java.util.List;

public class ErrorResponse {
    public Instant timestamp = Instant.now();
    public int status;
    public String error;
    public String message;
    public List<String> details;
}