package com.cs.ge;

import io.mongock.runner.springboot.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "https://app.zeeven.chillo.fr")
@EnableMongock
@SpringBootApplication
public class MsZeevenApplication {

    public static void main(final String[] args) {
        SpringApplication.run(MsZeevenApplication.class, args);
    }
}
