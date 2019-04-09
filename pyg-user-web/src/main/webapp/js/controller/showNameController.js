app.controller("showNameController",function ($scope,showNameService) {

    //显示登录用户名
    $scope.showName=function () {
        showNameService.showName().success(
            function (response) {
                $scope.userName=response.userName;
            }
        );
    }
    
});