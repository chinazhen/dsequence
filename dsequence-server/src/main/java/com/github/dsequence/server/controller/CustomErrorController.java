package com.github.dsequence.server.controller;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CustomErrorController
 *
 * @author muzhi
 * @version 1.0.0 createTime: 16/26 上午7:28
 */
@RestController
public class CustomErrorController implements ErrorController {

    private static final String PATH = "/error";

    @RequestMapping(value = PATH)
    public String error() {
        return "invalid request";
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}