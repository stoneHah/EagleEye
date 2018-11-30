package com.stone.learn.zuulsvr.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/11/29
 **/
//@RestController
public class GlobleController {

    @GetMapping("/error")
    public Object error(){
        return Collections.singletonMap("msg","服务异常");
    }
}
