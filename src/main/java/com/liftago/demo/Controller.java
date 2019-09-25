package com.liftago.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/import")
public class Controller {

    @GetMapping
    public String get() {
        return "hellooo";
    }

}
