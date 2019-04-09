package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    @Reference
    private CartService cartService;

    @RequestMapping("/findCartFromCookieAndRedis")
    public List<Cart> findCartFromCookie(){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if (cartListString == null || cartListString.equals("")){
            cartListString = "[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);

        if (username.equals("anonymousUser")){//如果没有登陆

            return cartList_cookie;

        }else {//如果登陆了

            //获取redis的购物车信息
            List<Cart> cartList_redis = cartService.findCartsFromRedis(username);
                //如果Cookie没有购物车
            if (cartList_cookie.size() <=0 ){
                return cartList_redis;
            }else {
                //合并购物车
                List<Cart> cartList = cartService.mergeCarts(cartList_cookie, cartList_redis);

                //合并后存入redis并且清空Cookie
                cartService.saveCartsToRedis(cartList,username);
                CookieUtil.deleteCookie(request,response,"cartList");

                return cartList;
            }


        }


    }


    @RequestMapping("/addGoodsToCart")
    @CrossOrigin(origins = "http://localhost:9105")
    public Result addGoodsToCart(Long itemId,Integer num){



        try {

            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            if (username.equals("anonymousUser")){//如果没有登陆
                //从Cookie中获取原来的
                List<Cart> cartList_cookie = findCartFromCookie();
                //添加
                cartList_cookie = cartService.addGoodsToCartList(cartList_cookie, itemId, num);

                String cartListString = JSON.toJSONString(cartList_cookie);
                //放回Cookie
                CookieUtil.setCookie(request,response,"cartList",
                        cartListString,3600*24,"UTF-8");
                return  new Result(true,"添加购物车_Cookie 成功");
            }else {//如果登陆了
                    //从redis获取原来的
                List<Cart> cartList_redis = cartService.findCartsFromRedis(username);
                //添加
                cartList_redis = cartService.addGoodsToCartList(cartList_redis, itemId, num);
                //存入redis
                cartService.saveCartsToRedis(cartList_redis,username);

                return  new Result(true,"添加购物车_Redis  成功");
            }


        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加购物车失败！！！");
        }

    }

}
