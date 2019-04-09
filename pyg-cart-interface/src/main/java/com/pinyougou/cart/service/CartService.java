package com.pinyougou.cart.service;

import com.pinyougou.pojogroup.Cart;

import java.util.List;

public interface CartService {

    List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num);

    //从redis查找

    List<Cart> findCartsFromRedis(String username);

    //存入redis
    void saveCartsToRedis(List<Cart> cartList,String username);

    //合并购物车

    List<Cart> mergeCarts(List<Cart> cartList1,List<Cart> cartList2);
}
