app.controller("brandController",function ($scope,brandService,$controller){

    //将父类的scope变量赋值给子类,实现伪继承
    $controller("baseController",{$scope:$scope});

    $scope.findAll=function(){
        brandService.findAll().success(function (response) {
            $scope.list=response;
        })
    }



    //分页查找
    $scope.findPage=function(page,size){
        brandService.findPage(page,size).success(
            function (response) {
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        )
    }

    //新增,修改列表
    $scope.save=function () {
        var object=brandService.add($scope.entity);
        if($scope.entity.id!=null){
            object=brandService.update($scope.entity);
        }

        object.success(
            function (response) {
                if(response.success){
                    $scope.reloadList();
                }else {
                    alert(response.message);
                }
            }
        )

    }


    //查找商品
    $scope.findOne=function (id) {
        brandService.findOne(id).success(
            function (response) {
                $scope.entity=response;
            }
        )
    }

    //删除商品

    $scope.selectIds=[];   //选中的id集合
    //删除
    $scope.deleteAll=function () {
        brandService.deleteAll($scope.selectIds).success(
            function (response) {
                if(response.success){
                    $scope.reloadList();
                }
            }
        )
    }

    //条件查询
    $scope.searchEntity={};
    $scope.search=function(page,size){
        brandService.search(page,size,$scope.searchEntity).success(
            function (response) {
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        )
    }

})