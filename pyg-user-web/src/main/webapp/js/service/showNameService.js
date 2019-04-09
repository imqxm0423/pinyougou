app.service("showNameService",function ($http) {

    this.showName=function () {
        return $http.get("../showName.do");
    }

});