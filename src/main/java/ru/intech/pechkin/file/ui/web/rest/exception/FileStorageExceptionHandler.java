package ru.intech.pechkin.file.ui.web.rest.exception;

import io.minio.errors.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestControllerAdvice
public class FileStorageExceptionHandler {
    @ExceptionHandler({ErrorResponseException.class, InvalidKeyException.class})
    public ResponseEntity<String> handleErrorResponseOrInvalidKeyException() {
        return new ResponseEntity<>(
                "Данного файла не существует",
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler({InsufficientDataException.class, InternalException.class, IOException.class,
            NoSuchAlgorithmException.class, ServerException.class, XmlParserException.class})
    public ResponseEntity<String> handleMinioException() {
        return new ResponseEntity<>(
                "Ошибка при чтении или записи файла. Повторите попытку позже",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
