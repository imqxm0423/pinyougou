package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {


    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //根据商品Id查询到商品
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);

        if (tbItem == null) {
            throw new RuntimeException("商品不存在");
        }

        if (!tbItem.getStatus().equals("1")){
            throw  new RuntimeException("商品状态值异常");
        }
        //查找购物车是否有这个商家
        String sellerId = tbItem.getSellerId();
        Cart cart = searchCart(cartList, sellerId);
        if (cart==null){//如果说购物车不存在商家
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(tbItem.getSeller());
            //设置购物车商品
            List<TbOrderItem> orderItemList = new ArrayList<>();
            TbOrderItem tbOrderItem = createTbOrderItem(tbItem, num);
            orderItemList.add(tbOrderItem);
            cart.setOrderItemList(orderItemList);
            cartList.add(cart);
        }else {//如果说购物车存在商家
            //判断购物车是否存在该商品
            TbOrderItem orderItem = searchOrderItem(cart.getOrderItemList(), tbItem);
            if (orderItem==null){//如果商家中没有这个商品
                orderItem = createTbOrderItem(tbItem,num);
                cart.getOrderItemList().add(orderItem);
            }else {//如果商家中已经有这个商品了
                orderItem.setNum(orderItem.getNum()+num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getNum()*orderItem.getPrice().doubleValue()));

                //如果操作后数量小于0
                if (orderItem.getNum()<=0){
                    cart.getOrderItemList().remove(orderItem);
                }
                //如果商家小于0
                if (cart.getOrderItemList().size()<=0){
                    cartList.remove(cart);
                }

            }

        }
        return cartList;
    }


    private TbOrderItem searchOrderItem(List<TbOrderItem> orderItemList,TbItem item){
        for (TbOrderItem orderItem:orderItemList) {
            if (orderItem.getItemId().longValue()==item.getId().longValue()){
                return orderItem;
            }
        }
        return null;
    }

    //赋值操作
    private TbOrderItem createTbOrderItem(TbItem tbitem,Integer num){
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(tbitem.getGoodsId());
        orderItem.setItemId(tbitem.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(tbitem.getImage());
        orderItem.setPrice(tbitem.getPrice());
        orderItem.setSellerId(tbitem.getSellerId());
        orderItem.setTitle(tbitem.getTitle());
        orderItem.setTotalFee(new BigDecimal(tbitem.getPrice().doubleValue()*num));
        return orderItem;
    }


    //根据商家Id查找购物车是否存在该商家
    private Cart searchCart(List<Cart> cartList,String sellerId){

        for (Cart cart:cartList) {
            if (cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }

        return null;

    }

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 从redis中查找
     * @param username
     * @return
     */
    @Override
    public List<Cart> findCartsFromRedis(String username) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        if (cartList == null){
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    @Override
    public void saveCartsToRedis(List<Cart> cartList, String username) {

        redisTemplate.boundHashOps("cartList").put(username,cartList);
    }

    /**
     * 合并购物车
     * @param cartList1
     * @param cartList2
     * @return
     */
    @Override
    public List<Cart> mergeCarts(List<Cart> cartList1, List<Cart> cartList2) {
        for (Cart cart: cartList2) {
            for (TbOrderItem orderItem:cart.getOrderItemList()) {

               cartList1 =  addGoodsToCartList(cartList1,orderItem.getItemId(),orderItem.getNum());

            }
        }

        return cartList1;
    }
}
