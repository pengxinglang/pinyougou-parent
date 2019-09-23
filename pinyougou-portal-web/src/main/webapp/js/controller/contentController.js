//广告控制层（运营商后台）
app.controller("contentController",function ($scope,contentService) {

    $scope.contentList=[];   //定义广告列表
    //利用数组的下标值（categoryId），来区分广告的类型
    $scope.findByCategoryId=function (categoryId) {
        contentService.findByCategoryId(categoryId).success(
            function (response) {
                $scope.contentList[categoryId]=response;
            }
        );
    }


    //跳转搜索页并且传递参数
    $scope.search=function () {
        location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
    }

});
