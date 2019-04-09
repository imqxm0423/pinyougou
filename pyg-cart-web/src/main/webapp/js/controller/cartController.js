app.controller("cartController",function ($scope,cartService) {

    $scope.findCartList=function () {
        cartService.findCartList().success(
            function (response) {
                $scope.cartList=response;
                count();
            }
        );
    };


    //商品加减
    $scope.addNum=function (itemId,num) {
      cartService.addGoodsToCart(itemId,num).success(
          function (response) {
              if (response.success){
                  $scope.findCartList();
              }
              else {
                  alert(response.message);
              }
          }
      );
    };

    count=function () {

        $scope.totalNum=0;
        $scope.totalFee=0;

        for (var i=0; i<$scope.cartList.length; i++ ){
            for (var j=0 ; j<$scope.cartList[i].orderItemList.length; j++){

                $scope.totalNum+=$scope.cartList[i].orderItemList[j].num;
                $scope.totalFee +=$scope.cartList[i].orderItemList[j].totalFee;
            }


        }

    }



    //查找所有的收获地址
    $scope.findAddressList=function () {
        cartService.findAddressList().success(
            function (response) {
                $scope.addressList=response;
                //设置默认选中
                for (var i=0;i<$scope.addressList.length;i++){
                    if ($scope.addressList[i].isDefault == '1'){
                        $scope.selectedAddress=$scope.addressList[i];
                        break;
                    }
                }
            }
        );
    };


    //设置一个被选中
    $scope.setSelected=function (address) {
        $scope.selectedAddress=address;//设置一个被选中的地址
    };

    //判断是否选中
    $scope.isSelected=function (address) {

        if ($scope.selectedAddress == address){
            return true;
        }
        else{
            return false;
        }
    }

    //支付方式
    $scope.order={"paymentType":"1"};

    $scope.selectPayType=function(type){
        $scope.order.paymentType= type;
    };


    //提交订单
    $scope.submitOrder=function () {
        $scope.order.receiverAreaName=$scope.selectedAddress.address;//地址
        $scope.order.receiverMobile=$scope.selectedAddress.mobile;//手机
        $scope.order.receiver=$scope.selectedAddress.contact;//联系人

        cartService.submitOrder($scope.order).success(
            function (response) {
                if(response.success){
                    if ( $scope.order.paymentType =="1"){
                        location.href="pay.html";
                    }else{
                        location.href="paysuccess.html";
                    }

                }else {
                    alert(response.message);
                }

            }
        );
    }

});