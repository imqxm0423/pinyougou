app.service('contentService',function ($http) {

    this.findContentList=function (categoryId) {
        return $http.get('content/findContentList.do?categoryId='+categoryId);
    }

})