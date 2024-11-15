package com.example.BookService.CombinedBody.exception;

public class InvalidInputException extends Exception{
    public InvalidInputException(String errorMessage) {
        super(errorMessage);
    }

    public InvalidInputException(String errorMessage, String params) {
        super(String.format(errorMessage, params));
    }
}
