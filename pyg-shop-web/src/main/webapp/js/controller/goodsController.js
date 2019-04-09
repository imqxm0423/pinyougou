//控制层
app.controller('goodsController', function ($scope, $controller,$location, itemCatService, goodsService, uploadService, typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
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

    //添加
    $scope.save=function () {
        //富文本编辑器
        $scope.entity.tbGoodsDesc.introduction = editor.html();
        var object =null;
        if ($scope.entity.tbGoods.id != null){
            object =goodsService.update($scope.entity);
        }else {
            object =goodsService.add($scope.entity);
        }
        object.success(
            function(response){
                if (response.success){
                    alert("保存成功");
                  location.href='goods.html';
                }
                else {
                    alert(response.message)
                }

            }
        )
    }



    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }


    $scope.uploadFile = function () {

        uploadService.uploadFile().success(
            function (response) {
                if (response.success) {

                    $scope.image_entity.url = response.message;//设置文件地址

                } else {
                    alert(response.message);
                }
            });

    }


    $scope.entity = {tbGoods: {}, tbGoodsDesc: {itemImages: []}};


    //增加
    $scope.add_image_entity = function () {

        $scope.entity.tbGoodsDesc.itemImages.push($scope.image_entity);
    }

    //移除
    $scope.remove_image_entity = function (index) {

        $scope.entity.tbGoodsDesc.itemImages.splice(index, 1);
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


    //模板ID
    $scope.$watch('entity.tbGoods.category3Id', function (newValue, oldValue) {


        if (newValue != undefined) {
            itemCatService.findOne(newValue).success(
                function (response) {
                    $scope.entity.tbGoods.typeTemplateId = response.typeId;
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

    $scope.entity={ tbGoodsDesc:{itemImages:[],specificationItems:[]}  };
    //选中
    $scope.updateSpecAttribute=function ($event,name,value) {

        var object =$scope.searchObjectByKey($scope.entity.tbGoodsDesc.specificationItems,'attributeName',name);

        if (object != null){
            if ($event.target.checked){
                object.attributeValue.push(value);
            }else {
                object.attributeValue.splice(object.attributeValue.indexOf(value),1);
            }

            if (object.attributeValue.length==0) {
                $scope.entity.tbGoodsDesc.specificationItems.splice(
                    $scope.entity.tbGoodsDesc.specificationItems.indexOf(object),1);
            }
        }
        else {
            $scope.entity.tbGoodsDesc.specificationItems.push({'attributeName':name,'attributeValue':[value]});
        }
    }


    $scope.createItemList=function () {

        //初始值
        $scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0' } ];


        var items=  $scope.entity.tbGoodsDesc.specificationItems;
        for(var i=0;i< items.length;i++){
            $scope.entity.itemList = addColumn( $scope.entity.itemList , items[i].attributeName,items[i].attributeValue );
        }

    }

    addColumn=function (list,name,value) {

        var newList=[];//新的集合
        for (var i=0;i<list.length;i++){
            var oldRow=list[i];
            for (var j=0;j<value.length;j++){
                var newRow= JSON.parse( JSON.stringify( oldRow )  );//深克隆
                newRow.spec[name]=value[j];
                newList.push(newRow);
            }
        }

        return newList;
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


    //判断是否选中
    $scope.checkAttributeValue=function (specName,optionName) {

        var list = $scope.entity.tbGoodsDesc.specificationItems;
       var object= $scope.searchObjectByKey(list,'attributeName',specName);

       if (object==null){
           return false;
       }else {
          if( object.attributeValue.indexOf(optionName) >=0){
              return true;
          }
          else {
              return false;
          }
       }
    }



    //上架
    $scope.shangJia=function () {


        goodsService.shangjia($scope.selectIds).success(
            function (response) {
                if (response.success){
                    alert(response.message);
                    $scope.reloadList();
                    $scope.selectIds=[];
                }else {
                    alert(response.message);
                }
            }
        );
    }

    //下架
    $scope.xiaJia=function () {

        goodsService.xiajia($scope.selectIds).success(
            function (response) {
                if (response.success){
                    alert(response.message);
                    $scope.reloadList();
                    $scope.selectIds=[];
                }else {
                    alert(response.message);
                }
            }
        );
    }



});
