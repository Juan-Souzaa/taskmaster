package com.taskmaster.exception;

public class InvalidTaskStateException extends RuntimeException {
    
    public InvalidTaskStateException(String message) {
        super(message);
    }
    
    public InvalidTaskStateException(String message, Throwable cause) {
        super(message, cause);
    }
}



