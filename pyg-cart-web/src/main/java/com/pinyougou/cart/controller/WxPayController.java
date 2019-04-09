package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WxPayService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.IdWorker;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class WxPayController {

    @Reference
    private WxPayService wxPayService;

    @RequestMapping("/createNative")
    public Map createNative(){

        IdWorker idWorker = new IdWorker();


        Map map = wxPayService.createNative(idWorker.nextId() + "", "1");

        return map;

    }

    @RequestMapping("/test")
    public Map test(){
        Map map=  new HashMap();
        map.put("test","1111111111");
        return map;


    }
}
