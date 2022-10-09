package com.note.ssl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class DinnersApplication {

    public static void main(String[] args) {
        SpringApplication.run(DinnersApplication.class, args);
    }
}
