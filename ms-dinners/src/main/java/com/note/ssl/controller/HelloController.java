package com.note.ssl.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author SongShengLin
 * @date 2022/10/9 21:58
 * @description
 */
@RequestMapping("/hello")
@RestController
public class HelloController {

    @GetMapping
    public String test(String name) {
        return "hello " + name;
    }
}
