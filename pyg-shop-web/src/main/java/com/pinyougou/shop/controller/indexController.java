package com.pinyougou.shop.controller;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class indexController {


    @RequestMapping("/loginName")
    public Map loginName(){


       String loginName = SecurityContextHolder.getContext().getAuthentication().getName();

       Map map = new HashMap();

       map.put("loginName",loginName);

       return map;
    }
}
