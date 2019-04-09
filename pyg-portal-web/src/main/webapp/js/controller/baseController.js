app.controller("baseController",function($scope){


$scope.reloadList=function(){
    //切换页码
    $scope.search( $scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
};
//分页控件配置
$scope.paginationConf = {
    currentPage: 1,
    totalItems: 10,
    itemsPerPage: 10,
    perPageOptions: [10, 20, 30, 40, 50],
    onChange: function(){
        $scope.reloadList();//重新加载
    }
};
//分页
$scope.findPage=function(page,rows){
    brandService.findPage(page,rows).success(
        function(response){
            $scope.list=response.rows;
            $scope.paginationConf.totalItems=response.total;//更新总记录数
        }
    );
}

//设置一个放置id的集合
$scope.selectIds=[];

//设置方法
//选择框选中与否方法
$scope.selectUpdate=function($event,id){
    if($event.target.checked){
        $scope.selectIds.push(id);
    }
    else {
        var index = $scope.selectIds.indexOf(id);
        $scope.selectIds.splice(index,1);
    }

}


//显示
    $scope.jsonToString=function (String ,key) {

        var json = JSON.parse(String);

        var value = "";

        for (var i=0 ;i<json.length;i++){

            if (i>0) {
                value +=",";
            }

            value += json[i][key];
        }

        return value;

    }



    //判断集合是否为空
    $scope.searchObjectByKey=function (list,key,keyValue) {
        for (var i=0 ;i<list.length;i++){
           if( list[i][key] == keyValue) {
               return list[i];
           }
        }
        return null;
    }

});