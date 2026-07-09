package com.trendsole.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ResourceNotFoundException - Custom Exception Class
 *
 * What it does:
 * - This exception is thrown when we try to find something in the database
 *   but it doesn't exist.
 * - Example: User searches for product with id=99, but it doesn't exist.
 *
 * @ResponseStatus(HttpStatus.NOT_FOUND)
 * - When this exception is thrown, Spring will automatically return
 *   HTTP status 404 (Not Found) to the client/browser.
 *
 * extends RuntimeException:
 * - RuntimeException means this is an "unchecked" exception.
 * - We don't need to use try-catch every time we throw it.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructor that accepts a custom error message.
     * Example usage: throw new ResourceNotFoundException("Product not found with id: 5");
     */
    public ResourceNotFoundException(String message) {
        super(message);  // Pass the message to the parent RuntimeException class
    }
}
