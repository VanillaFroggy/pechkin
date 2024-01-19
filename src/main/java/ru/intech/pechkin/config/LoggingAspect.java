package ru.intech.pechkin.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
    @Pointcut("execution(* ru.intech.pechkin.auth.service.AuthenticationService.*(..))")
    private void callAtAuthenticationService() {
    }

    @Pointcut("execution(* ru.intech.pechkin.messenger.infrastructure.service.ChatService.*(..))")
    private void callAtChatService() {
    }

    @Pointcut("execution(* ru.intech.pechkin.file.service.FileStorageService.*(..))")
    private void callAtFileStorageService() {
    }

    @Pointcut("execution(* ru.intech.pechkin.messenger.infrastructure.service.MessageService.*(..))")
    private void callAtMessageService() {
    }

    @Before("callAtAuthenticationService() || callAtChatService() " +
            "|| callAtFileStorageService() || callAtMessageService()")
    public void beforeServiceMethodAdvice(JoinPoint jp) {
        log.info(jp + ", args=[" + getArgs(jp) + "]");
    }

    @AfterThrowing(pointcut = "callAtAuthenticationService() || callAtChatService() " +
            "|| callAtFileStorageService() || callAtMessageService()", throwing = "exception")
    public void afterThrowingServiceAdvice(JoinPoint jp, Throwable exception) {
        log.error(jp + ", args=[" + getArgs(jp) + "]");
        log.error(exception.toString());
    }

    private String getArgs(JoinPoint jp) {
        return Arrays.stream(jp.getArgs())
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }
}
