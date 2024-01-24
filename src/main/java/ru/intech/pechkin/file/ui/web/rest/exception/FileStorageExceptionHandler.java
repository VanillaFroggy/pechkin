package ru.intech.pechkin.file.ui.web.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice
public class FileStorageExceptionHandler {
    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException() {
        return new ResponseEntity<>(
                "Ошибка при чтении или записи файла",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
