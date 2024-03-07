package ru.intech.pechkin.auth.ui.web.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.intech.pechkin.auth.service.exception.IllegalRegisterParameterException;
import ru.intech.pechkin.auth.service.exception.NoSuchUsernameAndPasswordException;

@RestControllerAdvice
public class AuthenticationExceptionHandler {
    @ExceptionHandler({NoSuchUsernameAndPasswordException.class, AuthenticationException.class})
    public ResponseEntity<String> handleNoSuchUsernameAndPasswordOrAuthenticationException() {
        return new ResponseEntity<>("Wrong login or password", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalRegisterParameterException.class)
    public ResponseEntity<String> handleIllegalRegisterParameterException(IllegalRegisterParameterException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }
}
