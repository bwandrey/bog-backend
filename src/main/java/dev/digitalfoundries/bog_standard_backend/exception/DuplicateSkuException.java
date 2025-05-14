package dev.digitalfoundries.bog_standard_backend.exception;

import org.springframework.dao.DataIntegrityViolationException;

public class DuplicateSkuException extends DataIntegrityViolationException {
    public DuplicateSkuException(String message, DataIntegrityViolationException e) {
        super(message, e);
    }
}
