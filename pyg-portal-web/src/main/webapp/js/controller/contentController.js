app.controller('contentController',function ($scope, contentService) {


    $scope.contentList=[];


    $scope.findContentList=function (categoryId) {
        
        contentService.findContentList(categoryId).success(
            function (response) {

                $scope.contentList[categoryId]=response;

            }
        );
    }


    //搜索跳转

    $scope.search=function () {

        location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
    }

});