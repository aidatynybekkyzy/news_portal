package com.news.portal.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

public class HomeController {
    @RequestMapping("/")
    public String welcome() {
        return "index";
    }
}
