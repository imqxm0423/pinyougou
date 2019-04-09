 //控制层 
app.controller('goodsController' ,function($scope,$controller ,$location,itemCatService,typeTemplateService,goodsService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

    //查询实体
    $scope.findOne = function () {
        var id = $location.search()['id'];
        if (id==null) {
            return ;
        }
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                editor.html($scope.entity.tbGoodsDesc.introduction);
                $scope.entity.tbGoodsDesc.itemImages=JSON.parse( $scope.entity.tbGoodsDesc.itemImages);
                $scope.entity.tbGoodsDesc.customAttributeItems=JSON.parse( $scope.entity.tbGoodsDesc.customAttributeItems);
                $scope.entity.tbGoodsDesc.specificationItems=JSON.parse( $scope.entity.tbGoodsDesc.specificationItems);

                for (var i=0;i< $scope.entity.itemList.length;i++){

                    $scope.entity.itemList[i].spec =JSON.parse( $scope.entity.itemList[i].spec);
                }

            }
        );
    }
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}


    $scope.status=['未审核' ,'审核通过' ,'审核未通过', '已关闭'];

    $scope.catList=[];

    $scope.findItemCatList=function () {

        itemCatService.findAll().success(
            function (response) {

                for (var i = 0 ;i<response.length;i++){
                    $scope.catList[response[i].id]=response[i].name;
                }
            });
    }


    //一级商品分类
    $scope.selectItemCat1List = function () {

        itemCatService.findByParentId(0).success(
            function (response) {

                $scope.itemCat1List = response;

            }
        );

    }


    //2级商品分类
    $scope.$watch('entity.tbGoods.category1Id', function (newValue, oldValue) {
        if (newValue != undefined){
            itemCatService.findByParentId(newValue).success(
                function (response) {
                    $scope.itemCat2List = response;
                    $scope.itemCat3List = '';
                    //   $scope.entity.tbGoods.typeTemplateId='';
                });
        }


    });


    //3级商品分类
    $scope.$watch('entity.tbGoods.category2Id', function (newValue, oldValue) {
        if (newValue != undefined ){
            itemCatService.findByParentId(newValue).success(
                function (response) {
                    $scope.itemCat3List = response;
                });
        }


    });

    //品牌列表
    $scope.$watch('entity.tbGoods.typeTemplateId', function (newValue, oldValue) {

        if(newValue != undefined){
            typeTemplateService.findOne(newValue).success(
                function (response) {

                    $scope.typeTemplates = response;

                    $scope.brandList = JSON.parse($scope.typeTemplates.brandIds);
                    //扩展模板
                    if ( $location.search()['id'] == null){
                        $scope.entity.tbGoodsDesc.customAttributeItems = JSON.parse($scope.typeTemplates.customAttributeItems);
                    }

                });

            typeTemplateService.findSpecDetailList(newValue).success(
                function (response) {
                    $scope.specDetailList=response;
                }
            );
        }



    });



    //更改状态
    $scope.updateStatus=function (status) {


        goodsService.updateStatus($scope.selectIds,status).success(
            function (response) {
                if (response.success){
                    $scope.reloadList();
                    $scope.selectIds=[];
                }else {
                    alert(response.message);
                }
            }
        );
    }



});	
