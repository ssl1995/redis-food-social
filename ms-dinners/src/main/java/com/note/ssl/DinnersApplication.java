package com.note.ssl;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication()
@MapperScan("com.note.ssl.mapper")
public class DinnersApplication {

    public static void main(String[] args) {
        SpringApplication.run(DinnersApplication.class, args);
    }
}
