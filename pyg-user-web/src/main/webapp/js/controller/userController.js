 //控制层 
app.controller('userController' ,function($scope,$controller   ,userService){	

    //注册
    $scope.reg=function () {

        if ($scope.password != $scope.entity.password){

            alert("两次输入不一致");
            $scope.password="";
            $scope.entity.password="";
            return ;

        }

        userService.add($scope.entity,$scope.smsCode).success(
            function (response) {
                if (response.success){
                    alert("新增成功");
                }
                else {
                    alert(response.message);
                }
            }
        );
    }


    //发送验证码
    $scope.send=function () {
        if ($scope.entity.phone == null ||$scope.entity.phone==""){
            alert("请填写正确的手机号");
            return ;
        }
        userService.send($scope.entity.phone).success(
            function (response) {
                alert(response.message);
            }
        );
    }
    
});	
