package ru.intech.pechkin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication(scanBasePackages = {"boot.registration"} , exclude = MongoDataAutoConfiguration.class)
@SpringBootApplication
public class PechkinApplication {

    public static void main(String[] args) {
        SpringApplication.run(PechkinApplication.class, args);
    }

}
