package com.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/itemsearch")
@RestController
public class SearchController {


    @Reference
    private ItemSearchService itemSearchService;

    @RequestMapping("/search")
    public Map itemSearch(@RequestBody Map searchMap){

        Map map = itemSearchService.itemSearch(searchMap);
        return map;

    }

}
