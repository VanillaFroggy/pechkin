package ru.intech.pechkin.auth.service.exception;

public class IllegalRegisterParameterException extends RuntimeException {
    public IllegalRegisterParameterException(String message) {
        super(message);
    }
}
