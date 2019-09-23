app.controller('searchController',function($scope,searchService,$location){

    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{},
        'price':'','pageNo':1,'pageSize':40,'sortField':'','sort':''};//搜索对象

    //搜索
    $scope.search=function(){
        $scope.searchMap.pageNo= parseInt($scope.searchMap.pageNo);
        searchService.search( $scope.searchMap ).success(
            function(response){
                $scope.resultMap=response;//搜索返回的结果
                //构建分页标签
                buildPageLabel();
            }
        );
    }

    //添加搜索项
    $scope.addSearchItem=function (key,value) {
        if(key=="category" || key=="brand" || key=="price"){//如果点击的是分类或者是品牌
            $scope.searchMap[key]=value;
        }else {
            $scope.searchMap.spec[key]=value;
        }
        $scope.search();
    }

    //移除复合搜索条件
    $scope.removeSearchItem=function(key) {
        if (key == "category" || key == "brand" || key=="price") {//如果是分类或品牌
            $scope.searchMap[key] = "";
        } else {//否则是规格
            delete $scope.searchMap.spec[key];//移除此属性
        }
        $scope.search();
    }


    //构建分页标签(totalPages为总页数)
    buildPageLabel=function(){
        $scope.maxPage=$scope.resultMap.totalPages;   //查询记录数的最大页
       // alert("maxPage=="+maxPage);
        $scope.firstPage=1; //分页栏的起始页
        $scope.lastPage=$scope.maxPage;  //分页栏的末页
        $scope.pageLabel=[];

        if($scope.searchMap.pageNo>=3){
            $scope.firstPage=$scope.searchMap.pageNo-2;
            if($scope.searchMap.pageNo<$scope.maxPage-2){
                $scope.lastPage=$scope.searchMap.pageNo+2;
            }else {
                $scope.lastPage=$scope.maxPage;
                $scope.firstPage=$scope.searchMap.pageNo-(5-($scope.maxPage-$scope.searchMap.pageNo)-1);
            }
        }else {
            $scope.lastPage=5;
        }

        for(var i=$scope.firstPage;i<=$scope.lastPage;i++){
            $scope.pageLabel.push(i);
        }
        /*alert("pageNo=="+$scope.searchMap.pageNo);
        alert("maxPage=="+maxPage);
        alert("firstPage=="+firstPage);
        alert("lastPage=="+lastPage)*/
        //alert($scope.pageLabel);

    }

    //根据页码查询
    $scope.queryByPage=function (pageNo) {
        //页码验证
        if(pageNo<1 || pageNo>$scope.resultMap.totalPages){
            return;
        }
        $scope.searchMap.pageNo=pageNo;
        $scope.search();
    }

    //判断当前页为第一页
    $scope.isTopPage=function(){
        if($scope.searchMap.pageNo==1){
            return true;
        }else{
            return false;
        }
    }

    //判断当前页是否未最后一页
    $scope.isEndPage=function(){
        if($scope.searchMap.pageNo==$scope.resultMap.totalPages){
            return true;
        }else{
            return false;
        }
    }


    //排序查询
    $scope.sortSearch=function (sortField,sort) {
        $scope.searchMap.sortField=sortField;
        $scope.searchMap.sort=sort;
        $scope.search();
    }

    //判断关键字是不是品牌
    $scope.keywordsIsBrand=function(){
        for(var i=0;i<$scope.resultMap.brandList.length;i++){
            if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){//如果包含
                return false;
            }
        }
        return true;
    }

    //加载查询字符串
    $scope.loadkeywords=function () {
        //alert(JSON.stringify($location.search()));
        //location服务自动将传入的参数以键值对的格式传入，object类型
        $scope.searchMap.keywords=$location.search()['keywords'];
        $scope.search();
    }

    });