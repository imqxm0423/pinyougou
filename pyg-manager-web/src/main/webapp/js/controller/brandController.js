app.controller("brandController",function($scope,$controller,brandService){

    $controller('baseController',{$scope:$scope});

    //添加
    $scope.save=function () {

        var object =null;
        if ($scope.entity.id != null){
            object =brandService.update($scope.entity);
        }else {
            object =brandService.add($scope.entity);
        }
        object.success(
            function(response){
                if (response.success){
                    $scope.reloadList();//重新加载
                }
                else {
                    alert(response.message)
                }

            }
        )
    }


    //修改查找
    $scope.findOne=function (id) {
        brandService.findOne(id).success(
            function(response){
                $scope.entity = response;
            }
        )

    };




    //删除
    $scope.del=function () {
        brandService.del($scope.selectIds).success(

            function (response) {
                if (response.success){
                    $scope.reloadList();//重新加载
                }
                else {
                    alert(response.message);
                }

            }
        )

    };


    //设置一个条件查询对象
    $scope.searchEntity={};

    //设置条件查询方法
    $scope.search=function (page,rows) {
        brandService.search(page,rows,$scope.searchEntity).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );

    };




});
