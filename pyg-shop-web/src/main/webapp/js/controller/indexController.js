app.controller('indexController',function ($scope,$controller,indexService) {

    $scope.loginName=function () {
        indexService.loginName().success(
            function (response) {

                 $scope.loginName = response.loginName;

            });
    }

})