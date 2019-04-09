package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    Map itemSearch(Map searchMap);

    void importList(List list);

    void deleteListFromSolr(Long [] goodsId);
}
