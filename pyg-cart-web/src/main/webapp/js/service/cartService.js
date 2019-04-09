app.service("cartService",function ($http) {

    this.findCartList=function () {
        return $http.get("cart/findCartFromCookieAndRedis.do");
    };


    this.addGoodsToCart=function (itemId,num) {
        return $http.get("cart/addGoodsToCart.do?itemId="+itemId+"&num="+num);
    };

    this.findAddressList=function () {
        return $http.get("address/findAddressList.do");
    };


    this.submitOrder=function (order) {
        return $http.post("order/add.do",order);
    };

});