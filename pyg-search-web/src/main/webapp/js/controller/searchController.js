app.controller('searchController', function ($scope, $location,searchService) {


    //定义
    $scope.searchMap = {
        'keywords': '',
        'category': '',
        'brand': '',
        'spec': {},
        'price': '',
        'pageNo': 1,
        'pageSize': 40,
        'sort':'',
        'sortField':''
    };

    //增加搜索选项
    $scope.addSearchItem = function (key, value) {
        if (key == 'category' || key == 'brand' || key == 'price') {//如果点击的是分类或者是品牌
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }

        $scope.search();
    };


    //删除搜索选项
    $scope.removeSearchItem = function (key) {
        if (key == 'category' || key == 'brand' || key == 'price') {//如果点击的是分类或者是品牌
            $scope.searchMap[key] = '';
        } else {
            delete $scope.searchMap.spec[key];
        }

        $scope.search();
    };


    //搜索功能
    $scope.search = function () {

        //转换一下
        $scope.searchMap.pageNo = parseInt( $scope.searchMap.pageNo);

        searchService.itemSearch($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;

                $scope.buildPagesLabel();
            }
        );
    }


    //设置页码

    $scope.buildPagesLabel = function () {

        //定义页码标签
        $scope.pagesLabel = [];

        var firstPage = $scope.searchMap.pageNo;
        var lastPage = $scope.resultMap.totalPages;
           //假设都有点
         $scope.preDot=true;
         $scope.lastDot=true;

        //如果总页数大于5
        if (lastPage > 5) {

            //如果当前页码小于等于3
            if ($scope.searchMap.pageNo <= 3) {
                firstPage = 1;
                lastPage = 5;
                $scope.preDot=false;
            }
            //如果当前页码 大于等于总页数-2
           else if ($scope.searchMap.pageNo >= $scope.resultMap.totalPages - 2) {
                firstPage = $scope.resultMap.totalPages - 4;
                lastPage = $scope.resultMap.totalPages;
                $scope.lastDot=false;
            }
            else {
                firstPage =  $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;
            }
        }else {
            $scope.preDot=false;
            $scope.lastDot=false;
        }

        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pagesLabel.push(i);
        }
    }

    //页码查询
    $scope.queryByPage=function (pageNo) {
        //如果超出第一条和最后一条
        if (pageNo<1 || pageNo > $scope.resultMap.totalPages ){
            return ;
        }

        $scope.searchMap.pageNo=pageNo;
        $scope.search();
    };


    //上一页下一页 虚实 设置
    $scope.isTopPage=function () {

        if ($scope.searchMap.pageNo == 1){
            return true;
        }
        else {
            return false;
        }
    };

    $scope.isEndPage=function () {

            if ($scope.searchMap.pageNo == $scope.resultMap.totalPages){
                return true;
            }
            else {
                return false;
            }
    };


    //排序
    $scope.sortSearch=function (sortFiled,sort) {

        $scope.searchMap.sort=sort;

        $scope.searchMap.sortField=sortFiled;

        $scope.search();

    }

    //关键词是品牌的话
    $scope.keywordsIsBrand=function () {
        for (var i=0;i<$scope.resultMap.brandList.length;i++){
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text) >= 0){
                return true;
            }
        }
        return false;
    }


    //加载传递的keywords
    $scope.loadKeyWords=function () {

     $scope.searchMap.keywords =  $location.search()['keywords'];

     $scope.search();

    }

});