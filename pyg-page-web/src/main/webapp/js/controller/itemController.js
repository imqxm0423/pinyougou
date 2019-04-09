app.controller("itemController",function($scope,$http){

	$scope.addNum=function(key){
		
		$scope.num += key;
		
		if($scope.num<1) {
			$scope.num=1;
		}
		
	};
	
	
	$scope.specSelected={};
	
	$scope.selectedChange=function(key,value){
		
		$scope.specSelected[key]=value;
		findSku();
	};
	
	$scope.isSelected=function(key,value){
		
		if($scope.specSelected[key]==value){
			return true;
		}
		else{
			return false;
		}
		
	}
	
	
	
	//加载默认SKU
	$scope.loadSku=function(){
		$scope.sku=skuList[0];		
		$scope.specSelected= JSON.parse(JSON.stringify($scope.sku.spec)) ;
	}
	
	//匹配数据内容是否相同
		matchData=function(map1,map2){
		
		for(var k in map1){
			
			if(map1[k] != map2[k]){
				return false;
			}
			
		}
		for(var k in map2){
			
			if(map2[k] != map1[k]){
				return false;
			}
			
		}
		
		return true;
		
	};
	
	
	//循环List查找是否存在SKU
	findSku=function(){
		
		for(var i = 0 ; i<skuList.length; i++){
			if (matchData(skuList[i].spec,$scope.specSelected)){
					$scope.sku=skuList[i];
					return ;
			}
			
		}
		
			$scope.sku={id:0,title:'--------',price:0};//如果没有匹配的		
	};
	
	//加入购物车
	$scope.addToCart=function(){
		
		alert('SKU ID :'+$scope.sku.id);
		$http.get("http://localhost:9107/cart/addGoodsToCart.do?itemId="+$scope.sku.id+"&num="+$scope.num,
            {'withCredentials':true}).success(
				function(response){
					if(response.success){
					    alert(1);
						location.href="http://localhost:9107/cart.html";
					}else{
						alert(response.message);
					}
				}
		);
		
	};

});
