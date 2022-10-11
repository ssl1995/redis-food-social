package com.note.ssl;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 */
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@SpringBootApplication
@MapperScan("com.note.ssl.mapper")
public class Auth2Application {

    public static void main(String[] args) {
        SpringApplication.run(Auth2Application.class, args);
    }
}
