app.service('brandService',function ($http) {


    this.findPage=function (page,rows) {
        return $http.get('../brand/findPage.do?page='+page+'&rows='+rows);
    };

    this.add=function (entity) {
        return  $http.post('../brand/add.do',entity);
    };

    this.update=function (entity) {
        return  $http.post('../brand/update.do',entity);
    };

    this.findOne=function (id) {
        return $http.get('../brand/findOne.do?id='+id);
    };

    this.del=function (selectIds) {
        return  $http.get("../brand/delete.do?ids="+selectIds)
    };

    this.search=function (page,rows,searchEntity) {
        return $http.post('../brand/search.do?page='+page+'&rows='+rows,searchEntity)
    };

    this.findBrandList=function () {
        return $http.get('../brand/findBrandList.do')
    }
});
